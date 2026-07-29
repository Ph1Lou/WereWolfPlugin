package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.roles.renegade.RenegadeJoinCampEvent;
import fr.ph1lou.werewolfapi.events.roles.renegade.RenegadeSwapEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Role(key = RoleBase.RENEGADE,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        defaultAura = Aura.NEUTRAL,
        timers = { @Timer(key = TimerBase.RENEGADE_DISCOVERY, defaultValue = 10, meetUpValue = 10) },
        configValues = {
                @IntValue(key = IntValueBase.RENEGADE_DISTANCE, defaultValue = 20, meetUpValue = 20, step = 5, item = UniversalMaterial.COMPASS)
        })
public class Renegade extends RoleNeutral implements IPower {

    private static final float BALANCE_RATE = 1f / 50f;
    private static final double RESISTANCE_LEVELS = 0.5;
    private static final int FORCED_JOIN_EPISODE = 6;

    private enum RenegadeColor { BLUE, RED }

    private final Map<UUID, RenegadeColor> playerColors = new HashMap<>();
    private final Set<UUID> discoveredPlayers = new HashSet<>();

    private boolean initialized = false;
    private boolean blueIsVillage;
    private RenegadeColor renegadeColor;
    private float balance = 0.5f;
    private boolean joined = false;
    private Camp joinedCamp;

    private boolean power = false;
    @Nullable private IPlayerWW currentDiscoveryTarget;
    private int discoveryProgress = 0;

    public Renegade(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public void recoverPower() {
        if (!this.initialized) {
            this.initialized = true;
            this.blueIsVillage = game.getRandom().nextBoolean();
            for (IPlayerWW p : game.getPlayersWW()) {
                this.playerColors.put(p.getUUID(),
                        game.getRandom().nextBoolean() ? RenegadeColor.BLUE : RenegadeColor.RED);
            }
            this.renegadeColor = this.playerColors.get(this.getPlayerWW().getUUID());
            this.balance = 0.5f;
            setPower(false);
        }
    }

    @Override
    public void second() {
        if (!isAbilityEnabled()) return;
        if (this.joined) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        Location renegadeLoc = this.getPlayerWW().getLocation();
        IPlayerWW nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (IPlayerWW p : game.getAlivePlayersWW()) {
            if (p.equals(this.getPlayerWW())) continue;
            Location loc = p.getLocation();
            if (loc.getWorld() != renegadeLoc.getWorld()) continue;
            double dist = renegadeLoc.distance(loc);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = p;
            }
        }

        int distance = game.getConfig().getValue(IntValueBase.RENEGADE_DISTANCE);

        if (nearest == null || nearestDist > distance) {
            this.currentDiscoveryTarget = null;
            this.discoveryProgress = 0;
            return;
        }

        RenegadeColor targetColor = this.playerColors.get(nearest.getUUID());
        if (targetColor != null) {
            if (targetColor == RenegadeColor.BLUE) {
                this.balance = Math.min(1f, this.balance + BALANCE_RATE);
            } else {
                this.balance = Math.max(0f, this.balance - BALANCE_RATE);
            }
        }

        if (!this.discoveredPlayers.contains(nearest.getUUID())) {
            if (!nearest.equals(this.currentDiscoveryTarget)) {
                this.currentDiscoveryTarget = nearest;
                this.discoveryProgress = 0;
            }
            this.discoveryProgress++;
            int discoveryTime = game.getConfig().getTimerValue(TimerBase.RENEGADE_DISCOVERY);
            if (this.discoveryProgress >= discoveryTime) {
                this.discoveredPlayers.add(nearest.getUUID());
                this.currentDiscoveryTarget = null;
                this.discoveryProgress = 0;
                this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                        "werewolf.roles.renegade.discover",
                        Formatter.player(nearest.getName()),
                        Formatter.format("&color&", game.translate(
                                targetColor == RenegadeColor.BLUE
                                        ? "werewolf.roles.renegade.blue"
                                        : "werewolf.roles.renegade.red")));
            }
        }

        if (this.balance >= 1f) {
            joinCamp(this.blueIsVillage ? Camp.VILLAGER : Camp.WEREWOLF);
        } else if (this.balance <= 0f) {
            joinCamp(this.blueIsVillage ? Camp.WEREWOLF : Camp.VILLAGER);
        }
    }

    @EventHandler
    public void onDay(DayEvent event) {
        if (!isAbilityEnabled()) return;

        if (this.joined) return;

        if (this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            setPower(true);
        }

        if (event.getNumber() >= FORCED_JOIN_EPISODE) {
            if (this.balance >= 0.5f) {
                joinCamp(this.blueIsVillage ? Camp.VILLAGER : Camp.WEREWOLF);
            } else {
                joinCamp(this.blueIsVillage ? Camp.WEREWOLF : Camp.VILLAGER);
            }
        }
    }

    private void joinCamp(Camp camp) {
        if (this.joined) return;
        this.joined = true;
        this.joinedCamp = camp;

        RenegadeColor joinedColor = (camp == Camp.VILLAGER) == this.blueIsVillage
                ? RenegadeColor.BLUE : RenegadeColor.RED;

        Bukkit.getPluginManager().callEvent(new RenegadeJoinCampEvent(this.getPlayerWW(), camp));

        if (camp == Camp.VILLAGER) {
            setTransformedToVillager(true);
        } else {
            setInfected();
            Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(this.getPlayerWW()));
        }

        int oppositeCount = 0;
        for (UUID uuid : this.discoveredPlayers) {
            IPlayerWW p = game.getPlayerWW(uuid).orElse(null);
            if (p == null) continue;
            RenegadeColor color = this.playerColors.get(uuid);
            if (color == joinedColor) {
                Camp playerCamp = p.getRole().getCamp();
                if (camp == Camp.VILLAGER && playerCamp == Camp.WEREWOLF) {
                    oppositeCount++;
                } else if (camp == Camp.WEREWOLF && playerCamp == Camp.VILLAGER) {
                    oppositeCount++;
                }
            }
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.renegade.join",
                Formatter.format("&camp&", game.translate(camp.getKey())),
                Formatter.number(oppositeCount));

        game.checkVictory();
    }

    public void swap(IPlayerWW target) {
        RenegadeColor targetColor = this.playerColors.get(target.getUUID());
        RenegadeColor myColor = this.renegadeColor;
        this.playerColors.put(target.getUUID(), myColor);
        this.playerColors.put(this.getPlayerWW().getUUID(), targetColor);
        this.renegadeColor = targetColor;
    }

    public boolean hasDiscovered(UUID uuid) {
        return this.discoveredPlayers.contains(uuid);
    }

    public boolean isJoined() {
        return this.joined;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onActionBar(ActionBarEvent event) {
        if (!this.getPlayerWW().getUUID().equals(event.getPlayerUUID())) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;
        if (!isAbilityEnabled()) return;

        StringBuilder sb = event.getActionBar();

        if (this.joined) {
            sb.insert(0, game.translate("werewolf.roles.renegade.actionbar_joined",
                    Formatter.format("&camp&", game.translate(this.joinedCamp.getKey()))) + " ");
            return;
        }

        int totalBars = 20;
        int blueBars = Math.round(this.balance * totalBars);
        int redBars = totalBars - blueBars;

        StringBuilder bar = new StringBuilder();
        bar.append("§9");
        for (int i = 0; i < blueBars; i++) bar.append("=");
        bar.append("§c");
        for (int i = 0; i < redBars; i++) bar.append("=");

        sb.insert(0, bar.toString() + " ");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!this.joined) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!player.getUniqueId().equals(this.getPlayerWW().getUUID())) return;

        boolean isNight = game.isDay(Day.NIGHT);
        boolean shouldApply = (this.joinedCamp == Camp.VILLAGER && isNight)
                || (this.joinedCamp == Camp.WEREWOLF && !isNight);

        if (!shouldApply) return;

        double reduction = RESISTANCE_LEVELS * 0.20;
        event.setDamage(event.getDamage() * (1 - reduction));
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.renegade.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.RENEGADE_DISTANCE)),
                        Formatter.timer(game, TimerBase.RENEGADE_DISCOVERY)))
                .setCommand(game.translate("werewolf.roles.renegade.command"))
                .build();
    }
}

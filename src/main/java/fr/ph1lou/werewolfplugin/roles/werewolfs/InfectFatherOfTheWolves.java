package fr.ph1lou.werewolfplugin.roles.werewolfs;


import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.infect_father_of_the_wolves.CorruptionCompleteEvent;
import fr.ph1lou.werewolfapi.events.roles.infect_father_of_the_wolves.InfectionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Role(key = RoleBase.INFECT, defaultAura = Aura.DARK, category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF,
        weight = 1.5f,
        configurations = @Configuration(config = @ConfigurationBasic(key = ConfigBase.INFECT_AUTO_RESURRECTION)),
        configValues = {@IntValue(key = IntValueBase.INFECT_CORRUPTION_TIME,
                defaultValue = 500,
                meetUpValue = 500,
                step = 10,
                item = UniversalMaterial.CLOCK),
                @IntValue(key = IntValueBase.INFECT_CORRUPTION_DISTANCE,
                        defaultValue = 20,
                        meetUpValue = 20,
                        step = 5,
                        item = UniversalMaterial.COMPASS)})
public class InfectFatherOfTheWolves extends RoleWereWolf implements IAffectedPlayers, IPower {

    private static final double CORRUPTION_THRESHOLD = 100.0;

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private final Map<UUID, Double> corruption = new HashMap<>();
    private final Map<UUID, Integer> lastDisplayed = new HashMap<>();
    private boolean power = true;

    public InfectFatherOfTheWolves(WereWolfAPI api,
                                   IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    public boolean isCorrupted(UUID playerUUID) {
        return this.corruption.getOrDefault(playerUUID, 0.0) >= CORRUPTION_THRESHOLD;
    }

    @Override
    public void second() {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.isAbilityEnabled()) {
            return;
        }

        IPlayerWW self = this.getPlayerWW();
        Location selfLoc = self.getLocation();

        int corruptionTime = Math.max(1, game.getConfig().getValue(IntValueBase.INFECT_CORRUPTION_TIME));
        double corruptionDistance = game.getConfig().getValue(IntValueBase.INFECT_CORRUPTION_DISTANCE);
        double infectRate = CORRUPTION_THRESHOLD / corruptionTime;
        double wolfRate = infectRate / 4.0;

        List<IPlayerWW> wolves = game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(self))
                .filter(p -> p.getRole().isWereWolf())
                .collect(Collectors.toList());

        for (IPlayerWW p : game.getAlivePlayersWW()) {
            if (p.equals(self)) {
                continue;
            }

            double rate = 0;
            Location pLoc = p.getLocation();

            if (pLoc.getWorld() == selfLoc.getWorld() && pLoc.distance(selfLoc) < corruptionDistance) {
                rate += infectRate;
            }

            for (IPlayerWW wolf : wolves) {
                if (wolf.equals(p)) continue;
                Location wolfLoc = wolf.getLocation();
                if (pLoc.getWorld() == wolfLoc.getWorld() && pLoc.distance(wolfLoc) < corruptionDistance) {
                    rate += wolfRate;
                }
            }

            if (rate > 0) {
                double previous = this.corruption.getOrDefault(p.getUUID(), 0.0);
                double current = Math.min(previous + rate, CORRUPTION_THRESHOLD);
                this.corruption.put(p.getUUID(), current);

                if (hasPower() && previous < CORRUPTION_THRESHOLD && current >= CORRUPTION_THRESHOLD) {
                    Bukkit.getPluginManager().callEvent(new CorruptionCompleteEvent(this.getPlayerWW(), p));
                }
            }
        }

        Set<IPlayerWW> toRefresh = new HashSet<>();
        for (Map.Entry<UUID, Double> e : this.corruption.entrySet()) {
            int pct = (int) Math.min(100, Math.floor(e.getValue()));
            Integer last = this.lastDisplayed.get(e.getKey());
            if (last == null || last != pct) {
                this.lastDisplayed.put(e.getKey(), pct);
                IPlayerWW target = game.getPlayerWW(e.getKey()).orElse(null);
                if (target != null && target.isState(StatePlayer.ALIVE)) {
                    toRefresh.add(target);
                }
            }
        }
        toRefresh.forEach(target -> Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(target)));
    }

    @EventHandler(priority = EventPriority.LOW)
    @RequireAbility
    @RequireAlive
    public void onUpdatePlayerNameTag(UpdatePlayerNameTagEvent event) {

        if (!event.getTargetUUID().equals(this.getPlayerUUID())) return;

        IPlayerWW viewed = game.getPlayerWW(event.getPlayerUUID()).orElse(null);
        if (viewed == null || viewed.equals(this.getPlayerWW())) return;
        if (!viewed.isState(StatePlayer.ALIVE)) return;

        int pct = (int) Math.min(100, Math.floor(this.corruption.getOrDefault(viewed.getUUID(), 0.0)));
        if (pct <= 0) return;

        String color = pct >= 100 ? "§c" : pct >= 50 ? "§e" : "§a";
        event.setSuffix(event.getSuffix() + " " + color + pct + "%");
    }

    @EventHandler(priority = EventPriority.HIGH)
    @RequireAbility
    @RequireAlive
    public void onCorruptionComplete(CorruptionCompleteEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) return;
        if (event.isCancelled()) return;

        getPlayerWW().sendMessageWithKey(Prefix.YELLOW,
                "werewolf.roles.infect_father_of_the_wolves.corruption_complete",
                Formatter.player(event.getTargetWW().getName()));
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.infect_father_of_the_wolves.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.INFECT_CORRUPTION_TIME)),
                        Formatter.format("&distance&", game.getConfig().getValue(IntValueBase.INFECT_CORRUPTION_DISTANCE))))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .setPower(game.translate(power ? "werewolf.roles.infect_father_of_the_wolves.power_available" : "werewolf.roles.infect_father_of_the_wolves.power_not_available"))
                .setItems(game.translate("werewolf.roles.infect_father_of_the_wolves.items"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!hasPower()) {
            return;
        }

        if (!isAbilityEnabled()) {
            return;
        }

        Optional<IPlayerWW> killerWW = playerWW.getLastKiller();


        if (!killerWW.isPresent()) {
            return;
        }

        if (!killerWW.get().getRole().isWereWolf()) {
            return;
        }

        if (playerWW.equals(getPlayerWW())) {
            event.setCancelled(this.autoResurrection());
            return;
        }

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.isCorrupted(playerWW.getUUID())) {
            double cur = this.corruption.getOrDefault(playerWW.getUUID(), 0.0);
            return;
        }


        TextComponent infectMessage = VersionUtils.getVersionUtils().createClickableText(
                game.translate(
                        Prefix.YELLOW, "werewolf.roles.infect_father_of_the_wolves.infection_message",
                        Formatter.player(playerWW.getName())),
                String.format("/ww %s %s",
                        game.translate("werewolf.roles.infect_father_of_the_wolves.command"),
                        playerWW.getUUID()),
                ClickEvent.Action.RUN_COMMAND
        );

        getPlayerWW().sendMessage(infectMessage);
    }

    private boolean autoResurrection() {

        if (!game.getConfig().isConfigActive(ConfigBase.INFECT_AUTO_RESURRECTION)) {
            return false;
        }

        InfectionEvent infectionEvent =
                new InfectionEvent(this.getPlayerWW(),
                        getPlayerWW());
        Bukkit.getPluginManager().callEvent(infectionEvent);
        setPower(false);

        if (!infectionEvent.isCancelled()) {
            game.resurrection(getPlayerWW());
            return true;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");

        return false;
    }
}

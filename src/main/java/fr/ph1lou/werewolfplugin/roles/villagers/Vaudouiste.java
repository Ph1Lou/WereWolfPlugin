package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.vaudouiste.VoodooCreateDollEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Role(key = RoleBase.VAUDOUISTE,
        defaultAura = Aura.DARK,
        category = Category.VILLAGER,
        attribute = RoleAttribute.MINOR_INFORMATION,
        configValues = {
                @IntValue(key = IntValueBase.VAUDOUISTE_PROXIMITY_RANGE,
                        defaultValue = 20, meetUpValue = 20, step = 5, item = UniversalMaterial.COMPASS),
                @IntValue(key = IntValueBase.VAUDOUISTE_PROXIMITY_REQUIRED,
                        defaultValue = 120, meetUpValue = 120, step = 10, item = UniversalMaterial.CLOCK),
                @IntValue(key = IntValueBase.VAUDOUISTE_MAX_DOLLS,
                        defaultValue = 3, meetUpValue = 3, step = 1, item = UniversalMaterial.ARROW),
                @IntValue(key = IntValueBase.VAUDOUISTE_SACRIFICE_HEALTH,
                        defaultValue = 1, meetUpValue = 1, step = 1, item = UniversalMaterial.SKELETON_SKULL)
        },
        timers = @Timer(key = TimerBase.VAUDOUISTE_DOLL_DURATION,
                defaultValue = 900, meetUpValue = 900, step = 30))
public class Vaudouiste extends RoleImpl implements IAffectedPlayers {

    private final Map<IPlayerWW, Integer> proximitySeconds = new HashMap<>();
    private final Map<IPlayerWW, Integer> dolls = new HashMap<>();
    private final Deque<PendingSacrifice> pendingSacrifices = new ArrayDeque<>();

    public Vaudouiste(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        Formatter range = Formatter.format("&range&", game.getConfig().getValue(IntValueBase.VAUDOUISTE_PROXIMITY_RANGE));
        Formatter proximity = Formatter.format("&proximity&", game.getConfig().getValue(IntValueBase.VAUDOUISTE_PROXIMITY_REQUIRED));
        Formatter maxDolls = Formatter.format("&max_dolls&", game.getConfig().getValue(IntValueBase.VAUDOUISTE_MAX_DOLLS));
        Formatter duration = Formatter.format("&duration&", Utils.conversion(game.getConfig().getTimerValue(TimerBase.VAUDOUISTE_DOLL_DURATION)));
        Formatter sacrifice = Formatter.format("&sacrifice&", game.getConfig().getValue(IntValueBase.VAUDOUISTE_SACRIFICE_HEALTH));
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.vaudouiste.description",
                        range, proximity, maxDolls, duration, sacrifice))
                .setPower(game.translate("werewolf.roles.vaudouiste.power",
                        maxDolls, duration, sacrifice))
                .setCommand(game.translate("werewolf.roles.vaudouiste.commands"))
                .build();
    }

    @Override
    public void recoverPower() {
    }

    @Override
    public void second() {

        if (!this.isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        List<IPlayerWW> expired = new ArrayList<>();
        dolls.forEach((target, remaining) -> {
            int next = remaining - 1;
            if (next <= 0) {
                expired.add(target);
            } else {
                dolls.put(target, next);
            }
        });
        expired.forEach(this::removeAffectedPlayer);
        expired.forEach(target ->
                this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW,
                        "werewolf.roles.vaudouiste.doll_expired",
                        Formatter.player(target.getName())));

        int range = game.getConfig().getValue(IntValueBase.VAUDOUISTE_PROXIMITY_RANGE);
        int required = game.getConfig().getValue(IntValueBase.VAUDOUISTE_PROXIMITY_REQUIRED);
        game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(this.getPlayerWW()))
                .filter(p -> p.distance(this.getPlayerWW()) < range)
                .forEach(p -> {
                    int prev = Math.min(required, proximitySeconds.getOrDefault(p, 0));
                    if (prev >= required) return;
                    int next = prev + 1;
                    proximitySeconds.put(p, next);
                    if (next >= required) {
                        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                                "werewolf.roles.vaudouiste.proximity_reached",
                                Formatter.player(p.getName()));
                    }
                });
    }

    public void createDoll(IPlayerWW target) {

        if (dolls.containsKey(target)) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.vaudouiste.already_doll");
            return;
        }

        if (proximitySeconds.getOrDefault(target, 0) < game.getConfig().getValue(IntValueBase.VAUDOUISTE_PROXIMITY_REQUIRED)) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.vaudouiste.not_enough_proximity",
                    Formatter.player(target.getName()),
                    Formatter.format("&range&", game.getConfig().getValue(IntValueBase.VAUDOUISTE_PROXIMITY_RANGE)),
                    Formatter.format("&proximity&", game.getConfig().getValue(IntValueBase.VAUDOUISTE_PROXIMITY_REQUIRED)));
            return;
        }

        if (dolls.size() >= game.getConfig().getValue(IntValueBase.VAUDOUISTE_MAX_DOLLS)) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.vaudouiste.too_many_dolls",
                    Formatter.number(game.getConfig().getValue(IntValueBase.VAUDOUISTE_MAX_DOLLS)));
            return;
        }

        VoodooCreateDollEvent event = new VoodooCreateDollEvent(this.getPlayerWW(), target);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        this.addAffectedPlayer(target);

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                "werewolf.roles.vaudouiste.perform",
                Formatter.player(target.getName()));
    }

    public void sacrifice() {

        if (pendingSacrifices.isEmpty()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.vaudouiste.no_pending_sacrifice");
            return;
        }

        PendingSacrifice pending = pendingSacrifices.poll();

        this.getPlayerWW().addPlayerMaxHealth(-2.0 * game.getConfig().getValue(IntValueBase.VAUDOUISTE_SACRIFICE_HEALTH));

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                "werewolf.roles.vaudouiste.sacrifice_reveal",
                Formatter.player(pending.dead.getName()),
                Formatter.format("&killer&", pending.killer.getName()),
                Formatter.role(game.translate(pending.killer.getRole().getKey())),
                Formatter.number(game.getConfig().getValue(IntValueBase.VAUDOUISTE_SACRIFICE_HEALTH)));
    }

    @EventHandler(ignoreCancelled = true)
    @RequireAbility
    @RequireAlive
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        if (event.getFinalDamage() <= 0) return;

        Player player = (Player) event.getEntity();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);
        if (playerWW == null) return;
        if (!dolls.containsKey(playerWW)) return;

        int hearts = (int) Math.ceil(event.getFinalDamage() / 2.0);

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW,
                "werewolf.roles.vaudouiste.doll_damaged",
                Formatter.player(playerWW.getName()),
                Formatter.number(hearts));
    }

    @EventHandler
    @RequireAbility
    public void onFinalDeath(FinalDeathEvent event) {

        IPlayerWW dead = event.getPlayerWW();
        if (!dolls.containsKey(dead)) return;

        removeAffectedPlayer(dead);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        Optional<IPlayerWW> killer = dead.getLastKiller();
        if (!killer.isPresent()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW,
                    "werewolf.roles.vaudouiste.doll_dead_no_killer",
                    Formatter.player(dead.getName()));
            return;
        }

        pendingSacrifices.add(new PendingSacrifice(dead, killer.get()));

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                "werewolf.roles.vaudouiste.doll_dead",
                Formatter.player(dead.getName()),
                Formatter.format("&sacrifice&", game.getConfig().getValue(IntValueBase.VAUDOUISTE_SACRIFICE_HEALTH)));
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.dolls.put(iPlayerWW, game.getConfig().getTimerValue(TimerBase.VAUDOUISTE_DOLL_DURATION));
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        this.dolls.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.dolls.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return new ArrayList<>(dolls.keySet());
    }

    private static final class PendingSacrifice {
        private final IPlayerWW dead;
        private final IPlayerWW killer;

        PendingSacrifice(IPlayerWW dead, IPlayerWW killer) {
            this.dead = dead;
            this.killer = killer;
        }
    }
}

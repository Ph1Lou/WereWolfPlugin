package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.vaudouiste.VoodooCreateDollEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
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
        attribute = RoleAttribute.MINOR_INFORMATION)
public class Vaudouiste extends RoleImpl implements IAffectedPlayers {

    public static final double PROXIMITY_RANGE = 20d;
    public static final int PROXIMITY_REQUIRED = 120;
    public static final int DOLL_DURATION = 15 * 60;
    public static final int MAX_DOLLS = 3;
    public static final double SACRIFICE_HEALTH = 2d;

    private final Map<IPlayerWW, Integer> proximitySeconds = new HashMap<>();
    private final Map<IPlayerWW, Integer> dolls = new HashMap<>();
    private final Deque<PendingSacrifice> pendingSacrifices = new ArrayDeque<>();

    public Vaudouiste(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.vaudouiste.description"))
                .setPower(game.translate("werewolf.roles.vaudouiste.power"))
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

        game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(this.getPlayerWW()))
                .filter(p -> p.distance(this.getPlayerWW()) < PROXIMITY_RANGE)
                .forEach(p -> {
                    int prev = Math.min(PROXIMITY_REQUIRED, proximitySeconds.getOrDefault(p, 0));
                    if (prev >= PROXIMITY_REQUIRED) return;
                    int next = prev + 1;
                    proximitySeconds.put(p, next);
                    if (next >= PROXIMITY_REQUIRED) {
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

        if (proximitySeconds.getOrDefault(target, 0) < PROXIMITY_REQUIRED) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.vaudouiste.not_enough_proximity",
                    Formatter.player(target.getName()));
            return;
        }

        if (dolls.size() >= MAX_DOLLS) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.vaudouiste.too_many_dolls",
                    Formatter.number(MAX_DOLLS));
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

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        PendingSacrifice pending = pendingSacrifices.poll();

        this.getPlayerWW().addPlayerMaxHealth(-SACRIFICE_HEALTH);

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                "werewolf.roles.vaudouiste.sacrifice_reveal",
                Formatter.player(pending.dead.getName()),
                Formatter.format("&killer&", pending.killer.getName()),
                Formatter.role(game.translate(pending.killer.getRole().getKey())));
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {

        if (!this.isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;
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
    public void onFinalDeath(FinalDeathEvent event) {

        if (!this.isAbilityEnabled()) return;

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
                Formatter.player(dead.getName()));
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.dolls.put(iPlayerWW, DOLL_DURATION);
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

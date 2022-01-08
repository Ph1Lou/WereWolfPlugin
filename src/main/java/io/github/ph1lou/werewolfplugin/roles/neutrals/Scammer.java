package io.github.ph1lou.werewolfplugin.roles.neutrals;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import io.github.ph1lou.werewolfapi.events.roles.scammer.ScamEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleNeutral;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfplugin.roles.villagers.Villager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Héphaïsto
 */
public class Scammer extends RoleNeutral implements IAffectedPlayers, IPower {
    private final Map<IPlayerWW, Integer> affectedPlayer = new HashMap<>();
    private boolean power = true;

    public Scammer(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.scammer.description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onStart(StartEvent event) {
        BukkitUtils.scheduleSyncRepeatingTask(new BukkitRunnable() {
            @Override
            public void run() {
                if (!hasPower()) return;

                Location location = getPlayerWW().getLocation();

                Bukkit.getOnlinePlayers().stream()
                        .map(Entity::getUniqueId)
                        .filter(uniqueId -> !getPlayerUUID().equals(uniqueId))
                        .map(game::getPlayerWW)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(iPlayerWW -> iPlayerWW.isState(StatePlayer.ALIVE) && checkDistance(iPlayerWW, location))
                        .forEach(iPlayerWW -> {
                            int value = 0;
                            if (affectedPlayer.containsKey(iPlayerWW)) {
                                value = affectedPlayer.get(iPlayerWW);
                                if (value == 99) {
                                    Bukkit.getPluginManager().callEvent(new ScamEvent(getPlayerWW(), iPlayerWW));
                                    return;
                                }
                            }
                            affectedPlayer.put(iPlayerWW, value);
                        });
            }
        }, 9 * 20, 9 * 20);
    }

    @EventHandler
    public void onScam(ScamEvent event) {
        setPower(false);
        IPlayerWW target = event.getTargetWW();
        IRole targetRole = target.getRole();
        getPlayerWW().setRole(targetRole);
        if (targetRole.isCamp(Camp.WEREWOLF)) {
            target.setRole(new WhiteWereWolf(game, target, RolesBase.ALPHA_WEREWOLF.getKey()));
        } else {
            target.setRole(new Villager(game, target, RolesBase.VILLAGER.getKey()));
        }
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayer.put(iPlayerWW, 0);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayer.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayer.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return new ArrayList<>(affectedPlayer.keySet());
    }

    /**
     * Check that the given PlayerWW is within 20 blocks of the Location
     *
     * @param player   the PlayerWW
     * @param location the location to compare
     * @return true if the player is within 20 blocks of the location, false otherwise
     */
    private boolean checkDistance(IPlayerWW player, Location location) {
        return player.getLocation().getWorld() == location.getWorld() &&
                player.getLocation().distance(location) < 20;
    }

    @Override
    public void setPower(boolean b) {
        power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }
}

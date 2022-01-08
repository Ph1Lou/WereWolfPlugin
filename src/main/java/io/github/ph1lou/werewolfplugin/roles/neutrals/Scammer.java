package io.github.ph1lou.werewolfplugin.roles.neutrals;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.scammer.ScamEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleNeutral;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
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

    @Override
    public void second() {

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
                    if (affectedPlayer.containsKey(iPlayerWW)) {
                        if (affectedPlayer.get(iPlayerWW).equals(99)){
                            Bukkit.getPluginManager().callEvent(new ScamEvent(getPlayerWW(),iPlayerWW));
                            return;
                        }
                    }
                });
    }

    @EventHandler
    public void onScam(ScamEvent event) {

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

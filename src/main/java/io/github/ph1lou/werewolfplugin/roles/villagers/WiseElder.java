package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.roles.wise_elder.RevealAuraAmountEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WiseElder extends RoleVillage {

    private int neutralCounter;
    private int darkCounter;
    private int lightCounter;
    private boolean active;

    public WiseElder(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.wise_elder.description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onDay(DayEvent event) {
        if (event.getNumber() == 3)
            active = true;

        if (active) {
            getPlayerWW().sendMessageWithKey("werewolf.role.wise_elder.end_of_cycle",
                    Formatter.format("&neutral&",neutralCounter),
                    Formatter.format("&dark&",darkCounter),
                    Formatter.format("&light&",lightCounter));
            Bukkit.getPluginManager()
                    .callEvent(new RevealAuraAmountEvent(getPlayerWW(),neutralCounter,darkCounter,lightCounter));
            resetCounters();
        }
    }

    @Override
    public void second() {
        if (!active) return;

        Location location = getPlayerWW().getLocation();
        Bukkit.getOnlinePlayers().stream()
                .map(Entity::getUniqueId)
                .filter(uniqueId -> !getPlayerUUID().equals(uniqueId))
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(iPlayerWW -> iPlayerWW.isState(StatePlayer.ALIVE) && checkDistance(iPlayerWW,location))
                .map(IPlayerWW::getRole)
                .map(IRole::getAura)
                .forEach(aura -> {
                    switch (aura) {
                        case NEUTRAL:
                            neutralCounter++;
                            break;
                        case DARK:
                            darkCounter++;
                            break;
                        case LIGHT:
                            lightCounter++;
                            break;
                    }
                });
    }

    /**
     * Reset all the aura counters
     */
    private void resetCounters() {
        neutralCounter = 0;
        darkCounter = 0;
        lightCounter = 0;
    }

    /**
     * Check that the given PlayerWW is within 15 blocks of the Location
     * @param player the PlayerWW
     * @param location the location to compare
     * @return true if the player is within 15 blocks of the location, false otherwise
     */
    private boolean checkDistance(IPlayerWW player, Location location) {
        return player.getLocation().getWorld() == location.getWorld() &&
                player.getLocation().distance(location) < 15;
    }
}

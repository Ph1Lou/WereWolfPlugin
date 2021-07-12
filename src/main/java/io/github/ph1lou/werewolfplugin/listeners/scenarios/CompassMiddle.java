package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

public class CompassMiddle extends ListenerManager {

    public CompassMiddle(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (event.getNumber() != 1) return;

        Bukkit.getOnlinePlayers()
                .forEach(player -> player.setCompassTarget(player
                        .getWorld()
                        .getSpawnLocation()));
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setCompassTarget(player
                .getWorld()
                .getSpawnLocation());
    }

    @Override
    public void register(boolean isActive) {

        WereWolfAPI game = this.getGame();

        if (isActive) {
            if (!isRegister()) {
                BukkitUtils.registerEvents(this);
                Bukkit.getOnlinePlayers()
                        .forEach(player -> player.setCompassTarget(player
                                .getWorld()
                                .getSpawnLocation()));
                register = true;
            }
        } else if (isRegister()) {

            register = false;
            HandlerList.unregisterAll(this);
            Bukkit.getOnlinePlayers()
                    .forEach(player -> game.getPlayerWW(player.getUniqueId())
                            .ifPresent(playerWW -> player.setCompassTarget(playerWW.getSpawn())));

        }
    }
}

package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

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

    @Override
    public void register(boolean isActive) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (isActive) {
            if (!isRegister()) {
                Bukkit.getPluginManager().registerEvents(this, (Plugin) main);
                Bukkit.getOnlinePlayers()
                        .forEach(player -> player.setCompassTarget(player
                                .getWorld()
                                .getSpawnLocation()));
                setRegister(true);
            }
        } else if (isRegister()) {

            setRegister(false);
            HandlerList.unregisterAll(this);
            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(player -> game.getPlayerWW(player.getUniqueId()) != null)
                    .forEach(player -> player.setCompassTarget(
                            Objects.requireNonNull(game.getPlayerWW(
                                    player.getUniqueId())).getSpawn()));

        }
    }
}

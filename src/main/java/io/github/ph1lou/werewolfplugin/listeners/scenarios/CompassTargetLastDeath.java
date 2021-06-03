package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CompassTargetLastDeath extends ListenerManager {

    public CompassTargetLastDeath(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        WereWolfAPI game = this.getGame();

        if (!game.getPlayerWW(event.getEntity().getUniqueId()).isPresent()) return;

        Bukkit.getOnlinePlayers()
                .forEach(player -> player.setCompassTarget(event.getEntity().getLocation()));
    }

}
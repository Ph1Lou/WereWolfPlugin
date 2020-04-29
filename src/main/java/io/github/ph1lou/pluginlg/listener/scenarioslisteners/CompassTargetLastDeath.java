package io.github.ph1lou.pluginlg.listener.scenarioslisteners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CompassTargetLastDeath extends Scenarios {

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        if (!game.playerLG.containsKey(event.getEntity().getName())) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setCompassTarget(event.getEntity().getLocation());
        }
    }


}

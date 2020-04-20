package io.github.ph1lou.pluginlg.listener.scenarioslistener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class NoCleanUp extends Scenarios {

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        try {
            Player killer = event.getEntity().getKiller();
            killer.setHealth(Math.min(killer.getHealth() + 4, killer.getMaxHealth()));
        } catch (Exception ignored) {
        }
    }
}

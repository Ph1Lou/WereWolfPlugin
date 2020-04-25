package io.github.ph1lou.pluginlg.listener.gamelisteners.scenarioslisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoFall extends Scenarios {


    @EventHandler
    private void onPlayerFall(EntityDamageEvent event) {

        if(!event.getEntity().getWorld().equals(game.getWorld())) return;

        if (!(event.getEntity() instanceof Player)) return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
        }
    }

}

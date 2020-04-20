package io.github.ph1lou.pluginlg.listener.scenarioslistener;

import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class NoSnowBall extends Scenarios {


    @EventHandler
    public void onProjectileThrownEvent(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Snowball) {
            event.setCancelled(true);
        }
    }

}

package io.github.ph1lou.pluginlg.listener.scenarioslisteners;

import org.bukkit.entity.Egg;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class NoEggSnowBall extends Scenarios {


    @EventHandler
    public void onProjectileThrownEvent(ProjectileLaunchEvent event) {

        if (event.getEntity() instanceof Snowball || event.getEntity() instanceof Egg) {
            event.setCancelled(true);
        }
    }

}

package io.github.ph1lou.pluginlg.listener.scenarioslistener;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityMountEvent;

public class HorseLess extends Scenarios {


    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        if (event.getEntity() != null && event.getEntity() instanceof Player && event.getMount() != null && event.getMount() instanceof Horse) {
            event.setCancelled(true);
        }
    }
}

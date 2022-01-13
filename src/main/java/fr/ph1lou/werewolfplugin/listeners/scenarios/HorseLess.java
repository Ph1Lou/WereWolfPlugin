package fr.ph1lou.werewolfplugin.listeners.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityMountEvent;

public class HorseLess extends ListenerManager {


    public HorseLess(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {

        if (event.getEntity() instanceof Player) {
            if (event.getMount() instanceof Horse) {
                event.setCancelled(true);
            }
        }
    }
}

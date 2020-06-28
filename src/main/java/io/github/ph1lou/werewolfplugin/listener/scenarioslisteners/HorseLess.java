package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityMountEvent;

public class HorseLess extends Scenarios {


    public HorseLess(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,key);
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {

        if (event.getEntity() != null && event.getEntity() instanceof Player && event.getMount() != null && event.getMount() instanceof Horse) {
            event.setCancelled(true);
        }
    }
}

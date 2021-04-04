package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoFall extends ListenerManager {


    public NoFall(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerFall(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
        }
    }

}

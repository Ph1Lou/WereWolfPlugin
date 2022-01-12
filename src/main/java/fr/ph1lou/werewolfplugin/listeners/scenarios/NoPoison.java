package fr.ph1lou.werewolfplugin.listeners.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoPoison extends ListenerManager {


    public NoPoison(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.POISON)) {
            event.setCancelled(true);
        }
    }

}

package fr.ph1lou.werewolfplugin.listeners.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class SafeMiner extends ListenerManager {
    public SafeMiner(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){

        if(getGame().getConfig().getTimerValue(TimerBase.DIGGING.getKey()) <= 0){
            return;
        }
        if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
            return;
        }

        if(event.getEntity() instanceof Player){
            int y = event.getEntity().getLocation().getBlockY();
            if(y> 0 && y < 30){
                event.setCancelled(true);
            }
        }
    }
}

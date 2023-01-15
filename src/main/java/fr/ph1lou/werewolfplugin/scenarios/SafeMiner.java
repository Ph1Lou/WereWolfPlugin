package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

@Scenario(key = ScenarioBase.SAFE_MINER)
public class SafeMiner extends ListenerWerewolf {

    public SafeMiner(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (getGame().getConfig().getTimerValue(TimerBase.DIGGING) <= 0) {
            return;
        }
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            int y = event.getEntity().getLocation().getBlockY();
            if (y > 0 && y < 30) {
                event.setCancelled(true);
            }
        }
    }
}

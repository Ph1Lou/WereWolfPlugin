package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

@Scenario(key = ScenarioBase.FIRE_LESS, defaultValue = true, meetUpValue = true)
public class FireLess extends ListenerWerewolf {

    public FireLess(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
            event.setCancelled(true);
        }

    }
}

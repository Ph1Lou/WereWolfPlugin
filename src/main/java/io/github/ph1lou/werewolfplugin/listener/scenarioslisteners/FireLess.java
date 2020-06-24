package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class FireLess extends Scenarios {


    public FireLess(Main main, GameManager game, ScenarioLG fireLess) {
        super(main, game,fireLess);
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
            event.setCancelled(true);
        }

    }
}

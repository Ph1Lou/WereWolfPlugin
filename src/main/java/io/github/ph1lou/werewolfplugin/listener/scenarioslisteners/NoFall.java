package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoFall extends Scenarios {


    public NoFall(Main main, GameManager game, ScenarioLG noFall) {
        super(main, game,noFall);
    }

    @EventHandler
    private void onPlayerFall(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
        }
    }

}

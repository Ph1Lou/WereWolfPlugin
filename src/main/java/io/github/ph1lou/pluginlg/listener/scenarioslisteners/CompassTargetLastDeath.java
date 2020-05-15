package io.github.ph1lou.pluginlg.listener.scenarioslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CompassTargetLastDeath extends Scenarios {

    public CompassTargetLastDeath(MainLG main, GameManager game, ScenarioLG compassTargetLastDeath) {
        super(main, game,  compassTargetLastDeath);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        if (!game.playerLG.containsKey(event.getEntity().getUniqueId())) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setCompassTarget(event.getEntity().getLocation());
        }
    }


}

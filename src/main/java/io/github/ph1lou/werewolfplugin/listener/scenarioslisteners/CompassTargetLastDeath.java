package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CompassTargetLastDeath extends Scenarios {

    public CompassTargetLastDeath(Main main, GameManager game, ScenarioLG compassTargetLastDeath) {
        super(main, game,  compassTargetLastDeath);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        if (!game.getPlayersWW().containsKey(event.getEntity().getUniqueId())) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setCompassTarget(event.getEntity().getLocation());
        }
    }


}

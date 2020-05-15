package io.github.ph1lou.pluginlg.listener.scenarioslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

public class NoCleanUp extends Scenarios {

    public NoCleanUp(MainLG main, GameManager game, ScenarioLG noCleanUp) {
        super(main, game,noCleanUp);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerDeath(PlayerDeathEvent event) {

        try {
            Player killer = event.getEntity().getKiller();
            killer.setHealth(Math.min(killer.getHealth() + 4, killer.getMaxHealth()));
        } catch (Exception ignored) {
        }
    }
}

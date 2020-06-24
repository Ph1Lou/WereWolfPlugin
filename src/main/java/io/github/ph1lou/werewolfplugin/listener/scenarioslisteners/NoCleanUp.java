package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

public class NoCleanUp extends Scenarios {

    public NoCleanUp(Main main, GameManager game, ScenarioLG noCleanUp) {
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

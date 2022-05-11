package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;


@Scenario(key = ScenarioBase.COMPASS_TARGET_LAST_DEATH,
        incompatibleScenarios = ScenarioBase.COMPASS_MIDDLE)
public class CompassTargetLastDeath extends ListenerManager {

    public CompassTargetLastDeath(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        WereWolfAPI game = this.getGame();

        if (!game.getPlayerWW(event.getEntity().getUniqueId()).isPresent()) return;

        Bukkit.getOnlinePlayers()
                .forEach(player -> player.setCompassTarget(event.getEntity().getLocation()));
    }

}
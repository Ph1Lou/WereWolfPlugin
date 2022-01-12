package fr.ph1lou.werewolfplugin.listeners.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

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
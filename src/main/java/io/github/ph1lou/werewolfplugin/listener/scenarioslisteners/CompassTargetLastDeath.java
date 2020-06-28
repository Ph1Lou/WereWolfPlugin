package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CompassTargetLastDeath extends Scenarios {

    public CompassTargetLastDeath(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,  key);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        if (!game.getPlayersWW().containsKey(event.getEntity().getUniqueId())) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setCompassTarget(event.getEntity().getLocation());
        }
    }


}

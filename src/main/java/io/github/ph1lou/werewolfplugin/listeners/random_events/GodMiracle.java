package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.GodMiracleEvent;
import io.github.ph1lou.werewolfapi.events.ThirdDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class GodMiracle extends ListenerManager {


    public GodMiracle(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(ThirdDeathEvent event) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (event.isCancelled()) return;

        if (game.getRandom().nextFloat() * 5 < 1) {

            PlayerWW playerWW = event.getPlayerWW();
            GodMiracleEvent godMiracle = new GodMiracleEvent(playerWW);
            Bukkit.getPluginManager().callEvent(godMiracle);

            if (godMiracle.isCancelled()) return;

            event.setCancelled(true);
            game.resurrection(playerWW);
            Bukkit.broadcastMessage(game.translate("werewolf.random_events.god_miracle.message"));
        }
    }

}

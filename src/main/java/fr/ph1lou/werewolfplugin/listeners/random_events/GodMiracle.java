package fr.ph1lou.werewolfplugin.listeners.random_events;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import fr.ph1lou.werewolfapi.events.random_events.GodMiracleEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class GodMiracle extends ListenerManager {


    public GodMiracle(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(ThirdDeathEvent event) {

        WereWolfAPI game = this.getGame();

        if (event.isCancelled()) return;

        if (game.getRandom().nextFloat() * 5 < 1) {

            IPlayerWW playerWW = event.getPlayerWW();
            GodMiracleEvent godMiracle = new GodMiracleEvent(playerWW);
            Bukkit.getPluginManager().callEvent(godMiracle);

            if (godMiracle.isCancelled()) return;

            event.setCancelled(true);
            register(false);
            game.resurrection(playerWW);
            Bukkit.broadcastMessage(game.translate("werewolf.random_events.god_miracle.message"));
        }
    }

}

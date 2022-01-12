package fr.ph1lou.werewolfplugin.listeners.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

public class NoEnd extends ListenerManager {


    public NoEnd(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {

        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            event.setCancelled(true);
        }
    }
}

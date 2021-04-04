package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

public class NoNether extends ListenerManager {


    public NoNether(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {

        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            event.setCancelled(true);
        }
    }
}

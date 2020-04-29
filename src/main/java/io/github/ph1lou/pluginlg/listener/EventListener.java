package io.github.ph1lou.pluginlg.listener;


import io.github.ph1lou.pluginlg.game.GameManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;


public class EventListener implements Listener {

    final GameManager game;

    public EventListener(GameManager game) {
        this.game = game;
    }

    @EventHandler
    private void catchChestOpen(InventoryOpenEvent event) {

        if (!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
        if (event.getInventory().getType().equals(InventoryType.CHEST)) {
            if (event.getInventory().getHolder() instanceof Chest) {
                Location location = ((Chest) event.getInventory().getHolder()).getLocation();
                if (game.eventslg.chest_location.contains(location)) {
                    game.eventslg.chest_has_been_open.put(location, true);
                }
            }
        }
    }


}

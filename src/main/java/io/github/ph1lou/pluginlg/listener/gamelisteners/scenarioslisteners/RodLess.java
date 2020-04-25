package io.github.ph1lou.pluginlg.listener.gamelisteners.scenarioslisteners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class RodLess extends Scenarios {


    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {

        if(!event.getViewers().isEmpty() && !event.getViewers().get(0).getWorld().equals(game.getWorld())) return;

        final CraftingInventory inv = event.getInventory();

        if(inv.getResult()==null) return;

        if (inv.getResult().getType().equals(Material.FISHING_ROD)) {
            inv.setResult(new ItemStack(Material.AIR));
        }
    }
}

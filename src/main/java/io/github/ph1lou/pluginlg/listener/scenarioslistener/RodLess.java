package io.github.ph1lou.pluginlg.listener.scenarioslistener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class RodLess extends Scenarios {


    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {

        final CraftingInventory inv = event.getInventory();
        final ItemStack AIR = new ItemStack(Material.AIR);
        if (inv.getResult().getType().equals(Material.FISHING_ROD)) {
            inv.setResult(AIR);
        }
    }
}

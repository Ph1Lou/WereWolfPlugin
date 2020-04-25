package io.github.ph1lou.pluginlg.listener.gamelisteners.scenarioslisteners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class HasteyBoys extends Scenarios {


    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {

        if(!event.getViewers().isEmpty() && !event.getViewers().get(0).getWorld().equals(game.getWorld())) return;

        if (event.getInventory().getResult() == null) return;

        Material itemType = event.getInventory().getResult().getType();

        if (itemType != Material.WOOD_HOE && itemType != Material.STONE_HOE && itemType != Material.GOLD_HOE && itemType != Material.IRON_HOE && itemType != Material.DIAMOND_HOE && itemType != Material.WOOD_AXE && itemType != Material.WOOD_PICKAXE && itemType != Material.WOOD_SPADE && itemType != Material.GOLD_AXE && itemType != Material.GOLD_PICKAXE && itemType != Material.GOLD_SPADE && itemType != Material.STONE_AXE && itemType != Material.STONE_PICKAXE && itemType != Material.STONE_SPADE && itemType != Material.IRON_AXE && itemType != Material.IRON_PICKAXE && itemType != Material.IRON_SPADE && itemType != Material.DIAMOND_AXE && itemType != Material.DIAMOND_PICKAXE && itemType != Material.DIAMOND_SPADE) {
            return;
        }
        ItemStack item = new ItemStack(itemType);
        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 2);
        event.getInventory().setResult(item);
    }
}

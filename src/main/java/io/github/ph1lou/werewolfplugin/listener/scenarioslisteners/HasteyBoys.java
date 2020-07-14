package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class HasteyBoys extends Scenarios {


    public HasteyBoys(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,key);
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {

        if (event.getInventory().getResult() == null) return;

        Material itemType = event.getInventory().getResult().getType();

        if (itemType != Material.WOODEN_HOE && itemType != Material.STONE_HOE && itemType != Material.GOLDEN_HOE && itemType != Material.IRON_HOE && itemType != Material.DIAMOND_HOE && itemType != Material.WOODEN_AXE && itemType != Material.WOODEN_PICKAXE && itemType != Material.WOODEN_SHOVEL && itemType != Material.GOLDEN_AXE && itemType != Material.GOLDEN_PICKAXE && itemType != Material.GOLDEN_SHOVEL && itemType != Material.STONE_AXE && itemType != Material.STONE_PICKAXE && itemType != Material.STONE_SHOVEL && itemType != Material.IRON_AXE && itemType != Material.IRON_PICKAXE && itemType != Material.IRON_SHOVEL && itemType != Material.DIAMOND_AXE && itemType != Material.DIAMOND_PICKAXE && itemType != Material.DIAMOND_SHOVEL) {
            return;
        }
        ItemStack item = new ItemStack(itemType);
        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 2);
        event.getInventory().setResult(item);
    }
}

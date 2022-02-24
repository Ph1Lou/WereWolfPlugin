package fr.ph1lou.werewolfplugin.listeners.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class HasteyBabies extends ListenerManager {
    public HasteyBabies(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {

        if (event.getInventory().getResult() == null) return;

        Material itemType = event.getInventory().getResult().getType();

        if (itemType != Material.DIAMOND_HOE &&
                itemType != UniversalMaterial.WOODEN_AXE.getType() &&
                itemType != UniversalMaterial.WOODEN_PICKAXE.getType() &&
                itemType != UniversalMaterial.WOODEN_SHOVEL.getType() &&
                itemType != UniversalMaterial.GOLDEN_AXE.getType() &&
                itemType != UniversalMaterial.GOLDEN_PICKAXE.getType() &&
                itemType != UniversalMaterial.GOLDEN_SHOVEL.getType() &&
                itemType != Material.STONE_AXE &&
                itemType != Material.STONE_PICKAXE &&
                itemType != UniversalMaterial.STONE_SHOVEL.getType() &&
                itemType != Material.IRON_AXE &&
                itemType != Material.IRON_PICKAXE &&
                itemType != UniversalMaterial.IRON_SHOVEL.getType() &&
                itemType != Material.DIAMOND_AXE &&
                itemType != Material.DIAMOND_PICKAXE &&
                itemType != UniversalMaterial.DIAMOND_SHOVEL.getType()) {
            return;
        }
        ItemStack item = new ItemStack(itemType);
        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        event.getInventory().setResult(item);
    }

}

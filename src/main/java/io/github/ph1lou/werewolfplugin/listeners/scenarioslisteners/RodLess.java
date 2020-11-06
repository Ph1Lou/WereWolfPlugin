package io.github.ph1lou.werewolfplugin.listeners.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class RodLess extends Scenarios {


    public RodLess(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,key);
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {

        CraftingInventory inv = event.getInventory();

        if(inv.getResult()==null) return;

        if (inv.getResult().getType().equals(Material.FISHING_ROD)) {
            inv.setResult(new ItemStack(Material.AIR));
        }
    }
}

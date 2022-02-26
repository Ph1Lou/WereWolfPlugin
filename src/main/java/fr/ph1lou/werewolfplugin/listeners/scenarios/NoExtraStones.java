package fr.ph1lou.werewolfplugin.listeners.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class NoExtraStones extends ListenerManager {
    public NoExtraStones(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        List<ItemStack> itemStacks = Arrays.asList(UniversalMaterial.ANDESITE.getStack(),
                UniversalMaterial.DIORITE.getStack(),
                UniversalMaterial.GRANITE.getStack());

        if(event
                .getBlock()
                .getDrops()
                .stream()
                .anyMatch(itemStack -> itemStacks.stream()
                        .anyMatch(itemStack1 -> itemStack1.isSimilar(itemStack)))){
            block.setType(Material.AIR);
            Location loc = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY() + 0.5, block.getLocation().getBlockZ() + 0.5);
            block.getWorld().dropItem(loc, new ItemStack(Material.STONE, 1));
        }
    }

}

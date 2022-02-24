package fr.ph1lou.werewolfplugin.listeners.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.List;

public class NoExtraStones extends ListenerManager {
    public NoExtraStones(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        List<Material> materials = Arrays.asList(Material.ANDESITE,
                Material.DIORITE,
                Material.GRANITE);

        if (materials.contains(block.getType())) {
            block.setType(Material.COBBLESTONE);
        }
    }

}

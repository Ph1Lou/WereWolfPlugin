package io.github.ph1lou.pluginlg.listener.scenarioslisteners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Timber extends Scenarios {

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {

        final Player player = event.getPlayer();
        final Material mat = event.getBlock().getType();
        if (mat == Material.LOG || mat == Material.LOG_2) {
            final List<Block> bList = new ArrayList<>();
            final List<ItemStack> finalItems = new ArrayList<>();
            bList.add(event.getBlock());
            new BukkitRunnable() {
                public void run() {
                    for (int i = 0; i < bList.size(); ++i) {
                        final Block block = bList.get(i);

                        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
                            final List<ItemStack> items = new ArrayList<>(block.getDrops());
                            block.setType(Material.AIR);
                            finalItems.addAll(items);
                        }
                        BlockFace[] values;
                        for (int length = (values = BlockFace.values()).length, j = 0; j < length; ++j) {
                            final BlockFace face = values[j];
                            if (block.getRelative(face).getType() == Material.LOG || block.getRelative(face).getType() == Material.LOG_2) {
                                bList.add(block.getRelative(face));
                            }
                        }
                        bList.remove(block);
                    }
                    if (bList.size() == 0) {
                        for (final ItemStack item2 : finalItems) {
                            player.getWorld().dropItemNaturally(event.getBlock().getLocation(), item2);
                        }
                        this.cancel();
                    }
                }
            }.runTaskTimer(main, 1L, 1L);
        }
    }
}

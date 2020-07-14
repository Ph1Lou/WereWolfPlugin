package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Timber extends Scenarios {

    public Timber(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,key);
    }


    boolean isLog(Material material){
        List<Material> logs = Arrays.asList(Material.ACACIA_LOG,Material.BIRCH_LOG,Material.DARK_OAK_LOG,Material.JUNGLE_LOG,Material.OAK_LOG,Material.SPRUCE_LOG);
        return logs.contains(material);
    }

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {

        final Player player = event.getPlayer();
        final Material mat = event.getBlock().getType();
        if (isLog(mat)) {
            final List<Block> bList = new ArrayList<>();
            final List<ItemStack> finalItems = new ArrayList<>();
            bList.add(event.getBlock());
            new BukkitRunnable() {
                public void run() {
                    for (int i = 0; i < bList.size(); ++i) {
                        final Block block = bList.get(i);

                        if (isLog(block.getType())) {
                            final List<ItemStack> items = new ArrayList<>(block.getDrops());
                            block.setType(Material.AIR);
                            finalItems.addAll(items);
                        }
                        BlockFace[] values;
                        for (int length = (values = BlockFace.values()).length, j = 0; j < length; ++j) {
                            final BlockFace face = values[j];
                            if (isLog(block.getRelative(face).getType())) {
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
            }.runTaskTimer((Plugin) main, 1L, 1L);
        }
    }
}

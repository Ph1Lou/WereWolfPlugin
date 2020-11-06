package io.github.ph1lou.werewolfplugin.listeners.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
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
import java.util.List;

public class Timber extends Scenarios {

    public Timber(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,key);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        Material mat = event.getBlock().getType();
        if (UniversalMaterial.isLog(mat)) {
            List<Block> bList = new ArrayList<>();
            List<ItemStack> finalItems = new ArrayList<>();
            bList.add(event.getBlock());
            new BukkitRunnable() {
                public void run() {
                    for (int i = 0; i < bList.size(); ++i) {
                        Block block = bList.get(i);

                        if (UniversalMaterial.isLog(block.getType())) {
                            List<ItemStack> items = new ArrayList<>(block.getDrops());
                            block.setType(Material.AIR);
                            finalItems.addAll(items);
                        }
                        BlockFace[] values;
                        for (int length = (values = BlockFace.values()).length, j = 0; j < length; ++j) {
                            BlockFace face = values[j];
                            if (UniversalMaterial.isLog(block.getRelative(face).getType())) {
                                bList.add(block.getRelative(face));
                            }
                        }
                        bList.remove(block);
                    }
                    if (bList.size() == 0) {
                        for (ItemStack item2 : finalItems) {
                            player.getWorld().dropItemNaturally(event.getBlock().getLocation(), item2);
                        }
                        this.cancel();
                    }
                }
            }.runTaskTimer((Plugin) main, 1L, 1L);
        }
    }
}

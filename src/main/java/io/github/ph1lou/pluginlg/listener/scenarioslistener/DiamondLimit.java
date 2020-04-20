package io.github.ph1lou.pluginlg.listener.scenarioslistener;

import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DiamondLimit extends Scenarios {

    final Map<String, Integer> diamondPerPlayer = new HashMap<>();


    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {

        if (main.isState(StateLG.LOBBY)) return;

        String playerName = event.getPlayer().getName();
        Block block = event.getBlock();

        if (!block.getType().equals(Material.DIAMOND_ORE)) return;

        final Location loc = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY() + 0.5, block.getLocation().getBlockZ() + 0.5);

        if (main.config.timerValues.get(TimerLG.DIGGING) < 0) {
            block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(event.getExpToDrop());
            block.setType(Material.AIR);
            return;
        }

        if (!event.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_PICKAXE) && !event.getPlayer().getItemInHand().getType().equals(Material.IRON_PICKAXE)) {
            return;
        }
        if (diamondPerPlayer.getOrDefault(playerName, 0) >= main.config.getDiamondLimit()) {
            block.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT, 1));
            block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(event.getExpToDrop());
            block.setType(Material.AIR);
        }
        diamondPerPlayer.put(playerName, diamondPerPlayer.getOrDefault(playerName, 0) + 1);
    }
}

package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

public class VanillaPlus extends Scenarios {


    public VanillaPlus(Main main, GameManager game, ScenarioLG scenario) {
        super(main, game, scenario);
    }

    @EventHandler
    private void onGravelBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        final Location loc = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY() + 0.5, block.getLocation().getBlockZ() + 0.5);

        if (block.getType().equals(Material.GRAVEL)) {
            block.setType(Material.AIR);
            if (Math.random() * 100 < game.getConfig().getFlintRate()) {
                block.getWorld().dropItem(loc, new ItemStack(Material.FLINT, 1));
            } else
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.GRAVEL));
        }
    }

    @EventHandler
    public void onLeaveDecay(LeavesDecayEvent event) {

        event.getBlock().setType(Material.AIR);
        if (Math.random() * 100 < game.getConfig().getAppleRate()) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
        }
    }
}

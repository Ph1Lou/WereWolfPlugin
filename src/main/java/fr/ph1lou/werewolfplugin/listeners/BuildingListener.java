package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuildingListener implements Listener {

    private final WereWolfAPI game;

    public BuildingListener(WereWolfAPI game) {
        this.game = game;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (game.getState() != StateGame.GAME) {
            return;
        }
        Block block = event.getBlock();
        if (game.getBuildingsManager().isProtected(block.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (game.getState() != StateGame.GAME) {
            return;
        }
        Block block = event.getBlock();
        if (game.getBuildingsManager().isProtected(block.getLocation())) {
            event.setCancelled(true);
        }
    }
}

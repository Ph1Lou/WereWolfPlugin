package io.github.ph1lou.pluginlg.listener.gamelisteners.scenarioslisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExpEvent;

public class XpBoost extends Scenarios {


    @EventHandler
    public void onXp(BlockExpEvent event) {
        if(!event.getBlock().getWorld().equals(game.getWorld())) return;
        event.setExpToDrop((int) (event.getExpToDrop() * game.config.getXpBoost() / 100f));
    }
}

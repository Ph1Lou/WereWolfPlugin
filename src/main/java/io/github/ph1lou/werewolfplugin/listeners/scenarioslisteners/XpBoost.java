package io.github.ph1lou.werewolfplugin.listeners.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExpEvent;

public class XpBoost extends Scenarios {


    public XpBoost(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,key);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onXp(BlockExpEvent event) {

        event.setExpToDrop((int) (event.getExpToDrop() * game.getConfig().getXpBoost() / 100f));
    }
}

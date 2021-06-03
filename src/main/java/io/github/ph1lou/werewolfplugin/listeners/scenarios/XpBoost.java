package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExpEvent;

public class XpBoost extends ListenerManager {


    public XpBoost(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onXp(BlockExpEvent event) {
        WereWolfAPI game = this.getGame();
        event.setExpToDrop((int) (event.getExpToDrop() * game.getConfig().getXpBoost() / 100f));
    }
}

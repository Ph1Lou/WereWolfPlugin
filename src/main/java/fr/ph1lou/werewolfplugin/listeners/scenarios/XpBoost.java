package fr.ph1lou.werewolfplugin.listeners.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
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

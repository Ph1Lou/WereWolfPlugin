package io.github.ph1lou.pluginlg.listener.scenarioslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExpEvent;

public class XpBoost extends Scenarios {


    public XpBoost(MainLG main, GameManager game, ScenarioLG xpBoost) {
        super(main, game,xpBoost);
    }

    @EventHandler
    public void onXp(BlockExpEvent event) {

        event.setExpToDrop((int) (event.getExpToDrop() * game.config.getXpBoost() / 100f));
    }
}

package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExpEvent;

public class XpBoost extends Scenarios {


    public XpBoost(Main main, GameManager game, ScenarioLG xpBoost) {
        super(main, game,xpBoost);
    }

    @EventHandler
    public void onXp(BlockExpEvent event) {

        event.setExpToDrop((int) (event.getExpToDrop() * game.getConfig().getXpBoost() / 100f));
    }
}

package io.github.ph1lou.pluginlg.listener.gamelisteners.scenarioslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Scenarios implements Listener {
    MainLG main;
    GameManager game;
    boolean register = false;

    public void init(MainLG main,GameManager game) {
        this.main = main;
        this.game=game;
    }

    public void register(ScenarioLG scenario) {
        if (game.config.scenarioValues.get(scenario)) {
            if (!register) {
                Bukkit.getPluginManager().registerEvents(this, main);
                register = true;
            }
        } else {
            if (register) {
                HandlerList.unregisterAll(this);
                register = false;
            }
        }
    }
}

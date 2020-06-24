package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Scenarios implements Listener {
    final Main main;
    final GameManager game;
    final ScenarioLG scenario;
    boolean register = false;

    public Scenarios(Main main, GameManager game, ScenarioLG scenario) {
        this.main = main;
        this.game=game;
        this.scenario=scenario;
    }


    public void register() {
        if (game.getConfig().getScenarioValues().get(scenario)) {
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

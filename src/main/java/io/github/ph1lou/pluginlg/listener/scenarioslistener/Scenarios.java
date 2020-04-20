package io.github.ph1lou.pluginlg.listener.scenarioslistener;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Scenarios implements Listener {
    MainLG main;
    boolean register = false;

    public void init(MainLG main) {
        this.main = main;
    }

    public void register(ScenarioLG scenario) {
        if (main.config.scenarioValues.get(scenario)) {
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

package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.listener.scenarioslisteners.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;

import java.util.HashMap;
import java.util.Map;

public class ScenariosLG {

    final MainLG main;
    final GameManager game;
    final Map<ScenarioLG, Scenarios> scenarios = new HashMap<>();

    public ScenariosLG(MainLG main, GameManager game) {
        this.main = main;
        this.game=game;
    }

    public void init() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(main,game), main);
        pm.registerEvents(new EventListener(game), main);
        pm.registerEvents(new MenuListener(main,game), main);
        pm.registerEvents(new SmallFeaturesListener(main,game), main);
        pm.registerEvents(new EnchantmentListener(game), main);
        pm.registerEvents(new ArmorDetectionListener(game), main);
        pm.registerEvents(new ChatListener(game), main);
        pm.registerEvents(new PatchPotions(game), main);
        for (ScenarioLG scenario : ScenarioLG.values()) {
            try {
                Scenarios s;
                Class<? extends Scenarios> scenarioClass = scenario.getScenario();

                if (scenarioClass != null) {
                    s = scenarioClass.getDeclaredConstructor().newInstance();
                    s.init(main, game);
                    scenarios.put(scenario, s);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        update();
    }

    public void delete() {
        PluginManager pm = Bukkit.getPluginManager();


        for (RegisteredListener event : HandlerList.getRegisteredListeners(main)) {
            HandlerList.unregisterAll(event.getListener());
        }

    }

    public void update() {
        for (ScenarioLG scenario : this.scenarios.keySet()) {
            scenarios.get(scenario).register(scenario);
        }
    }
}

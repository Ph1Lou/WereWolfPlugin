package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.listener.scenarioslistener.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;

public class ScenariosLG {

    final MainLG main;
    final Map<ScenarioLG, Scenarios> scenarios = new HashMap<>();

    public ScenariosLG(MainLG main) {
        this.main = main;
    }

    public void init() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(main), main);
        pm.registerEvents(new EventListener(main), main);
        pm.registerEvents(new MenuListener(main), main);
        pm.registerEvents(new SmallFeaturesListener(main), main);
        pm.registerEvents(new EnchantmentListener(main), main);
        pm.registerEvents(new ServerListener(main), main);
        pm.registerEvents(new ArmorDetectionListener(main), main);
        pm.registerEvents(new ChatListener(main), main);
        pm.registerEvents(new PatchPotions(main), main);
        for (ScenarioLG scenario : ScenarioLG.values()) {
            try {
                Scenarios s;
                Class<? extends Scenarios> scenarioClass = scenario.getScenario();

                if (scenarioClass != null) {
                    s = scenarioClass.getDeclaredConstructor().newInstance();
                    s.init(main);
                    scenarios.put(scenario, s);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        update();
    }

    public void update() {
        for (ScenarioLG scenario : this.scenarios.keySet()) {
            scenarios.get(scenario).register(scenario);
        }
    }
}

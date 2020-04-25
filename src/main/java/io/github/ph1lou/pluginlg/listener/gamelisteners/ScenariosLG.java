package io.github.ph1lou.pluginlg.listener.gamelisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.listener.gamelisteners.scenarioslisteners.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

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
                    s.init(main,game);
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

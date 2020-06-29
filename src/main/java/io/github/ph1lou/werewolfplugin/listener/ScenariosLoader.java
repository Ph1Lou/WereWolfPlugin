package io.github.ph1lou.werewolfplugin.listener;

import io.github.ph1lou.werewolfapi.ScenarioRegister;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ScenariosLoader {

    private final Main main;
    private final GameManager game;
    private final List<Scenarios> scenariosRegister = new ArrayList<>();

    public ScenariosLoader(Main main, GameManager game) {
        this.main = main;
        this.game = game;
        init();
    }

    public void init() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(main, game), main);
        pm.registerEvents(game.events,main);
        pm.registerEvents(new MenuListener(main,game), main);
        pm.registerEvents(new SmallFeaturesListener(main,game), main);
        pm.registerEvents(new EnchantmentListener(game), main);
        pm.registerEvents(new ChatListener(game), main);
        pm.registerEvents(new PatchPotions(game), main);
        pm.registerEvents(new CycleListener(main,game), main);

        for(ScenarioRegister scenarioRegister:main.getRegisterScenarios()){
            try {
                scenariosRegister.add((Scenarios) scenarioRegister.getConstructors().newInstance(main,game,scenarioRegister.getKey()));
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        update();
    }

    public void delete() {

        for (RegisteredListener event : HandlerList.getRegisteredListeners(main)) {
            HandlerList.unregisterAll(event.getListener());
        }

    }

    public void update() {
        for (Scenarios scenario : this.scenariosRegister) {
            scenario.register();
        }
    }
}

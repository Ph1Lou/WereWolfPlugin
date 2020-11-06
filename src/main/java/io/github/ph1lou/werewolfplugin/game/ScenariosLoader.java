package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.ScenarioRegister;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class ScenariosLoader {

    private final List<Scenarios> scenariosRegister = new ArrayList<>();
    private final List<Listener> listeners = new ArrayList<>();
    private final Main main;

    public ScenariosLoader(Main main) {
        this.main = main;
    }

    public void init() {
        PluginManager pm = Bukkit.getPluginManager();
        GameManager game = (GameManager) main.getWereWolfAPI();
        listeners.add(new PlayerListener(main));
        listeners.add(new SmallFeaturesListener(main));
        listeners.add(new EnchantmentListener(game));
        listeners.add(new ChatListener(game));
        listeners.add(new PatchPotions(game));
        listeners.add(new CycleListener(main));
        listeners.add((Listener) game.getScore());
        listeners.add(game.getEvents());
        listeners.add((Listener) game.getVote());
        for (Listener listener : listeners) {
            pm.registerEvents(listener, main);
        }

        for (ScenarioRegister scenarioRegister : main.getRegisterManager().getScenariosRegister()) {
            try {
                scenariosRegister.add((Scenarios) scenarioRegister.getConstructors().newInstance(main, game, scenarioRegister.getKey()));
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        update();
    }

    public void delete() {

        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
        for (Scenarios scenario : scenariosRegister) {
            if (scenario.isRegister()) {
                HandlerList.unregisterAll(scenario);
            }
        }
        for (PlayerWW plg : main.getWereWolfAPI().getPlayersWW().values()) {
            HandlerList.unregisterAll((Listener) plg.getRole());
        }
    }

    public void update() {
        for (Scenarios scenario : this.scenariosRegister) {
            scenario.register();
        }
    }

}

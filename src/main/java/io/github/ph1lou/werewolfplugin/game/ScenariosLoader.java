package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.rolesattributs.LoverAPI;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;


public class ScenariosLoader {

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
        listeners.add((Listener) game.getVote());
        for (Listener listener : listeners) {
            pm.registerEvents(listener, main);
        }
        update();
    }

    public void delete() {

        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }

        for (PlayerWW playerWW1 : main.getWereWolfAPI().getPlayerWW()) {
            HandlerList.unregisterAll((Listener) playerWW1.getRole());
        }

        for (LoverAPI loverAPI : main.getWereWolfAPI().getLoversManager().getLovers()) {
            HandlerList.unregisterAll((Listener) loverAPI);
        }
    }

    public void update() {

        WereWolfAPI game = main.getWereWolfAPI();

        main.getRegisterManager().getScenariosRegister()
                .forEach(scenarioRegister -> scenarioRegister.getScenario()
                        .register(game.getConfig()
                                .getScenarioValues()
                                .get(scenarioRegister.getKey())));

        main.getRegisterManager().getConfigsRegister()
                .stream()
                .filter(configRegister -> configRegister.getConfig() != null)
                .forEach(configRegister -> configRegister.getConfig().register(game.getConfig()
                        .getConfigValues().get(configRegister.getKey())));
    }
}

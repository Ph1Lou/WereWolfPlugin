package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.listeners.ChatListener;
import io.github.ph1lou.werewolfplugin.listeners.CycleListener;
import io.github.ph1lou.werewolfplugin.listeners.EnchantmentListener;
import io.github.ph1lou.werewolfplugin.listeners.PatchPotions;
import io.github.ph1lou.werewolfplugin.listeners.PlayerListener;
import io.github.ph1lou.werewolfplugin.listeners.SmallFeaturesListener;
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

        for (IPlayerWW playerWW1 : main.getWereWolfAPI().getPlayerWW()) {
            HandlerList.unregisterAll((Listener) playerWW1.getRole());
        }

        for (ILover ILover : main.getWereWolfAPI().getLoversManager().getLovers()) {
            HandlerList.unregisterAll((Listener) ILover);
        }
    }

    public void update() {

        WereWolfAPI game = main.getWereWolfAPI();

        main.getRegisterManager().getScenariosRegister()
                .forEach(scenarioRegister -> scenarioRegister.getScenario()
                        .register(game.getConfig()
                                .isScenarioActive(scenarioRegister.getKey())));

        main.getRegisterManager().getConfigsRegister()
                .stream()
                .filter(configRegister -> configRegister.getConfig().isPresent())
                .forEach(configRegister -> configRegister.getConfig().get().register(game.getConfig()
                        .isConfigActive(configRegister.getKey())));
    }
}

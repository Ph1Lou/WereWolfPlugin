package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.ScenarioRegister;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ScenariosLoader {

    private final GameManager game;
    private final List<Scenarios> scenariosRegister = new ArrayList<>();
    private final List<Listener> listeners = new ArrayList<>();

    public ScenariosLoader(GameManager game) {
        this.game = game;
    }

    public void init() {
        Main main = game.getMain();
        PluginManager pm = Bukkit.getPluginManager();
        listeners.add(new PlayerListener(main, game));
        listeners.add(new SmallFeaturesListener(main, game));
        listeners.add(new EnchantmentListener(game));
        listeners.add(new ChatListener(main, game));
        listeners.add(new PatchPotions(game));
        listeners.add(new CycleListener(main, game));
        listeners.add((Listener) game.getScore());
        listeners.add(game.getEvents());
        listeners.add((Listener) game.getVote());
        for (Listener listener : listeners) {
            pm.registerEvents(listener, main);
        }

        for (ScenarioRegister scenarioRegister : main.getRegisterScenarios()) {
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
        for (PlayerWW plg : game.getPlayersWW().values()) {
            HandlerList.unregisterAll((Listener) plg.getRole());
        }
    }

    public void update() {
        for (Scenarios scenario : this.scenariosRegister) {
            scenario.register();
        }
    }

    public void updateCompass() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (game.getPlayersWW().containsKey(player.getUniqueId())) {
                if (game.getConfig().getConfigValues().get("werewolf.menu.global.compass_middle")) {
                    player.setCompassTarget(player.getWorld().getSpawnLocation());
                } else {
                    player.setCompassTarget(game.getPlayersWW().get(player.getUniqueId()).getSpawn());
                }
            }
        }
    }
}

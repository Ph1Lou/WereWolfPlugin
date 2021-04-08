package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.registers.IRegisterManager;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfplugin.RegisterManager;
import io.github.ph1lou.werewolfplugin.listeners.ActionBarListener;
import io.github.ph1lou.werewolfplugin.listeners.ChatListener;
import io.github.ph1lou.werewolfplugin.listeners.CycleListener;
import io.github.ph1lou.werewolfplugin.listeners.EnchantmentListener;
import io.github.ph1lou.werewolfplugin.listeners.PatchPotions;
import io.github.ph1lou.werewolfplugin.listeners.PlayerListener;
import io.github.ph1lou.werewolfplugin.listeners.SmallFeaturesListener;
import io.github.ph1lou.werewolfplugin.listeners.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;


public class ListenersLoader {

    private final List<Listener> listeners = new ArrayList<>();
    private final GameManager game;

    public ListenersLoader(io.github.ph1lou.werewolfapi.WereWolfAPI game) {
        this.game = (GameManager) game;
    }

    public void init() {
        PluginManager pm = Bukkit.getPluginManager();
        listeners.add(new PlayerListener(this.game));
        listeners.add(new SmallFeaturesListener(this.game));
        listeners.add(new EnchantmentListener(this.game));
        listeners.add(new ChatListener(this.game));
        listeners.add(new PatchPotions(this.game));
        listeners.add(new CycleListener(this.game));
        listeners.add(new ActionBarListener(this.game));
        listeners.add(new TabManager(this.game));
        listeners.add((Listener) this.game.getScore());
        listeners.add((Listener) this.game.getVote());
        this.listeners.forEach(BukkitUtils::registerEvents);

        update();
    }

    public void delete() {

        for (Listener listener : this.listeners) {
            HandlerList.unregisterAll(listener);
        }

        for (IPlayerWW playerWW1 : this.game.getPlayerWW()) {
            HandlerList.unregisterAll((Listener) playerWW1.getRole());
        }

        for (ILover lover : this.game.getLoversManager().getLovers()) {
            HandlerList.unregisterAll((Listener) lover);
        }
    }

    public void update() {

        IRegisterManager registerManager = RegisterManager.get();

        registerManager.getScenariosRegister()
                .forEach(scenarioRegister -> scenarioRegister.getScenario()
                        .register(game.getConfig()
                                .isScenarioActive(scenarioRegister.getKey())));

        registerManager.getConfigsRegister()
                .stream()
                .filter(configRegister -> configRegister.getConfig().isPresent())
                .forEach(configRegister -> configRegister.getConfig().get().register(game.getConfig()
                        .isConfigActive(configRegister.getKey())));
    }
}

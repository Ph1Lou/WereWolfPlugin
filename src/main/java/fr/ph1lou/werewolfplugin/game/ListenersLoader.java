package fr.ph1lou.werewolfplugin.game;

import fr.ph1lou.werewolfplugin.RegisterManager;
import fr.ph1lou.werewolfplugin.listeners.ActionBarListener;
import fr.ph1lou.werewolfplugin.listeners.ChatListener;
import fr.ph1lou.werewolfplugin.listeners.CycleListener;
import fr.ph1lou.werewolfplugin.listeners.EnchantmentListener;
import fr.ph1lou.werewolfplugin.listeners.PlayerListener;
import fr.ph1lou.werewolfplugin.listeners.SmallFeaturesListener;
import fr.ph1lou.werewolfplugin.listeners.TabManager;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.registers.interfaces.IRegisterManager;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.listeners.PatchPotions;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;


public class ListenersLoader {

    private final List<Listener> listeners = new ArrayList<>();
    private final GameManager game;

    public ListenersLoader(WereWolfAPI game) {
        this.game = (GameManager) game;
    }

    public void init() {
        listeners.add(new PlayerListener(this.game));
        listeners.add(new SmallFeaturesListener(this.game));
        listeners.add(new EnchantmentListener(this.game));
        listeners.add(new ChatListener(this.game));
        listeners.add(new PatchPotions(this.game));
        listeners.add(new CycleListener(this.game));
        listeners.add(new ActionBarListener(this.game));
        listeners.add(new TabManager(this.game));
        listeners.add((Listener) this.game.getVote());
        this.listeners.forEach(BukkitUtils::registerEvents);

        update();
    }

    public void delete() {

        for (Listener listener : this.listeners) {
            HandlerList.unregisterAll(listener);
        }

        for (IPlayerWW playerWW1 : this.game.getPlayersWW()) {
            HandlerList.unregisterAll(playerWW1.getRole());
        }

        for (ILover lover : this.game.getLoversManager().getLovers()) {
            HandlerList.unregisterAll(lover);
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

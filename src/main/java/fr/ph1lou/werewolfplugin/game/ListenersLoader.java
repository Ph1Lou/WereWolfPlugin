package fr.ph1lou.werewolfplugin.game;

import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.Register;
import fr.ph1lou.werewolfplugin.listeners.ActionBarListener;
import fr.ph1lou.werewolfplugin.listeners.ChatListener;
import fr.ph1lou.werewolfplugin.listeners.CycleListener;
import fr.ph1lou.werewolfplugin.listeners.DamagesListener;
import fr.ph1lou.werewolfplugin.listeners.DeathListener;
import fr.ph1lou.werewolfplugin.listeners.EnchantmentListener;
import fr.ph1lou.werewolfplugin.listeners.PatchPotions;
import fr.ph1lou.werewolfplugin.listeners.PlayerListener;
import fr.ph1lou.werewolfplugin.listeners.SmallFeaturesListener;
import fr.ph1lou.werewolfplugin.listeners.TabManager;
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
        this.listeners.add(new PlayerListener(this.game));
        this.listeners.add(new SmallFeaturesListener(this.game));
        this.listeners.add(new EnchantmentListener(this.game));
        this.listeners.add(new ChatListener(this.game));
        this.listeners.add(new PatchPotions(this.game));
        this.listeners.add(new CycleListener(this.game));
        this.listeners.add(new ActionBarListener(this.game));
        this.listeners.add(new TabManager(this.game));
        this.listeners.add(new DeathListener(this.game));
        this.listeners.add(new DamagesListener(this.game));
        this.listeners.add((Listener) this.game.getVoteManager());
        this.listeners.forEach(BukkitUtils::registerListener);

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

        Register registerManager = Register.get();

        registerManager.getScenariosRegister()
                .forEach(scenarioRegister -> scenarioRegister.getObject()
                        .ifPresent(listenerManager -> listenerManager.register(game.getConfig()
                                .isScenarioActive(scenarioRegister.getMetaDatas().key()))
                        ));

        registerManager.getConfigsRegister()
                .forEach(configurationWrapper -> configurationWrapper.getObject()
                        .ifPresent(object -> {
                            if(object instanceof ListenerManager){
                                ((ListenerManager)object).register(game.getConfig()
                                        .isConfigActive(configurationWrapper.getMetaDatas().key()));
                            }
                        }));

        registerManager.getTimersRegister()
                .forEach(timerWrapper -> timerWrapper.getObject()
                        .ifPresent(object -> {
                            if(object instanceof ListenerManager){
                                ((ListenerManager)object).register(true);
                            }
                        }));
    }
}

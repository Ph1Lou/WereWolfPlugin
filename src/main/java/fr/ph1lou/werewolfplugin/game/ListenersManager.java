package fr.ph1lou.werewolfplugin.game;

import fr.ph1lou.werewolfapi.game.IListenersManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
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
import fr.ph1lou.werewolfplugin.listeners.InvisibleListener;
import fr.ph1lou.werewolfplugin.listeners.PatchPotions;
import fr.ph1lou.werewolfplugin.listeners.PlayerListener;
import fr.ph1lou.werewolfplugin.listeners.SmallFeaturesListener;
import fr.ph1lou.werewolfplugin.listeners.TabManager;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class ListenersManager implements IListenersManager {

    private final List<Listener> listeners = new ArrayList<>();
    private final Map<String, ListenerWerewolf> listenersScenarios = new HashMap<>();
    private final Map<String, ListenerWerewolf> listenersConfigurations = new HashMap<>();
    private final Map<String, ListenerWerewolf> listenersTimers = new HashMap<>();
    private final Map<String, ListenerWerewolf> listenersRandomEvents = new HashMap<>();

    private final WereWolfAPI game;

    public ListenersManager(WereWolfAPI game) {
        this.game = game;

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
        this.listeners.add(new InvisibleListener(this.game));
        VoteManager voteManager = new VoteManager(this.game);
        this.game.setVoteManager(voteManager);
        this.listeners.add(voteManager);
        this.listeners.forEach(BukkitUtils::registerListener);

        Register registerManager = Register.get();

        registerManager.getTimersRegister()
                .forEach(timerWrapper -> this.instantiate(timerWrapper.getClazz())
                        .ifPresent(listenerWerewolf -> {
                            listenerWerewolf.register(true);
                            this.listenersTimers.put(timerWrapper.getMetaDatas().key(), listenerWerewolf);
                        }));

        registerManager.getRandomEventsRegister()
                .forEach(eventWrapper -> this.instantiate(eventWrapper.getClazz())
                        .ifPresent(listenerWerewolf -> this.listenersRandomEvents.put(eventWrapper.getMetaDatas().key(),
                                listenerWerewolf)));

        registerManager.getScenariosRegister().forEach(scenarioRegister -> this.instantiate(scenarioRegister.getClazz())
                .ifPresent(listenerWerewolf -> this.listenersScenarios.put(scenarioRegister.getMetaDatas().key(), listenerWerewolf)));

        registerManager.getConfigsRegister()
                .forEach(configurationWrapper -> this.instantiate(configurationWrapper.getClazz())
                        .ifPresent(listenerWerewolf -> this.listenersConfigurations.put(configurationWrapper.getMetaDatas().config().key(), listenerWerewolf)));
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

        for (Listener listener : this.listenersTimers.values()) {
            HandlerList.unregisterAll(listener);
        }

        for (Listener listener : this.listenersScenarios.values()) {
            HandlerList.unregisterAll(listener);
        }

        for (Listener listener : this.listenersConfigurations.values()) {
            HandlerList.unregisterAll(listener);
        }

        for (Listener listener : this.listenersRandomEvents.values()) {
            HandlerList.unregisterAll(listener);
        }
    }


    private Optional<ListenerWerewolf> instantiate(Class<?> clazz) {
        if (ListenerWerewolf.class.isAssignableFrom(clazz)) {
            try {
                return Optional.of((ListenerWerewolf) clazz.getConstructor(WereWolfAPI.class).newInstance(game));
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ListenerWerewolf> getRandomEvent(String key) {
        return Optional.ofNullable(this.listenersRandomEvents.get(key));
    }

    @Override
    public Optional<ListenerWerewolf> getScenario(String key) {
        return Optional.ofNullable(this.listenersScenarios.get(key));
    }

    @Override
    public Optional<ListenerWerewolf> getConfiguration(String key) {
        return Optional.ofNullable(this.listenersConfigurations.get(key));
    }

    @Override
    public Optional<ListenerWerewolf> getTimer(String key) {
        return Optional.ofNullable(this.listenersTimers.get(key));
    }

    @Override
    public void updateListeners() {

        Register registerManager = Register.get();

        registerManager.getScenariosRegister()
                .forEach(scenarioRegister -> this.getScenario(scenarioRegister.getMetaDatas().key())
                        .ifPresent(listenerWerewolf -> listenerWerewolf
                                .register(game.getConfig().isScenarioActive(scenarioRegister.getMetaDatas().key())))
                );

        registerManager.getConfigsRegister()
                .forEach(configurationWrapper -> this.getConfiguration(configurationWrapper.getMetaDatas().config().key())
                        .ifPresent(listenerWerewolf -> listenerWerewolf.register(game.getConfig()
                                .isConfigActive(configurationWrapper.getMetaDatas().config().key()))));

        registerManager.getRandomEventsRegister()
                .forEach(listenerWerewolfEventWrapper -> this.getRandomEvent(listenerWerewolfEventWrapper.getMetaDatas().key())
                        .ifPresent(listenerWerewolf -> listenerWerewolf
                                .register(game.getRandom().nextDouble() * 100 < game.getConfig().getProbability(listenerWerewolfEventWrapper.getMetaDatas().key()))));

    }
}

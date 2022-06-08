package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.random_events.BearingRitualEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

@Event(key = EventBase.BEARING_RITUAL, loreKey = "werewolf.random_events.bearing_ritual.description",
timers = {@Timer(key = BearingRitual.TIMER_START, defaultValue = 60*60, meetUpValue = 30*60, step = 30),
        @Timer(key = BearingRitual.PERIOD, defaultValue = 40*60, meetUpValue = 20*60, step = 30)})
public class BearingRitual extends ListenerWerewolf {

    public static final String TIMER_START = "werewolf.random_events.bearing_ritual.timer_start";
    public static final String PERIOD = "werewolf.random_events.bearing_ritual.period";

    private boolean active = false;

    public BearingRitual(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {
                    BearingRitualEvent bearingRitualEvent = new BearingRitualEvent();
                    Bukkit.getPluginManager().callEvent(bearingRitualEvent);

                    if (bearingRitualEvent.isCancelled()) return;

                    active = true;

                    Bukkit.broadcastMessage(game.translate("werewolf.random_events.bearing_ritual.message"));

                    BukkitUtils.scheduleSyncDelayedTask(() -> {
                        if (game.isState(StateGame.GAME)) {
                            if (isRegister()) {
                                active = false;
                                register(false);
                                Bukkit.broadcastMessage(game.translate("werewolf.random_events.bearing_ritual.end"));
                            }
                        }
                    }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 40L);
                }
            }
        }, (long) (20L * game.getConfig().getTimerValue(TIMER_START) + game.getRandom().nextDouble() * 15 * game.getConfig().getTimerValue(PERIOD)));
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        if (!active) return;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onGameStop(StopEvent event) {
        active = false;
    }

    @EventHandler
    public void onGameStart(StartEvent event) {
        active = false;
    }

}

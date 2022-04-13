package fr.ph1lou.werewolfplugin.listeners.random_events;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.random_events.BearingRitualEvent;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class BearingRitual extends ListenerManager {

    private boolean active = false;

    public BearingRitual(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
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
                    }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION.getKey()) * 40L);
                }
            }
        }, (long) (20 * 60 * 60 + game.getRandom().nextDouble() * 15 * 60 * 40));
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

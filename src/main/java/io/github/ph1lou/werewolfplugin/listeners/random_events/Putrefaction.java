package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.PutrefactionEvent;
import io.github.ph1lou.werewolfapi.events.RepartitionEvent;
import io.github.ph1lou.werewolfapi.events.StartEvent;
import io.github.ph1lou.werewolfapi.events.StopEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class Putrefaction extends ListenerManager {

    private boolean active = false;

    public Putrefaction(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = main.getWereWolfAPI();

        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {

                    PutrefactionEvent putrefactionEvent = new PutrefactionEvent();
                    Bukkit.getPluginManager().callEvent(putrefactionEvent);

                    if (putrefactionEvent.isCancelled()) return;

                    active = true;

                    Bukkit.broadcastMessage(game.translate("werewolf.random_events.putrefaction.message"));

                    Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
                        if (game.isState(StateGame.GAME)) {
                            if (isRegister()) {
                                active = false;
                                register(false);
                            }
                        }
                    }, game.getConfig().getTimerValue(TimersBase.DAY_DURATION.getKey()) * 40L);
                }
            }
        }, (long) (20 * 60 * 60 + game.getRandom().nextDouble() * 15 * 60 * 20));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!active) return;

        if (Objects.requireNonNull(event.getTo()).getBlock().isLiquid()) {
            event.getPlayer().damage(0.5);
        }
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

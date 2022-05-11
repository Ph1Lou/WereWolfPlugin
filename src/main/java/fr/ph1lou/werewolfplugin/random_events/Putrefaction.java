package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.random_events.PutrefactionEvent;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

@Event(key = EventBase.PUTREFACTION, loreKey = "werewolf.random_events.putrefaction.description")
public class Putrefaction extends ListenerManager {

    private boolean active = false;

    public Putrefaction(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {

                    PutrefactionEvent putrefactionEvent = new PutrefactionEvent();
                    Bukkit.getPluginManager().callEvent(putrefactionEvent);

                    if (putrefactionEvent.isCancelled()) return;

                    active = true;

                    Bukkit.broadcastMessage(game.translate("werewolf.random_events.putrefaction.message"));

                    BukkitUtils.scheduleSyncDelayedTask(() -> {
                        if (game.isState(StateGame.GAME)) {
                            if (isRegister()) {
                                active = false;
                                register(false);
                                Bukkit.broadcastMessage(game.translate("werewolf.random_events.putrefaction.end"));
                            }
                        }
                    }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 40L);
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

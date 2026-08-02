package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.utils.ExposedHelper;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@RandomEvent(key = EventBase.EXPOSED,
        loreKey = "werewolf.random_events.exposed.description",
        timers = { @Timer(key = Exposed.TIMER_START_1, defaultValue = 65 * 60, meetUpValue = 20 * 60, step = 30),
                @Timer(key = Exposed.TIMER_START_2, defaultValue = 30 * 60, meetUpValue = 15 * 60, step = 30),
                @Timer(key = Exposed.PERIOD, defaultValue = 15 * 60, meetUpValue = 15 * 60, step = 30) })
public class Exposed extends ListenerWerewolf {

    public static final String TIMER_START_1 = "werewolf.random_events.exposed.timer_start_1";
    public static final String TIMER_START_2 = "werewolf.random_events.exposed.timer_start_2";
    public static final String PERIOD = "werewolf.random_events.exposed.period";
    private IPlayerWW temp = null;

    public Exposed(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (isRegister()) {
                IPlayerWW playerWW = announce();

                if (temp == null && playerWW != null) {
                    temp = playerWW;
                    BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                        if (isRegister()) {
                            announce();
                            temp = null;
                            register(false);
                        }
                    }, game.getConfig().getTimerValue(TIMER_START_2) * 20L);
                }

            }
        }, (long) (20L * game.getConfig().getTimerValue(TIMER_START_1) + game.getRandom().nextDouble() * game.getConfig().getTimerValue(PERIOD) * 20));
    }

    @Nullable
    private IPlayerWW announce() {

        WereWolfAPI game = this.getGame();

        List<IPlayerWW> playerWWS = game.getAlivePlayersWW().stream()
                .filter(playerWW -> !playerWW.equals(temp))
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return null;

        IPlayerWW playerWW = playerWWS.get(game.getRandom().nextInt(playerWWS.size()));

        ExposedHelper.expose(game, playerWW);

        return playerWW;
    }
}

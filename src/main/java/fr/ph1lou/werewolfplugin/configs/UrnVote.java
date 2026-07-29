package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.events.buildings.UrnVote1StartEvent;
import fr.ph1lou.werewolfapi.events.buildings.UrnVote2StartEvent;
import fr.ph1lou.werewolfapi.events.buildings.UrnVote3StartEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.URN_VOTE,
        defaultValue = true, meetUpValue = true,
        incompatibleConfigs = {ConfigBase.VOTE},
        loreKey = "werewolf.configurations.urn_vote.description"),
        timers = {
                @Timer(key = TimerBase.URN_VOTE_1_START, defaultValue = 30 * 60, meetUpValue = 8 * 60,
                        decrement = true, step = 30, onZero = UrnVote1StartEvent.class),
                @Timer(key = TimerBase.URN_VOTE_2_START, defaultValue = 60 * 60, meetUpValue = 16 * 60,
                        decrement = true, step = 30, onZero = UrnVote2StartEvent.class),
                @Timer(key = TimerBase.URN_VOTE_3_START, defaultValue = 90 * 60, meetUpValue = 24 * 60,
                        decrement = true, step = 30, onZero = UrnVote3StartEvent.class),
                @Timer(key = TimerBase.URN_VOTE_DURATION, defaultValue = 3 * 60, meetUpValue = 60, step = 10)
        })
public class UrnVote extends ListenerWerewolf {

    public UrnVote(WereWolfAPI game) {
        super(game);
    }

    @EventHandler
    public void onUrnVote1(UrnVote1StartEvent event) {
        startSession(1);
    }

    @EventHandler
    public void onUrnVote2(UrnVote2StartEvent event) {
        startSession(2);
        scheduleSanctuaries();
    }

    @EventHandler
    public void onUrnVote3(UrnVote3StartEvent event) {
        startSession(3);
    }

    private void startSession(int n) {
        WereWolfAPI game = getGame();

        if (!game.getConfig().isConfigActive(ConfigBase.URN_VOTE)) {
            return;
        }
        if (!game.getConfig().isConfigActive(ConfigBase.BUILDINGS)) {
            return;
        }

        ((GameManager) game).getBuildingsManager().getUrnVoteManager().startSession(n);

        int duration = game.getConfig().getTimerValue(TimerBase.URN_VOTE_DURATION);
        Bukkit.getOnlinePlayers().forEach(Sound.CHICKEN_HURT::play);
        Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.configurations.buildings.urn_vote_time",
                Formatter.number(n),
                Formatter.timer(Utils.conversion(duration))));

        int sessionNumber = n;
        BukkitUtils.scheduleSyncDelayedTask(game,
                () -> ((GameManager) game).getBuildingsManager().getUrnVoteManager().endSession(sessionNumber),
                duration * 20L);
    }

    private void scheduleSanctuaries() {
        WereWolfAPI game = getGame();
        if (!game.getConfig().isConfigActive(ConfigBase.BUILDINGS)) {
            return;
        }
        if (!game.getConfig().isConfigActive(ConfigBase.BUILDING_SANCTUARY)) {
            return;
        }
        int duration = game.getConfig().getTimerValue(TimerBase.URN_VOTE_DURATION);
        BukkitUtils.scheduleSyncDelayedTask(game,
                () -> ((GameManager) game).getBuildingsManager().generateSanctuaries(),
                (duration + 60) * 20L);
    }
}

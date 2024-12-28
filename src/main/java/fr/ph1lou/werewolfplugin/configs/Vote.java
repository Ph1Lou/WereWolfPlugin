package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteBeginEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEndEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.vote.IVoteManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.VOTE,
        defaultValue = true,
        meetUpValue = true),
        timers = {
                @Timer(key = TimerBase.VOTE_BEGIN,
                        defaultValue = 40 * 60,
                        meetUpValue = 6 * 60,
                        decrement = true,
                        step = 30,
                        onZero = VoteBeginEvent.class),
                @Timer(key = TimerBase.VOTE_DURATION,
                        defaultValue = 60 * 3,
                        meetUpValue = 60,
                        step = 10),
                @Timer(key = TimerBase.VOTE_WAITING,
                        defaultValue = 60,
                        meetUpValue = 60,
                        step = 10)
        },
        configValues = { @IntValue(key = IntValueBase.VOTE_END, defaultValue = 8,
                meetUpValue = 8,
                step = 1, item = UniversalMaterial.PLAYER_HEAD),
                @IntValue(key = IntValueBase.VOTE_DISTANCE, defaultValue = 20,
                        meetUpValue = 15,
                        step = 1, item = UniversalMaterial.BLACK_WOOL)
        },
        configurations = {
                @ConfigurationBasic(key = ConfigBase.VOTE_EVERY_OTHER_DAY) }
)
public class Vote extends ListenerWerewolf {

    public Vote(WereWolfAPI game) {
        super(game);
    }

    @EventHandler
    public void onVoteBegin(VoteBeginEvent event) {
        this.getGame().getVoteManager().setStatus(VoteStatus.NOT_IN_PROGRESS);
        this.getGame().getConfig().setScenario(ScenarioBase.NO_POISON, false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVoteEnd(VoteEndEvent event) {

        WereWolfAPI game = this.getGame();
        IVoteManager voteManager = game.getVoteManager();

        voteManager.setStatus(VoteStatus.WAITING);

        voteManager.triggerResult();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        WereWolfAPI game = this.getGame();
        IVoteManager voteManager = game.getVoteManager();

        if (!voteManager.isStatus(VoteStatus.NOT_IN_PROGRESS)) {
            return;
        }

        if (game.getPlayersCount() < game.getConfig().getValue(IntValueBase.VOTE_END)) {
            Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.configurations.vote.vote_deactivate"));
            voteManager.setStatus(VoteStatus.ENDED);
            return;
        }

        if (event.getNumber() % 2 == 1 && game.getConfig().isConfigActive(ConfigBase.VOTE_EVERY_OTHER_DAY)) {
            return;
        }

        int duration = game.getConfig().getTimerValue(TimerBase.VOTE_DURATION);

        voteManager.resetVote();
        Bukkit.getOnlinePlayers().forEach(Sound.CHICKEN_HURT::play);
        Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.configurations.vote.vote_time",
                Formatter.timer(Utils.conversion(duration))));
        voteManager.setStatus(VoteStatus.IN_PROGRESS);
        BukkitUtils.scheduleSyncDelayedTask(game, () -> Bukkit.getPluginManager().callEvent(new VoteEndEvent()), duration * 20L);
    }
}

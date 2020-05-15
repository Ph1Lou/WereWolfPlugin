package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.events.*;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.Day;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CycleListener implements Listener {

    final MainLG main;
    final GameManager game;

    public CycleListener(MainLG main,GameManager game){
        this.main=main;
        this.game=game;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event){

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        game.setDay(Day.DAY);

        if(game.isState(StateLG.END)) return;

        long duration = game.config.getTimerValues().get(TimerLG.VOTE_DURATION);
        Bukkit.broadcastMessage(game.translate("werewolf.announcement.day", event.getNumber()));
        game.score.groupSizeChange();

        if (game.config.getConfigValues().get(ToolLG.VOTE) && game.score.getPlayerSize() < game.config.getPlayerRequiredVoteEnd()) {
            game.config.getConfigValues().put(ToolLG.VOTE, false);
            Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_deactivate"));
        }

        if(2*game.config.getTimerValues().get(TimerLG.DAY_DURATION) - duration-game.config.getTimerValues().get(TimerLG.CITIZEN_DURATION)>0){

            if (game.config.getConfigValues().get(ToolLG.VOTE) && game.config.getTimerValues().get(TimerLG.VOTE_BEGIN) < 0) {
                Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_time", game.score.conversion(game.config.getTimerValues().get(TimerLG.VOTE_DURATION))));
            }
            if (game.config.getConfigValues().get(ToolLG.VOTE) && game.config.getTimerValues().get(TimerLG.VOTE_DURATION) + game.config.getTimerValues().get(TimerLG.VOTE_BEGIN) < 0) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> Bukkit.getPluginManager().callEvent(new VoteEndEvent(game.getGameUUID())),duration*20);
            }
        }
        long duration2 = game.config.getTimerValues().get(TimerLG.POWER_DURATION);

        if (2*game.config.getTimerValues().get(TimerLG.DAY_DURATION)-duration2>0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> Bukkit.getPluginManager().callEvent(new SelectionEndEvent(game.getGameUUID())),duration2*20);

        }

        long duration3 = game.config.getTimerValues().get(TimerLG.DAY_DURATION);

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> Bukkit.getPluginManager().callEvent(new NightEvent(game.getGameUUID(), event.getNumber())),duration3*20);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event){

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        long duration  =game.config.getTimerValues().get(TimerLG.DAY_DURATION)-30;
        game.setDay(Day.NIGHT);

        if(game.isState(StateLG.END)) return;

        Bukkit.broadcastMessage(game.translate("werewolf.announcement.night", event.getNumber()));
        game.score.groupSizeChange();

        if(duration>0){
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> Bukkit.getPluginManager().callEvent(new DayWillComeEvent(game.getGameUUID())),duration*20);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> Bukkit.getPluginManager().callEvent(new DayEvent(game.getGameUUID(),event.getNumber()+1)),(duration+30)*20);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVoteEnd(VoteEndEvent event){

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        long duration= game.config.getTimerValues().get(TimerLG.CITIZEN_DURATION);
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> game.vote.showResultVote(game.vote.getResult()),duration*20);
    }
}

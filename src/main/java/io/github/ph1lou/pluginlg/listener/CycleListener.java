package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.*;
import io.github.ph1lou.pluginlgapi.events.*;
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


        game.setDay(Day.DAY);

        if(game.isState(StateLG.END)) return;

        long duration = game.getConfig().getTimerValues().get(TimerLG.VOTE_DURATION);
        Bukkit.broadcastMessage(game.translate("werewolf.announcement.day", event.getNumber()));
        game.score.groupSizeChange();

        if (game.getConfig().getConfigValues().get(ToolLG.VOTE) && game.score.getPlayerSize() < game.getConfig().getPlayerRequiredVoteEnd()) {
            game.getConfig().getConfigValues().put(ToolLG.VOTE, false);
            Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_deactivate"));
            game.getVote().setStatus(VoteStatus.ENDED);
        }

        if(2*game.getConfig().getTimerValues().get(TimerLG.DAY_DURATION) - duration-game.getConfig().getTimerValues().get(TimerLG.CITIZEN_DURATION)>0){

            if (game.getConfig().getConfigValues().get(ToolLG.VOTE) && game.getConfig().getTimerValues().get(TimerLG.VOTE_BEGIN) < 0) {
                Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_time", game.score.conversion((int) duration)));
                game.getVote().setStatus(VoteStatus.IN_PROGRESS);
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                    if(!game.isState(StateLG.END)){
                        Bukkit.getPluginManager().callEvent(new VoteEndEvent());
                    }

                },duration*20);
            }
        }
        long duration2 = game.getConfig().getTimerValues().get(TimerLG.POWER_DURATION);

        if (2*game.getConfig().getTimerValues().get(TimerLG.DAY_DURATION)-duration2>0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {

                if(!game.isState(StateLG.END)){
                    Bukkit.getPluginManager().callEvent(new SelectionEndEvent());
                }
            },duration2*20);

        }

        long duration3 = game.getConfig().getTimerValues().get(TimerLG.DAY_DURATION);

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if(!game.isState(StateLG.END)){
                Bukkit.getPluginManager().callEvent(new NightEvent( event.getNumber()));
            }

        },duration3*20);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event){


        long duration  =game.getConfig().getTimerValues().get(TimerLG.DAY_DURATION)-30;
        game.setDay(Day.NIGHT);

        if(game.isState(StateLG.END)) return;

        Bukkit.broadcastMessage(game.translate("werewolf.announcement.night", event.getNumber()));
        game.score.groupSizeChange();

        if(duration>0){
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                if(!game.isState(StateLG.END)){
                    Bukkit.getPluginManager().callEvent(new DayWillComeEvent());
                }

            },duration*20);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if(!game.isState(StateLG.END)){
                Bukkit.getPluginManager().callEvent(new DayEvent(event.getNumber()+1));
            }

        },(duration+30)*20);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVoteEnd(VoteEndEvent event){

        long duration= game.getConfig().getTimerValues().get(TimerLG.CITIZEN_DURATION);
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if(!game.isState(StateLG.END)){
                Bukkit.getPluginManager().callEvent(new VoteResultEvent());
            }

        },duration*20);
    }
}

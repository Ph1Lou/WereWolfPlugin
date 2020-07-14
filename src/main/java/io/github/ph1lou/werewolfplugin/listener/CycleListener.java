package io.github.ph1lou.werewolfplugin.listener;

import io.github.ph1lou.werewolfapi.ScoreAPI;
import io.github.ph1lou.werewolfapi.enumlg.*;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CycleListener implements Listener {

    private final Main main;
    private final GameManager game;

    public CycleListener(Main main, GameManager game) {
        this.main = main;
        this.game = game;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        game.setDay(Day.DAY);

        if(game.isState(StateLG.END)) return;

        long duration = game.getConfig().getTimerValues().get(TimerLG.VOTE_DURATION);
        Bukkit.broadcastMessage(game.translate("werewolf.announcement.day", event.getNumber()));
        groupSizeChange();

        if (game.getConfig().getConfigValues().get(ToolLG.VOTE) && game.getScore().getPlayerSize() < game.getConfig().getPlayerRequiredVoteEnd()) {
            game.getConfig().getConfigValues().put(ToolLG.VOTE, false);
            Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_deactivate"));
            game.getVote().setStatus(VoteStatus.ENDED);
        }

        if(2*game.getConfig().getTimerValues().get(TimerLG.DAY_DURATION) - duration-game.getConfig().getTimerValues().get(TimerLG.CITIZEN_DURATION)>0){

            if (game.getConfig().getConfigValues().get(ToolLG.VOTE) && game.getConfig().getTimerValues().get(TimerLG.VOTE_BEGIN) < 0) {
                Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_time", game.getScore().conversion((int) duration)));
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
        groupSizeChange();

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
    public void onVoteEnd(VoteEndEvent event) {

        long duration = game.getConfig().getTimerValues().get(TimerLG.CITIZEN_DURATION);
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if (!game.isState(StateLG.END)) {
                Bukkit.getPluginManager().callEvent(new VoteResultEvent());
            }

        }, duration * 20);
    }

    public void groupSizeChange() {

        ScoreAPI score = game.getScore();

        if (score.getPlayerSize() <= score.getGroup() * 3 && score.getGroup() > 3) {
            score.setGroup(score.getGroup() - 1);

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(game.translate("werewolf.commands.admin.group.group_change", score.getGroup()));
                p.sendTitle( game.translate("werewolf.commands.admin.group.top_title"), game.translate("werewolf.commands.admin.group.bot_title", score.getGroup()), 20, 60, 20);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWereWolfList(WereWolfListEvent event) {
        game.updateNameTag();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLoverRepartition(LoversRepartitionEvent event) {
        game.getLoversManage().autoLovers();
    }

    @EventHandler
    public void onPVP(PVPEvent event) {

        game.getWorld().setPVP(true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate("werewolf.announcement.pvp"));
            p.playSound(p.getLocation(), Sound.ENTITY_DONKEY_ANGRY, 1, 20);
        }
    }

    @EventHandler
    public void onDiggingEnd(DiggingEndEvent event) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate("werewolf.announcement.mining"));
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 20);
        }
    }
}

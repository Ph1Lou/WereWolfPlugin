package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayWillComeEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.roles.SelectionEndEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.statistiks.StatistiksUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class CycleListener implements Listener {

    private final GameManager game;

    public CycleListener(WereWolfAPI game) {
        this.game = (GameManager) game;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        game.setDay(Day.DAY);

        if (game.isState(StateGame.END)) return;

        game.getMapManager().getWorld().setTime(23500);

        Bukkit.broadcastMessage(game.translate(Prefix.ORANGE , "werewolf.announcement.day",
                Formatter.number(event.getNumber())));
        groupSizeChange();


        long duration = game.getConfig().getTimerValue(TimerBase.POWER_DURATION);

        if (2L * game.getConfig().getTimerValue(TimerBase.DAY_DURATION)
                - duration > 0) {

            BukkitUtils.scheduleSyncDelayedTask(game, () -> Bukkit.getPluginManager().callEvent(new SelectionEndEvent()), duration * 20);

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event) {

        long duration = game.getConfig().getTimerValue(TimerBase.DAY_DURATION)
                - 30;
        game.setDay(Day.NIGHT);

        if (game.isState(StateGame.END)) return;

        if(event.getNumber()%2==0){
            BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                String message = StatistiksUtils.getMessage();
                if(!message.isEmpty()){
                    Bukkit.broadcastMessage(message);
                }
            }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 10L);
        }

        game.getMapManager().getWorld().setTime(12000);

        Bukkit.broadcastMessage(game.translate(Prefix.YELLOW , "werewolf.announcement.night",
                Formatter.number(event.getNumber())));
        groupSizeChange();

        if (duration > 0) {
            BukkitUtils.scheduleSyncDelayedTask(game, () -> Bukkit.getPluginManager().callEvent(new DayWillComeEvent()), duration * 20);
        }

        BukkitUtils.scheduleSyncDelayedTask(game, () -> Bukkit.getPluginManager().callEvent(new DayEvent(event.getNumber() + 1)), (duration + 30) * 20);
    }

    public void groupSizeChange() {

        if (game.getPlayersCount() <= game.getGroup() * 3 && game.getGroup() > 3) {
            game.setGroup(game.getGroup() - 1);

            Bukkit.getOnlinePlayers()
                    .forEach(player -> {
                        player.sendMessage(
                                game.translate(
                                        Prefix.ORANGE , "werewolf.commands.admin.group.group_change",
                                        Formatter.number(game.getGroup())));
                        VersionUtils.getVersionUtils().sendTitle(
                                player,
                                game.translate("werewolf.commands.admin.group.top_title"),
                                game.translate("werewolf.commands.admin.group.bot_title",
                                        Formatter.number(game.getGroup())),
                                20,
                                60,
                                20);
                    });
        }
    }
}

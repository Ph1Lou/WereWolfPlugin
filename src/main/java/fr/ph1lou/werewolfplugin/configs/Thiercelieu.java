package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;

@Configuration(key = ConfigBase.THIERCELIEU, loreKey = "werewolf.thiercelieu.description")
public class Thiercelieu extends ListenerManager {

    private final List<AnnouncementDeathEvent> announcementDeathEvents = new ArrayList<>();
    private final List<IPlayerWW> playerWWList = new ArrayList<>();

    public Thiercelieu(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnnouncement(AnnouncementDeathEvent event){
        event.setCancelled(true);
        if(!this.playerWWList.contains(event.getPlayerWW())){
            this.announcementDeathEvents.add(event);
            this.playerWWList.add(event.getPlayerWW());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDay(DayEvent event){
        this.announcementDeathEvents.forEach(announcementDeathEvent -> {
            Formatter[] formatters = (Formatter[]) ArrayUtils.addAll(announcementDeathEvent.getFormatters().toArray(new Formatter[0]),
                    new Formatter[]{Formatter.player( announcementDeathEvent.getPlayerName()),
                            Formatter.role(this.getGame().translate(announcementDeathEvent.getRole()))});
            announcementDeathEvent.getTargetPlayer().sendMessageWithKey("werewolf.utils.bar");
            announcementDeathEvent.getTargetPlayer().sendMessageWithKey(Prefix.RED,announcementDeathEvent.getFormat(),formatters);
            announcementDeathEvent.getTargetPlayer().sendMessageWithKey("werewolf.utils.bar");
            announcementDeathEvent.getTargetPlayer().sendSound(Sound.AMBIENCE_THUNDER);
        });

        this.announcementDeathEvents.clear();
        this.playerWWList.clear();
    }
    @EventHandler
    public void onGameStop(StopEvent event) {
        this.announcementDeathEvents.clear();
    }

    @EventHandler
    public void onGameStart(StartEvent event) {
        this.announcementDeathEvents.clear();
    }
}

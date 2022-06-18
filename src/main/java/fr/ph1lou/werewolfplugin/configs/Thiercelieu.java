package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.AnnouncementLoverDeathEvent;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.THIERCELIEU,
        loreKey = "werewolf.configurations.thiercelieu.description"))
public class Thiercelieu extends ListenerWerewolf {

    private final List<AnnouncementDeathEvent> announcementDeathEvents = new ArrayList<>();
    private final Set<IPlayerWW> playerWWList = new HashSet<>();
    private final List<AnnouncementLoverDeathEvent> loversList = new ArrayList<>();

    public Thiercelieu(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnnouncement(AnnouncementDeathEvent event){

        if(getGame().isDay(Day.DAY)){
            return;
        }

        event.setCancelled(true);
        this.announcementDeathEvents.add(0, event);
        this.playerWWList.add(event.getPlayerWW());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        if(getGame().isDay(Day.DAY)){
            return;
        }

        if(this.playerWWList.stream()
                .noneMatch(playerWW -> playerWW.getUUID().equals(event.getPlayerUUID()))){
            return;
        }

        event.setTabVisibility(false);
    }

    @EventHandler
    public void onLoverDeathMessage(AnnouncementLoverDeathEvent event){

        if(getGame().isDay(Day.DAY)){
            return;
        }

        this.loversList.add(event);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDay(DayEvent event){

        this.playerWWList.clear();

        this.announcementDeathEvents.forEach(announcementDeathEvent -> {
            Formatter[] formatters = (Formatter[]) ArrayUtils.addAll(announcementDeathEvent.getFormatters().toArray(new Formatter[0]),
                    new Formatter[]{Formatter.player( announcementDeathEvent.getPlayerName()),
                            Formatter.role(this.getGame().translate(announcementDeathEvent.getRole()))});

            if(this.loversList.stream()
                    .anyMatch(announcementLoverDeathEvent -> announcementLoverDeathEvent
                            .getPlayerWW().equals(announcementDeathEvent.getPlayerWW()))){
                announcementDeathEvent.getTargetPlayer()
                        .sendMessageWithKey("werewolf.lovers.lover.lover_death",
                                Formatter.player(announcementDeathEvent.getPlayerWW().getName()));
            }

            announcementDeathEvent.getTargetPlayer().sendMessageWithKey("werewolf.utils.bar");
            announcementDeathEvent.getTargetPlayer().sendMessageWithKey(Prefix.RED,announcementDeathEvent.getFormat(),formatters);
            announcementDeathEvent.getTargetPlayer().sendMessageWithKey("werewolf.utils.bar");
            announcementDeathEvent.getTargetPlayer().sendSound(Sound.AMBIENCE_THUNDER);

            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(announcementDeathEvent.getPlayerWW()));
        });

        this.announcementDeathEvents.clear();
        this.loversList.clear();
        this.playerWWList.clear();

    }
}

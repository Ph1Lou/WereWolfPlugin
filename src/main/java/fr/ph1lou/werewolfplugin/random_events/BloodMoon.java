package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.AnnouncementLoverDeathEvent;
import fr.ph1lou.werewolfapi.events.random_events.BloodMoonEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RandomEvent(key = EventBase.BLOOD_MOON, loreKey = "werewolf.random_events.blood_moon.description",
        configValues = @IntValue(key = BloodMoon.START_DAY, defaultValue = 13, meetUpValue = 7, step = 1, item = UniversalMaterial.COMPASS))
public class BloodMoon extends ListenerWerewolf {

    public final static String START_DAY = "werewolf.random_events.blood_moon.count";
    private final List<AnnouncementDeathEvent> announcementDeathEvents = new ArrayList<>();
    private final Set<IPlayerWW> playerWWList = new HashSet<>();
    private final List<AnnouncementLoverDeathEvent> loversList = new ArrayList<>();
    private boolean enabled = false;

    public BloodMoon(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDayStart(NightEvent event) {

        if (event.getNumber() == getGame().getConfig().getValue(START_DAY)) {

            BloodMoonEvent bloodMoonEvent = new BloodMoonEvent();
            Bukkit.getPluginManager().callEvent(bloodMoonEvent);

            if (bloodMoonEvent.isCancelled()) {
                return;
            }
            enabled = true;
            Bukkit.broadcastMessage(getGame().translate(Prefix.ORANGE, "werewolf.random_events.blood_moon.start"));
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnnouncement(AnnouncementDeathEvent event) {

        if (!enabled) {
            return;
        }

        if (getGame().isDay(Day.DAY)) {
            return;
        }

        event.setCancelled(true);
        this.announcementDeathEvents.add(0, event);
        this.playerWWList.add(event.getPlayerWW());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        if (!enabled) {
            return;
        }

        if (getGame().isDay(Day.DAY)) {
            return;
        }

        if (this.playerWWList.stream()
                .noneMatch(playerWW -> playerWW.getUUID().equals(event.getPlayerUUID()))) {
            return;
        }

        event.setTabVisibility(false);
    }

    @EventHandler
    public void onLoverDeathMessage(AnnouncementLoverDeathEvent event) {

        if (!enabled) {
            return;
        }

        if (getGame().isDay(Day.DAY)) {
            return;
        }

        this.loversList.add(event);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDay(DayEvent event) {

        if (!enabled) {
            return;
        }

        this.playerWWList.clear();

        this.announcementDeathEvents.forEach(announcementDeathEvent -> {
            Formatter[] formatters = (Formatter[]) ArrayUtils.addAll(announcementDeathEvent.getFormatters().toArray(new Formatter[0]),
                    new Formatter[] { Formatter.player(announcementDeathEvent.getPlayerName()),
                            Formatter.role(this.getGame().translate(announcementDeathEvent.getRole())) });

            if (this.loversList.stream()
                    .anyMatch(announcementLoverDeathEvent -> announcementLoverDeathEvent
                            .getPlayerWW().equals(announcementDeathEvent.getPlayerWW()))) {
                announcementDeathEvent.getTargetPlayer()
                        .sendMessageWithKey("werewolf.lovers.lover.lover_death",
                                Formatter.player(announcementDeathEvent.getPlayerWW().getName()));
            }

            announcementDeathEvent.getTargetPlayer().sendMessageWithKey("werewolf.utils.bar");
            announcementDeathEvent.getTargetPlayer().sendMessageWithKey(Prefix.RED, announcementDeathEvent.getFormat(), formatters);
            announcementDeathEvent.getTargetPlayer().sendMessageWithKey("werewolf.utils.bar");
            announcementDeathEvent.getTargetPlayer().sendSound(Sound.AMBIENCE_THUNDER);

            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(announcementDeathEvent.getPlayerWW()));
        });

        this.announcementDeathEvents.clear();
        this.loversList.clear();
        this.playerWWList.clear();

        Bukkit.broadcastMessage(getGame().translate(Prefix.ORANGE, "werewolf.random_events.blood_moon.end"));
        this.enabled = false;

    }

    @Override
    public void second() {

        if (!enabled) {
            return;
        }

        getGame().getAlivePlayersWW()
                .stream()
                .filter(playerWW1 -> playerWW1.getRole().isWereWolf())
                .map(IPlayerWW::getUUID)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> {
                    getGame().getAlivePlayersWW()
                            .stream()
                            .filter(playerWW -> playerWW.getRole().isWereWolf())
                            .forEach(playerWW -> player.playEffect(playerWW.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK));
                });
    }
}

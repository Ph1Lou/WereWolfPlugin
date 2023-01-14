package fr.ph1lou.werewolfplugin.statistiks;

import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;
import fr.ph1lou.werewolfapi.annotations.TellableStoryEvent;
import fr.ph1lou.werewolfapi.annotations.statistics.StatisticsEvent;
import fr.ph1lou.werewolfapi.annotations.statistics.StatisticsExtraInfo;
import fr.ph1lou.werewolfapi.annotations.statistics.StatisticsExtraInt;
import fr.ph1lou.werewolfapi.annotations.statistics.StatisticsPlayer;
import fr.ph1lou.werewolfapi.annotations.statistics.StatisticsTarget;
import fr.ph1lou.werewolfapi.annotations.statistics.StatisticsTargets;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.statistics.impl.GameReview;
import fr.ph1lou.werewolfapi.statistics.impl.RegisteredAction;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Events implements Listener {

    private final Main main;

    public Events(Main main) {
        this.main = main;

        main.getRegisterManager().getEventsClass()
                .forEach(eventClass -> Bukkit.getPluginManager().registerEvent(eventClass.getClazz(),
                        this,
                        EventPriority.MONITOR,
                        (listener, event) -> {

                            StatisticsEvent statisticsEvent = eventClass.getClazz().getAnnotation(StatisticsEvent.class);
                            WereWolfAPI api = main.getWereWolfAPI();

                            IPlayerWW playerWW =  getValue(IPlayerWW.class,
                                    eventClass.getClazz(),
                                    event,
                                    StatisticsPlayer.class);

                            String extraInfo = getValue(String.class,
                                    eventClass.getClazz(),
                                    event,
                                    StatisticsExtraInfo.class);

                            Integer extraInt =  getValue(Integer.class,
                                    eventClass.getClazz(),
                                    event,
                                    StatisticsExtraInt.class);

                            IPlayerWW targetWW = getValue(IPlayerWW.class,
                                    eventClass.getClazz(),
                                    event,
                                    StatisticsTarget.class);

                            Set<IPlayerWW> targetsWW = targetWW != null ? Sets.newHashSet(targetWW) : getValue(Set.class,
                                    eventClass.getClazz(),
                                    event,
                                    StatisticsTargets.class);

                            main.getCurrentGameReview()
                                    .addRegisteredAction(new RegisteredAction(statisticsEvent.key(),
                                            playerWW,
                                            targetsWW,
                                            api.getTimer(),
                                            extraInfo,
                                            extraInt)
                                            .setActionableStory(eventClass.getClazz().getAnnotation(TellableStoryEvent.class) != null));
                        }, main, true));

    }

    private <T> T getValue(Class<T> returnedClazz, Class<Event> eventClass, Event event, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(eventClass.getMethods())
                .filter(method -> method.getAnnotation(annotationClass) != null)
                .findFirst()
                .map(method -> {
                    try {
                        Object object = method.invoke(event);

                        Class<?> objectClass = object.getClass();
                        if(objectClass.isPrimitive()){ // Handle Integer and int
                            objectClass = Primitives.wrap(objectClass);
                        }

                        if(returnedClazz.isInterface() &&
                                Arrays.stream(objectClass.getInterfaces())
                                        .anyMatch(aClass -> aClass.isAssignableFrom(returnedClazz))){
                            return (T)object;
                        }

                        if(objectClass.isAssignableFrom(returnedClazz)){
                            return (T)object;
                        }
                        Bukkit.getLogger()
                                .warning(String
                                        .format("%s class has method %s annotated with %s but not returned %s",
                                                eventClass.getName(),
                                                method.getName(),
                                                annotationClass.getName(),
                                                returnedClazz.getName()));
                        return null;
                    } catch (IllegalAccessException |
                             InvocationTargetException e) {
                        Bukkit.getLogger()
                                .warning(String
                                        .format("%s class has method %s annotated with %s but not callable with no arg",
                                                eventClass.getName(),
                                                method.getName(),
                                                annotationClass.getName()));
                        return null;
                    }
                })
                .orElse(null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWin(WinEvent event) {
        main.getCurrentGameReview().end(event.getRole(), event.getPlayers());
        StatistiksUtils.postGame(main, main.getCurrentGameReview());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameStart(StartEvent event) {
        UUID serverUUID;
        try{
            serverUUID = UUID.fromString(Objects.requireNonNull(main.getConfig().getString("server_uuid")));
        }
        catch (Exception e){
            serverUUID = UUID.randomUUID();
            main.getConfig().set("server_uuid", serverUUID);
        }

        main.setCurrentGameReview(new GameReview(main.getWereWolfAPI(), serverUUID));

    }


}

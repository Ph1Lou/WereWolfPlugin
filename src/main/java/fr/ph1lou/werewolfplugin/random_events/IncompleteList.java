package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.random_events.IncompleteListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.RequestSeeWereWolfListEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Event(key = EventBase.INCOMPLETE_LIST, loreKey = "werewolf.random_events.incomplete_list.description")
public class IncompleteList extends ListenerWerewolf {

    private final Map<IPlayerWW, List<IPlayerWW>> forgetWerewolves = new HashMap<>();

    public IncompleteList(WereWolfAPI game) {
        super(game);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWerewolfList(WereWolfListEvent event){

        IncompleteListEvent incompleteListEvent = new IncompleteListEvent();

        Bukkit.getPluginManager().callEvent(incompleteListEvent);

        if(incompleteListEvent.isCancelled()){
            return;
        }

        for (IPlayerWW playerWW1 : this.getGame().getPlayersWW()) {

            RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(playerWW1.getUUID());
            Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);

            if(requestSeeWereWolfListEvent.isAccept()){

                this.forgetWerewolves.put(playerWW1, new ArrayList<>());

                for (IPlayerWW playerWW2 : this.getGame().getPlayersWW()) {

                    AppearInWereWolfListEvent appearInWereWolfListEvent =
                            new AppearInWereWolfListEvent(playerWW2.getUUID(), playerWW1.getUUID());
                    Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);

                    if (appearInWereWolfListEvent.isAppear()) {
                        this.forgetWerewolves.get(playerWW1).add(playerWW2);
                    }
                }
                Collections.shuffle(this.forgetWerewolves.get(playerWW1), this.getGame().getRandom());
                AtomicInteger werewolfSize = new AtomicInteger((this.forgetWerewolves.get(playerWW1).size() - 1) / 2);
                //Remove half of werewolves
                this.forgetWerewolves.get(playerWW1).removeIf(playerWW -> {
                    if(playerWW.equals(playerWW1)){
                        return true;
                    }
                    return werewolfSize.getAndDecrement() > 0;
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAppearInWerewolfListEvent(AppearInWereWolfListEvent event){

        this.getGame().getPlayerWW(event.getRequesterUUID())
                .ifPresent(playerWW1 -> this.getGame().getPlayerWW(event.getPlayerUUID())
                        .ifPresent(playerWW2 -> {
                            if(this.forgetWerewolves.containsKey(playerWW1) &&
                                    this.forgetWerewolves.get(playerWW1).contains(playerWW2)){
                                event.setAppear(false);
                            }
                        }));
    }
}

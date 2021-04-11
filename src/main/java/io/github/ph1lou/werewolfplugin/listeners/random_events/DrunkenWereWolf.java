package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DrunkenWereWolf extends ListenerManager {

    List<UUID> fakeList = new ArrayList<>();
    private IPlayerWW temp;


    public DrunkenWereWolf(GetWereWolfAPI main) {
        super(main);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onWereWolfList(WereWolfListEvent event) {

        if (this.temp != null) {
            return;
        }

        WereWolfAPI game = main.getWereWolfAPI();

        List<IPlayerWW> playerWWS = game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        this.temp = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

        List<UUID> fakeListPool = game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getUUID)
                .collect(Collectors.toList());

        if (fakeListPool.size() < playerWWS.size()) {
            this.temp = null;
            Bukkit.getLogger().warning("[WereWolfPlugin] Failure in FakeList Creation For Drunken Werewolf");
            return;
        }

        Collections.shuffle(fakeListPool, game.getRandom());

        this.fakeList.addAll(fakeListPool.subList(0, playerWWS.size()));

        this.fakeList.add(this.temp.getUUID());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAppearInWereWolfList(AppearInWereWolfListEvent event) {

        if (this.temp == null) return;

        if (!event.getRequesterUUID().equals(this.temp.getUUID())) {
            return;
        }

        event.setAppear(this.fakeList.contains(event.getPlayerUUID()));
    }


    @EventHandler
    public void onGameStop(StopEvent event) {
        temp = null;
        this.fakeList.clear();
    }

    @EventHandler
    public void onGameStart(StartEvent event) {
        temp = null;
        this.fakeList.clear();
    }
}

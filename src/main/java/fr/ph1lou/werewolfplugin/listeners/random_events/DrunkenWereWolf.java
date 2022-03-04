package fr.ph1lou.werewolfplugin.listeners.random_events;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.RolesBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.random_events.DrunkenWereWolfEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class DrunkenWereWolf extends ListenerManager {

    private final Set<UUID> fakeList = new HashSet<>();
    private IPlayerWW temp;


    public DrunkenWereWolf(GetWereWolfAPI main) {
        super(main);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onWereWolfList(WereWolfListEvent event) {

        if (this.temp != null) {
            return;
        }

        WereWolfAPI game = this.getGame();

        List<IPlayerWW> playerWWS = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        this.temp = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

        DrunkenWereWolfEvent event1 = new DrunkenWereWolfEvent(this.temp);

        Bukkit.getPluginManager().callEvent(event1);

        if (event1.isCancelled()) {
            this.temp = null;
            return;
        }

        List<UUID> fakeListPool = game.getPlayersWW().stream()
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

        this.fakeList.addAll(game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isKey(RolesBase.ALPHA_WEREWOLF.getKey()))
                .map(IPlayerWW::getUUID)
                .collect(Collectors.toList()));

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

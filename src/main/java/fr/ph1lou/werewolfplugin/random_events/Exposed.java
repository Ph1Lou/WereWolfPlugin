package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.random_events.ExposedEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Event(key = EventBase.EXPOSED,
        loreKey = "werewolf.random_events.exposed.description",
        timers = {@Timer(key = Exposed.TIMER_START_1, defaultValue = 65*60, meetUpValue = 20*60, step = 30),
                @Timer(key = Exposed.TIMER_START_2, defaultValue = 30*60, meetUpValue = 15*60, step = 30),
                @Timer(key = Exposed.PERIOD, defaultValue = 15*60, meetUpValue = 15*60, step = 30)})
public class Exposed extends ListenerWerewolf {
  
    private IPlayerWW temp = null;
    public static final String TIMER_START_1 = "werewolf.random_events.exposed.timer_start_1";
    public static final String TIMER_START_2 = "werewolf.random_events.exposed.timer_start_2";
    public static final String PERIOD = "werewolf.random_events.exposed.period";

    public Exposed(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {
                    IPlayerWW playerWW = announce();

                    if (temp == null && playerWW != null) {
                        temp = playerWW;
                        BukkitUtils.scheduleSyncDelayedTask(() -> {
                            if (game.isState(StateGame.GAME)) {
                                if (isRegister()) {
                                    announce();
                                    temp = null;
                                    register(false);
                                }
                            }
                        }, game.getConfig().getTimerValue(TIMER_START_2) * 20L);
                    }

                }
            }
        }, (long) (20L * game.getConfig().getTimerValue(TIMER_START_1) + game.getRandom().nextDouble() * game.getConfig().getTimerValue(PERIOD) * 20));
    }

    @Nullable
    private IPlayerWW announce() {

        WereWolfAPI game = this.getGame();

        List<IPlayerWW> playerWWS = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.equals(temp))
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return null;

        IPlayerWW playerWW = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

        List<IRole> role1List = game.getPlayersWW().stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(playerWW1 -> !playerWW.equals(playerWW1))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.isCamp(playerWW.getRole().getCamp()))
                .collect(Collectors.toList());

        if (role1List.isEmpty()) return null;

        IRole role1 = role1List.get((int) Math.floor(game.getRandom().nextDouble() * role1List.size()));

        List<IRole> role2List = game.getPlayersWW().stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(playerWW1 -> !playerWW.equals(playerWW1))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(role1))
                .collect(Collectors.toList());

        if (role2List.isEmpty()) return null;

        IRole role2 = role2List.get((int) Math.floor(game.getRandom().nextDouble() * role2List.size()));

        List<String> roles = new ArrayList<>(Arrays.asList(role1.getKey(),
                role2.getKey(),
                playerWW.getRole().getDisplayRole()));

        Collections.shuffle(roles, game.getRandom());

        ExposedEvent exposedEvent = new ExposedEvent(playerWW,
                new ArrayList<>(Arrays.asList(role1.getPlayerWW(),
                        role2.getPlayerWW(),
                        playerWW)));

        Bukkit.getPluginManager().callEvent(exposedEvent);

        if (!exposedEvent.isCancelled()) {
            roles = roles.stream().map(game::translate).collect(Collectors.toList());
            Bukkit.broadcastMessage(game.translate("werewolf.random_events.exposed.message",
                    Formatter.player(playerWW.getName()),
                    Formatter.format("&role1&",roles.get(0)),
                    Formatter.format("&role2&",roles.get(1)),
                    Formatter.format("&role3&",roles.get(2))));
        }

        return playerWW;
    }

    @EventHandler
    public void onGameStop(StopEvent event) {
        temp = null;
    }

    @EventHandler
    public void onGameStart(StartEvent event) {
        temp = null;
    }

}

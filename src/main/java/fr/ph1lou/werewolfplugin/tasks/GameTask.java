package fr.ph1lou.werewolfplugin.tasks;


import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import fr.ph1lou.werewolfplugin.game.Configuration;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.scoreboards.ScoreBoard;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.stream.Stream;


public class GameTask extends BukkitRunnable {

    private final GameManager game;

    public GameTask(GameManager game) {
        this.game = game;
    }

    @Override
    public void run() {

        if (game.isState(StateGame.END)) {
            ((ScoreBoard) game.getScoreboard()).updateBoard();
            cancel();
            return;
        }

        World world = game.getMapManager().getWorld();
        ((ScoreBoard) game.getScoreboard()).updateBoard();


        game.getPlayersWW()
                .forEach(playerWW -> {
            try {
                playerWW.getRole().second();
                playerWW.second();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        game.getLoversManager().getLovers().forEach(lover -> {
            try {
                lover.second();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        Register.get().getRandomEventsRegister().stream()
                .map(e -> e.getMetaDatas().key())
                .map(k -> this.game.getListenersManager().getRandomEvent(k))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(ListenerWerewolf::isRegister)
                .forEach(e -> {
                    try {
                        e.second();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });

        Register.get().getScenariosRegister().stream()
                .map(e -> e.getMetaDatas().key())
                .map(k -> this.game.getListenersManager().getScenario(k))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(ListenerWerewolf::isRegister)
                .forEach(e -> {
                    try {
                        e.second();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });

        Register.get().getConfigsRegister().stream()
                .map(e -> e.getMetaDatas().config().key())
                .map(k -> this.game.getListenersManager().getConfiguration(k))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(ListenerWerewolf::isRegister)
                .forEach(e -> {
                    try {
                        e.second();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });

        Register.get().getTimersRegister().stream()
                .map(e -> e.getMetaDatas().key())
                .map(k -> this.game.getListenersManager().getTimer(k))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(ListenerWerewolf::isRegister)
                .forEach(e -> {
                    try {
                        e.second();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });

        if (game.getConfig().getTimerValue(TimerBase.BORDER_BEGIN) < 0) {
            //World Border
            IConfiguration config = game.getConfig();
            WorldBorder worldBorder = world.getWorldBorder();

            if (config.getBorderMax() != config.getBorderMin()) {
                worldBorder.setSize(config.getBorderMin(), (long) ((long) Math.abs(worldBorder.getSize() - config.getBorderMin()) / config.getBorderSpeed()));
                config.setBorderMax((int) (worldBorder.getSize()));
            }
        }


        world.setTime((long) (world.getTime() + 20 *
                (600f /
                        game.getConfig().getTimerValue(
                                TimerBase.DAY_DURATION) - 1)));

        game.setTimer(game.getTimer() + 1);

        Stream.concat(Register.get().getTimersRegister()
                                .stream()
                                .map(Wrapper::getMetaDatas),
                        Stream.concat(Register.get().getConfigsRegister()
                                        .stream()
                                        .map(Wrapper::getMetaDatas)
                                        .flatMap(configuration -> Stream.of(configuration.timers())),
                                Stream.concat(Register.get().getRolesRegister().stream()
                                                .map(Wrapper::getMetaDatas)
                                                .flatMap(role -> Stream.of(role.timers())),
                                        Stream.concat(Register.get().getRandomEventsRegister().stream()
                                                .map(Wrapper::getMetaDatas)
                                                .flatMap(role -> Stream.of(role.timers())), Register.get().getScenariosRegister().stream()
                                                        .map(Wrapper::getMetaDatas).flatMap(scenario -> Stream.of(scenario.timers()))))))
                .forEach(timerRegister -> {

                    if (timerRegister.decrement() ||
                            (timerRegister.decrementAfterRole() &&
                                    !game.getConfig().isConfigActive(ConfigBase.TROLL_ROLE) &&
                                    game.getConfig().getTimerValue(TimerBase.ROLE_DURATION) < 0) ||
                            (!timerRegister.decrementAfterTimer().isEmpty() &&
                                    game.getConfig().getTimerValue(timerRegister.decrementAfterTimer()) < 0)) {
                        if (game.getConfig().getTimerValue(timerRegister.key()) == 0) {
                            try {
                                if (!timerRegister.onZero().equals(Event.class)) {
                                    Bukkit.getPluginManager().callEvent(
                                            timerRegister.onZero().getConstructor()
                                                    .newInstance()
                                    );
                                }
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                     NoSuchMethodException ignored) {
                            }
                        }
                        ((Configuration) game.getConfig()).decreaseTimer(timerRegister.key());
                    }
                });

    }

}


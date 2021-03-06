package io.github.ph1lou.werewolfplugin.registers;

import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.events.TrollEvent;
import io.github.ph1lou.werewolfapi.events.TrollLoverEvent;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.BorderStartEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.DiggingEndEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.InvulnerabilityEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.PVPEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteBeginEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.AutoAngelEvent;
import io.github.ph1lou.werewolfapi.events.roles.rival.RivalEvent;
import io.github.ph1lou.werewolfapi.events.roles.wild_child.AutoModelEvent;
import io.github.ph1lou.werewolfapi.registers.TimerRegister;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;

import java.util.ArrayList;
import java.util.List;

public class TimersRegister {

    public static List<TimerRegister> registerTimers() {

        List<TimerRegister> timersRegister = new ArrayList<>();

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.INVULNERABILITY.getKey())
                        .setDefaultValue(30)
                        .onZero(wereWolfAPI -> {
                            Bukkit.getOnlinePlayers()
                                    .forEach(player -> {
                                        player.sendMessage(wereWolfAPI.translate("werewolf.announcement.invulnerability"));
                                        Sound.GLASS.play(player);
                                    });
                            Bukkit.getPluginManager().callEvent(new InvulnerabilityEvent());
                        })
                        .addPredicate(wereWolfAPI -> true));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.ROLE_DURATION.getKey())
                        .setDefaultValue(1200)
                        .addPredicate(wereWolfAPI -> true)
                        .onZero((wereWolfAPI) -> {
                            if (wereWolfAPI.getConfig().isTrollSV()) {
                                Bukkit.getPluginManager().callEvent(new TrollEvent());
                            } else {
                                Bukkit.getPluginManager().callEvent(new RepartitionEvent());
                            }
                        }));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.PVP.getKey())
                        .setDefaultValue(1500)
                        .onZero(wereWolfAPI -> {
                            Bukkit.getPluginManager().callEvent(new PVPEvent());
                            wereWolfAPI.getMapManager().getWorld().setPVP(true);
                            Bukkit.getOnlinePlayers()
                                    .forEach(player -> {
                                        player.sendMessage(wereWolfAPI.translate("werewolf.announcement.pvp"));
                                        Sound.DONKEY_ANGRY.play(player);
                                    });
                        })
                        .addPredicate(wereWolfAPI -> true));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.WEREWOLF_LIST.getKey())
                        .addPredicate(wereWolfAPI -> wereWolfAPI.getConfig().getTimerValue(TimerBase.ROLE_DURATION.getKey()) < 0
                                && !wereWolfAPI.getConfig().isTrollSV())
                        .onZero(wereWolfAPI -> {
                            wereWolfAPI.getPlayersWW().stream()
                                    .filter(playerWW -> !playerWW.isState(StatePlayer.DEATH))
                                    .filter(playerWW -> playerWW.getRole().isWereWolf())
                                    .forEach(playerWW -> {
                                        playerWW.sendMessageWithKey("werewolf.role.werewolf.see_others");
                                        Sound.WOLF_HOWL.play(playerWW);
                                        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
                                    });
                            Bukkit.getPluginManager().callEvent(new WereWolfListEvent());
                        })
                        .setDefaultValue(600));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.VOTE_BEGIN.getKey())
                        .addPredicate(wereWolfAPI -> true)
                        .onZero(wereWolfAPI -> Bukkit.getPluginManager().callEvent(new VoteBeginEvent()))
                        .setDefaultValue(2400));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.BORDER_BEGIN.getKey())
                        .setDefaultValue(3600)
                        .onZero(wereWolfAPI -> {
                            Bukkit.getOnlinePlayers()
                                    .forEach(player -> {
                                        player.sendMessage(wereWolfAPI.translate("werewolf.announcement.border"));
                                        Sound.FIREWORK_LAUNCH.play(player);
                                    });
                            Bukkit.getPluginManager().callEvent(new BorderStartEvent());
                        })
                        .addPredicate(wereWolfAPI -> {

                            if (wereWolfAPI.getConfig().getTimerValue(TimerBase.BORDER_BEGIN.getKey()) >= 0)
                                return true;

                            IConfiguration config = wereWolfAPI.getConfig();
                            WorldBorder worldBorder = wereWolfAPI.getMapManager().getWorld().getWorldBorder();

                            if (config.getBorderMax() !=
                                    config.getBorderMin()) {

                                worldBorder.setSize(config.getBorderMin(), (long) ((long) Math.abs(worldBorder.getSize() - config.getBorderMin()) / config.getBorderSpeed()));
                                config.setBorderMax((int) (worldBorder.getSize()));
                            }

                            return true;
                        }));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.DIGGING.getKey())
                        .addPredicate(wereWolfAPI -> true)
                        .onZero(wereWolfAPI -> {
                            Bukkit.getOnlinePlayers()
                                    .forEach(player -> {
                                        player.sendMessage(wereWolfAPI.translate("werewolf.announcement.mining"));
                                        Sound.ANVIL_BREAK.play(player);
                                    });
                            Bukkit.getPluginManager().callEvent(new DiggingEndEvent());
                        })
                        .setDefaultValue(4200));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.VOTE_DURATION.getKey())
                        .setDefaultValue(180));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.CITIZEN_DURATION.getKey())
                        .setDefaultValue(60));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.RIVAL_DURATION.getKey())
                        .setDefaultValue(2400)
                        .onZero(wereWolfAPI -> Bukkit.getPluginManager().callEvent(new RivalEvent()))
                        .addPredicate(wereWolfAPI -> wereWolfAPI.getConfig().getTimerValue(TimerBase.LOVER_DURATION.getKey()) < 0));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.MODEL_DURATION.getKey())
                        .addPredicate(wereWolfAPI -> wereWolfAPI.getConfig().getTimerValue(TimerBase.ROLE_DURATION.getKey()) < 0
                                && !wereWolfAPI.getConfig().isTrollSV())
                        .onZero(wereWolfAPI -> Bukkit.getPluginManager().callEvent(new AutoModelEvent()))
                        .setDefaultValue(240));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.LOVER_DURATION.getKey())
                        .addPredicate(wereWolfAPI -> wereWolfAPI.getConfig().getTimerValue(TimerBase.ROLE_DURATION.getKey()) < 0
                                && !wereWolfAPI.getConfig().isTrollSV())
                        .onZero(wereWolfAPI -> {
                            if (wereWolfAPI.getConfig().isTrollLover()) {
                                Bukkit.getPluginManager().callEvent(new TrollLoverEvent());
                            } else {
                                wereWolfAPI.getLoversManager().repartition();
                            }
                        })
                        .setDefaultValue(240));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.ANGEL_DURATION.getKey())
                        .addPredicate(wereWolfAPI -> wereWolfAPI.getConfig().getTimerValue(TimerBase.ROLE_DURATION.getKey()) < 0
                                && !wereWolfAPI.getConfig().isTrollSV())
                        .onZero(wereWolfAPI -> Bukkit.getPluginManager().callEvent(new AutoAngelEvent()))
                        .setDefaultValue(240));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.POWER_DURATION.getKey())
                        .setDefaultValue(240));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.FOX_SMELL_DURATION.getKey())
                        .setDefaultValue(90));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.SUCCUBUS_DURATION.getKey())
                        .setDefaultValue(180));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.WEREWOLF_CHAT_DURATION.getKey())
                        .setDefaultValue(30));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.DAY_DURATION.getKey())
                        .setDefaultValue(300));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimerBase.AUTO_RESTART_DURATION.getKey())
                        .setDefaultValue(60));

        return timersRegister;


    }

}

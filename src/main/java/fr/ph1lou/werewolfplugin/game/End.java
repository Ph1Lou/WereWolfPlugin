package fr.ph1lou.werewolfplugin.game;


import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import fr.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import fr.ph1lou.werewolfapi.events.lovers.AroundLoverEvent;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.ICamp;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.Main;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class End {

    private final GameManager game;


    public End(GameManager game) {
        this.game = game;
    }

    public void checkVictory() {

        if (game.isDebug()) {
            return;
        }

        if (game.getConfig().isConfigActive(ConfigBase.TROLL_ROLE)) return;

        if (game.isState(StateGame.END)) return;

        if (game.getPlayersWW().stream().anyMatch(iPlayerWW -> iPlayerWW.isState(StatePlayer.JUDGEMENT))) {
            return;
        }

        List<IRole> iRolesAlive = game.getAlivePlayersWW()
                .stream()
                .map(IPlayerWW::getRole)
                .collect(Collectors.toList());


        if (iRolesAlive.isEmpty()) {
            end("werewolf.end.death");
            return;
        }

        String key = game.getLoversManager().getLovers()
                .stream()
                .filter(lover -> lover.isKey(LoverBase.AMNESIAC_LOVER) || lover.isKey(LoverBase.LOVER))
                .filter(ILover::isAlive)
                .map(lover -> {
                    Set<IPlayerWW> lovers = new HashSet<>(lover.getLovers());

                    AroundLoverEvent event = new AroundLoverEvent(lovers);
                    Bukkit.getPluginManager().callEvent(event);

                    if (event.getPlayerWWS().size() == iRolesAlive.size()) {
                        return lover.getKey();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (key != null) {
            end(key);
            return;
        }

        WinConditionsCheckEvent winConditionsCheckEvent = new WinConditionsCheckEvent();
        Bukkit.getPluginManager().callEvent(winConditionsCheckEvent);

        if (winConditionsCheckEvent.isWin()) {
            String winnerTeam = winConditionsCheckEvent.getVictoryTeam();

            if (winnerTeam.isEmpty()) return;

            end(winnerTeam);
            return;
        }

        if (iRolesAlive.size() == 1 && iRolesAlive.stream().allMatch(ICamp::isNeutral)) {
            if (iRolesAlive.get(0).isSolitary()) {
                end(iRolesAlive.get(0).getKey(), game.translate(iRolesAlive.get(0).getKey()) + game.translate("werewolf.end.solitary"));
            } else {
                end(iRolesAlive.get(0).getKey());
            }
        } else if (iRolesAlive.stream().noneMatch(ICamp::isNeutral)) {

            if (iRolesAlive.stream().allMatch(ICamp::isWereWolf)) {
                end(Category.WEREWOLF.getKey());
            } else if (iRolesAlive.stream().noneMatch(ICamp::isWereWolf)) {
                end(Category.VILLAGER.getKey());
            }
        }
    }

    private void end(String winner) {
        this.end(winner, game.translate(winner));
    }

    private void end(String winner, String subtitlesVictory) {

        game.cleanSchedules();

        Bukkit.getPluginManager().callEvent(new WinEvent(winner,
                new HashSet<>(game.getAlivePlayersWW())));

        game.setState(StateGame.END);

        game.getConfig().setConfig(ConfigBase.CHAT, true);

        for (IPlayerWW playerWW1 : game.getPlayersWW()) {

            String role = game.translate(playerWW1.getDeathRole());
            String playerName = playerWW1.getName();
            StringBuilder sb = new StringBuilder();

            if (playerWW1.isState(StatePlayer.DEATH)) {
                sb.append(game.translate("werewolf.end.reveal_death",
                        Formatter.player(playerName),
                        Formatter.role(role)));
            } else {
                sb.append(game.translate("werewolf.end.reveal",
                        Formatter.player(playerName),
                        Formatter.role(role)));
            }
            playerWW1.getDeathRoles().forEach(deathRole -> {
                if (!playerWW1.getDeathRole().equals(deathRole)) {
                    sb.append(" => ").append(game.translate(deathRole));
                }
            });

            EndPlayerMessageEvent endPlayerMessageEvent = new EndPlayerMessageEvent(playerWW1, sb);
            Bukkit.getPluginManager().callEvent(endPlayerMessageEvent);

            Bukkit.broadcastMessage(sb.toString());
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate(Prefix.ORANGE, "werewolf.end.message",
                    Formatter.format("&winner&", subtitlesVictory)));
            VersionUtils.getVersionUtils().sendTitle(p, game.translate("werewolf.end.victory"), subtitlesVictory, 20, 60, 20);
            TextComponent msg = Utils.getDiscord(game);
            p.spigot().sendMessage(msg);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), game::stopGame, 20L * game.getConfig().getTimerValue(TimerBase.AUTO_RESTART_DURATION));
        Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.announcement.restart",
                Formatter.timer(game, TimerBase.AUTO_RESTART_DURATION)));
    }


}

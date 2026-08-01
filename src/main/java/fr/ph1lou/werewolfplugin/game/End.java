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
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.ICamp;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.statistics.impl.GameReview;
import fr.ph1lou.werewolfapi.statistics.interfaces.IRegisteredAction;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.Register;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
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

        GameReview review = Main.getPlugin(Main.class).getStatisticsEvents() != null
                ? Main.getPlugin(Main.class).getStatisticsEvents().getGameReview() : null;

        Map<UUID, String> reviewNames = new HashMap<>();
        if (review != null) {
            for (IPlayerWW p : game.getPlayersWW()) {
                reviewNames.put(p.getReviewUUID(), p.getName());
            }
        }

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

            String revealMessage = sb.toString();
            String hover = buildHoverText(playerWW1, review, reviewNames);
            Bukkit.getConsoleSender().sendMessage(revealMessage);
            Bukkit.getOnlinePlayers().forEach(p ->
                    VersionUtils.getVersionUtils().sendEndMessage(p, revealMessage, playerName, hover));
        }

        List<String> triggeredEvents = Register.get().getRandomEventsRegister().stream()
                .filter(w -> game.getListenersManager().getRandomEvent(w.getMetaDatas().key())
                        .map(ListenerWerewolf::wasTriggered).orElse(false))
                .map(w -> game.translate(w.getMetaDatas().key()))
                .collect(Collectors.toList());

        if (!triggeredEvents.isEmpty()) {
            Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.end.random_events",
                    Formatter.format("&events&", String.join("§f, ", triggeredEvents))));
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

    private static final int MAX_HOVER_ACTIONS = 200;
    private static final int MAX_HOVER_LENGTH = 24000;

    private String buildHoverText(IPlayerWW playerWW, GameReview review,
                                  Map<UUID, String> reviewNames) {

        if (review == null) {
            return "";
        }

        UUID reviewUUID = playerWW.getReviewUUID();
        List<String> lines = new ArrayList<>();
        lines.add(game.translate("werewolf.end.stats_hover_title",
                Formatter.player(playerWW.getName())));

        List<String> actionLines = new ArrayList<>();

        for (IRegisteredAction action : review.getRegisteredActions()) {
            if (action.getUuid() == null || !action.getUuid().equals(reviewUUID)) {
                continue;
            }

            String label = translateAction(action.getEvent());
            String time = Utils.conversion(action.getTimer());
            StringBuilder line = new StringBuilder("§7").append(time).append(" §f").append(label);

            if (action.getUuidS() != null && !action.getUuidS().isEmpty()) {
                List<String> targets = new ArrayList<>();
                for (UUID targetUUID : action.getUuidS()) {
                    String targetName = reviewNames.get(targetUUID);
                    if (targetName != null) {
                        targets.add(targetName);
                    }
                }
                if (!targets.isEmpty()) {
                    line.append(" §b-> §e").append(String.join("§f, §e", targets));
                }
            }

            actionLines.add(line.toString());
        }

        if (actionLines.size() > MAX_HOVER_ACTIONS) {
            int skipped = actionLines.size() - MAX_HOVER_ACTIONS;
            lines.add("§7... (" + skipped + " more)");
            lines.addAll(actionLines.subList(actionLines.size() - MAX_HOVER_ACTIONS, actionLines.size()));
        } else {
            lines.addAll(actionLines);
        }

        String result = String.join("\n", lines);
        if (result.length() > MAX_HOVER_LENGTH) {
            result = result.substring(0, MAX_HOVER_LENGTH) + "\n§7...";
        }
        return result;
    }

    private String translateAction(String eventKey) {

        String suffix = eventKey.startsWith("werewolf.")
                ? eventKey.substring("werewolf.".length()) : eventKey;
        String label = game.translate("werewolf.statistics_action." + suffix);

        if (label == null || (label.startsWith("Message ") && (label.endsWith(" not found") || label.endsWith(" is not a string")))) {
            label = suffix.replace("_", " ");
            if (!label.isEmpty()) {
                label = Character.toUpperCase(label.charAt(0)) + label.substring(1);
            }
        }
        return label;
    }


}

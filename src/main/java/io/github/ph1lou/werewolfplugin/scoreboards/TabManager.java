package io.github.ph1lou.werewolfplugin.scoreboards;

import io.github.ph1lou.werewolfapi.ModerationManagerAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.ConfigsBase;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.events.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.RequestSeeWereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.UpdateModeratorNameTag;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public class TabManager {

    final WereWolfAPI game;

    public TabManager(WereWolfAPI game) {
        this.game = game;
    }

    public void registerPlayer(Player player) {

        String name = player.getName();
        player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
        Scoreboard score = player.getScoreboard();

        for (Player player1 : Bukkit.getOnlinePlayers()) {

            Scoreboard scoreBoard = player1.getScoreboard();

            if (scoreBoard.getTeam(name) == null) {
                scoreBoard.registerNewTeam(name).addEntry(name);
            }

            String name1 = player1.getName();

            if (!player1.equals(player)) {
                score.registerNewTeam(name1).addEntry(name1);
            }
        }

        updatePlayerOthersAndHimself(player);
    }

    public void updatePlayerScoreBoard(Player player, List<UUID> uuids) {
        updatePlayerScoreBoard(player, uuids.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }


    public void updatePlayerScoreBoard(Player player, Collection<? extends Player> players) {
        players.forEach(player1 -> updatePlayerForOthers(player1, Collections.singleton(player)));
    }

    public void updatePlayerScoreBoard(Player player) {

        Bukkit.getOnlinePlayers()
                .forEach(player1 -> updatePlayerForOthers(player1, Collections.singleton(player)));
    }

    public void unregisterPlayer(Player player) {

        String name = player.getName();

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreBoard = player1.getScoreboard();
            Objects.requireNonNull(scoreBoard.getTeam(name)).unregister();
        }
    }

    public void updatePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerForOthers(player);
        }
    }

    public void updatePlayerOthersAndHimself(Player player) {
        updatePlayerScoreBoard(player);
        updatePlayerForOthers(player);
    }

    public void updatePlayerForOthers(UUID uuid) {

        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return;

        updatePlayerForOthers(player, Bukkit.getOnlinePlayers());
    }

    public void updatePlayerForOthers(Player player) {
        updatePlayerForOthers(player, Bukkit.getOnlinePlayers());
    }

    public void updatePlayerForOthers(Player player, Collection<? extends Player> players) {

        ModerationManagerAPI moderationManager = game.getModerationManager();

        StringBuilder sb = new StringBuilder();
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        if (moderationManager.getHosts().contains(uuid)) {
            sb.append(game.translate("werewolf.commands.admin.host.tag"));
        } else if (moderationManager.getModerators().contains(uuid)) {
            sb.append(game.translate("werewolf.commands.admin.moderator.tag"));
        } else if (moderationManager.getQueue().contains(uuid)) {
            if (game.isState(StateGame.LOBBY)) {
                sb.append(game.translate("werewolf.menu.rank.tag"));
            }
        }

        UpdatePlayerNameTag event = new UpdatePlayerNameTag(uuid, sb.toString(), " ", true);
        AppearInWereWolfListEvent appearInWereWolfListEvent = new AppearInWereWolfListEvent(uuid);
        UpdateModeratorNameTag updateModeratorNameTag =
                new UpdateModeratorNameTag(uuid, "", " ");

        Bukkit.getPluginManager().callEvent(event);
        Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);
        Bukkit.getPluginManager().callEvent(updateModeratorNameTag);

        players.forEach(player1 -> set(name, player1,
                event, appearInWereWolfListEvent.isAppear(),
                updateModeratorNameTag));
    }

    public void set(String name, Player player, UpdatePlayerNameTag event1, boolean appear, UpdateModeratorNameTag event2) {

        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam(name);
        StringBuilder sb = new StringBuilder(event1.getPrefix());
        if (team != null) {
            UUID uuid1 = player.getUniqueId();
            RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(uuid1);
            Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);

            if (appear && requestSeeWereWolfListEvent.isAccept()) {
                if (game.getConfig().getConfigValues()
                        .get(ConfigsBase.RED_NAME_TAG.getKey())) {
                    sb.append(ChatColor.DARK_RED);
                }
            }

            if (game.getModerationManager().getModerators().contains(uuid1)) {
                String string1 = event2.getSuffix() + event1.getSuffix();
                team.setSuffix(string1.substring(0, Math.min(16, string1.length())));
                String string2 = sb.toString() + event2.getPrefix();
                team.setPrefix(string2.substring(0, Math.min(16, string2.length())));
                VersionUtils.getVersionUtils().setTeamNameTagVisibility(team, true);

            } else {
                String string1 = event1.getSuffix();
                team.setSuffix(string1.substring(0, Math.min(16, string1.length())));
                String string2 = sb.toString();
                team.setPrefix(string2.substring(0, Math.min(16, string2.length())));
                VersionUtils.getVersionUtils().setTeamNameTagVisibility(team, event1.isVisibility());

            }
        }
    }


}

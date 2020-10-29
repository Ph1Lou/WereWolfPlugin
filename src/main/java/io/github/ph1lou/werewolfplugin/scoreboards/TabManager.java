package io.github.ph1lou.werewolfplugin.scoreboards;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.RequestSeeWereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.UpdateModeratorNameTag;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import io.github.ph1lou.werewolfapi.rolesattributs.InvisibleState;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.game.ModerationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class TabManager {

    final GameManager game;

    public TabManager(GameManager game) {
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

        updatePlayerScoreBoard(player, game.getBoards().keySet());

        updatePlayer(player.getUniqueId(), Bukkit.getOnlinePlayers());
    }

    public void updatePlayerScoreBoard(Player player, Collection<? extends UUID> uuids) {
        uuids.forEach(uuid -> updatePlayer(uuid, Collections.singleton(player)));
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
            updatePlayer(player.getUniqueId(), Bukkit.getOnlinePlayers());
        }
    }

    public void updatePlayer(UUID uuid) {
        updatePlayer(uuid, Bukkit.getOnlinePlayers());
    }

    public void updatePlayer(UUID uuid, Collection<? extends Player> players) {

        ModerationManager moderationManager = game.getModerationManager();

        StringBuilder sb = new StringBuilder();
        PlayerWW playerWW = game.getPlayerWW(uuid);
        Player player = Bukkit.getPlayer(uuid);
        boolean visibility = true;
        String name;

        if (playerWW == null && player == null) {
            return;
        }

        if (game.getConfig().getScenarioValues().get("werewolf.menu.scenarios.no_name_tag")) {
            visibility = false;
        }
        if (moderationManager.getHosts().contains(uuid)) {
            sb.append(game.translate("werewolf.commands.admin.host.tag"));
        } else if (moderationManager.getModerators().contains(uuid)) {
            sb.append(game.translate("werewolf.commands.admin.moderator.tag"));
        } else if (moderationManager.getQueue().contains(uuid)) {
            if (game.isState(StateLG.LOBBY)) {
                sb.append(game.translate("werewolf.menu.rank.tag"));
            }
        }
        if (playerWW != null) {

            Roles role = playerWW.getRole();

            if (visibility) {
                visibility = (!(role instanceof InvisibleState)) || !((InvisibleState) role).isInvisible();
            }
            name = playerWW.getName();
        } else name = player.getName();

        UpdatePlayerNameTag event = new UpdatePlayerNameTag(uuid, sb.toString(), " ", visibility);
        AppearInWereWolfListEvent appearInWereWolfListEvent = new AppearInWereWolfListEvent(uuid);
        UpdateModeratorNameTag updateModeratorNameTag = new UpdateModeratorNameTag(uuid, "", " ");
        Bukkit.getPluginManager().callEvent(event);
        Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);
        Bukkit.getPluginManager().callEvent(updateModeratorNameTag);

        players.forEach(player1 -> set(name, player1, event, appearInWereWolfListEvent.isAppear(), updateModeratorNameTag));
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
                sb.append(ChatColor.DARK_RED);
            }

            if (game.getModerationManager().getModerators().contains(uuid1)) {
                String string1 = event2.getSuffix() + event1.getSuffix();
                team.setSuffix(string1.substring(0, Math.min(16, string1.length())));
                String string2 = sb.toString() + event2.getPrefix();
                team.setPrefix(string2.substring(0, Math.min(16, string2.length())));
            } else {
                String string1 = event1.getSuffix();
                team.setSuffix(string1.substring(0, Math.min(16, string1.length())));
                String string2 = sb.toString();
                team.setPrefix(string2.substring(0, Math.min(16, string2.length())));
            }

            VersionUtils.getVersionUtils().setTeamNameTagVisibility(team, event1.isVisibility());
        }
    }


}

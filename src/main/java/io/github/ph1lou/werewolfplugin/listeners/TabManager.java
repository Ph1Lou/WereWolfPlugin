package io.github.ph1lou.werewolfplugin.listeners;

import io.github.ph1lou.werewolfapi.IModerationManager;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import io.github.ph1lou.werewolfapi.events.game.permissions.UpdateModeratorNameTag;
import io.github.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.RequestSeeWereWolfListEvent;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class TabManager implements Listener {

    final WereWolfAPI game;

    public TabManager(WereWolfAPI game) {
        this.game = game;
    }

    private void registerPlayer(Player player) {

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

    private void updatePlayerScoreBoard(Player player) {

        Bukkit.getOnlinePlayers()
                .forEach(player1 -> updatePlayerForOthers(player1, Collections.singleton(player)));
    }

    private void unregisterPlayer(Player player) {

        String name = player.getName();

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreBoard = player1.getScoreboard();
            Objects.requireNonNull(scoreBoard.getTeam(name)).unregister();
        }
    }


    private void updatePlayerOthersAndHimself(Player player) {
        updatePlayerScoreBoard(player);
        updatePlayerForOthers(player);
    }

    private void updatePlayerForOthers(Player player) {
        updatePlayerForOthers(player, Bukkit.getOnlinePlayers());
    }

    private void updatePlayerForOthers(Player player, Collection<? extends Player> players) {

        IModerationManager moderationManager = game.getModerationManager();

        StringBuilder sb = new StringBuilder();
        UUID uuid = player.getUniqueId();

        if (moderationManager.getHosts().contains(uuid)) {
            sb.append(game.translate("werewolf.commands.admin.host.tag"));
        } else if (moderationManager.getModerators().contains(uuid)) {
            sb.append(game.translate("werewolf.commands.admin.moderator.tag"));
        } else if (moderationManager.getQueue().contains(uuid)) {
            if (game.isState(StateGame.LOBBY)) {
                sb.append(game.translate("werewolf.menu.rank.tag"));
            }
        }

        UpdatePlayerNameTag event = new UpdatePlayerNameTag(uuid, sb.toString(), "", true);

        UpdateModeratorNameTag updateModeratorNameTag =
                new UpdateModeratorNameTag(uuid, "", "");

        Bukkit.getPluginManager().callEvent(event);

        Bukkit.getPluginManager().callEvent(updateModeratorNameTag);

        players.forEach(player1 -> set(player, player1,
                event,
                updateModeratorNameTag));
    }

    private void set(Player target, Player recipient, UpdatePlayerNameTag event1, UpdateModeratorNameTag event2) {

        Scoreboard scoreboard = recipient.getScoreboard();
        Team team = scoreboard.getTeam(target.getName());
        StringBuilder sb = new StringBuilder(event1.getPrefix());

        if (team != null) {
            UUID uuid1 = recipient.getUniqueId();

            RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(uuid1);
            Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);

            if (requestSeeWereWolfListEvent.isAccept()) {
                AppearInWereWolfListEvent appearInWereWolfListEvent = new AppearInWereWolfListEvent(target.getUniqueId(), uuid1);
                Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);
                if (appearInWereWolfListEvent.isAppear()) {
                    if (game.getConfig().isConfigActive(ConfigsBase.RED_NAME_TAG.getKey())) {
                        sb.append(ChatColor.DARK_RED);
                    }
                }
            }

            if (game.getModerationManager().getModerators().contains(uuid1)) {
                String string1 = event2.getSuffix() + event1.getSuffix();
                team.setSuffix(string1.substring(0, Math.min(16, string1.length())));
                String string2 = sb + event2.getPrefix();
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

    @EventHandler
    public void onNameTagUpdate(UpdateNameTagEvent event) {
        Player player = Bukkit.getPlayer(event.getUUID());

        if (player != null) {
            this.updatePlayerOthersAndHimself(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.unregisterPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        this.registerPlayer(event.getPlayer());
    }

    @EventHandler
    public void onStop(StopEvent event) {
        Bukkit.getOnlinePlayers().forEach(this::registerPlayer);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onUpdate(UpdatePlayerNameTag event) {

        StringBuilder sb = new StringBuilder(event.getSuffix());

        IPlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID());

        if (playerWW == null) {
            return;
        }

        if (playerWW.isState(StatePlayer.DEATH)) {
            sb.append(" ").append(game.translate("werewolf.score_board.death"));
            event.setSuffix(sb.toString());
        }
    }

}

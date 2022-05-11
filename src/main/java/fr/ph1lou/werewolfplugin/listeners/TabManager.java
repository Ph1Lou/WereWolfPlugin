package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.permissions.UpdateModeratorNameTagEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.RequestSeeWereWolfListEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IInvisible;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
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
            Team team = scoreBoard.getTeam(name);
            if(team != null){
                team.unregister();
            }
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

        UpdateModeratorNameTagEvent UpdateModeratorNameTagEvent = new UpdateModeratorNameTagEvent(uuid);

        Bukkit.getPluginManager().callEvent(UpdateModeratorNameTagEvent);

        players.forEach(player1 -> set(player, player1,
                UpdateModeratorNameTagEvent,
                sb.toString()));
    }

    private void set(Player player, Player target, UpdateModeratorNameTagEvent event2, String prefix) {

        UpdatePlayerNameTagEvent event1 = new UpdatePlayerNameTagEvent(player.getUniqueId(), target.getUniqueId(), prefix);

        Bukkit.getPluginManager().callEvent(event1);

        Scoreboard scoreboard = target.getScoreboard();
        Team team = scoreboard.getTeam(player.getName());
        StringBuilder sb = new StringBuilder(event1.getPrefix());

        if(event1.isTabVisibility()){
            VersionUtils.getVersionUtils().showPlayer(target,player);
        }
        else{
            VersionUtils.getVersionUtils().hidePlayer(target,player);
        }

        if (team != null) {
            UUID uuid1 = target.getUniqueId();
            RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(uuid1);
            Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);

            if (requestSeeWereWolfListEvent.isAccept()) {
                AppearInWereWolfListEvent appearInWereWolfListEvent = new AppearInWereWolfListEvent(player.getUniqueId(), uuid1);
                Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);
                if (appearInWereWolfListEvent.isAppear()) {
                    if (game.getConfig().isConfigActive(ConfigBase.RED_NAME_TAG)) {
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
                team.setPrefix(string2.substring(Math.max(string2.length()-16,0)));
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

    @EventHandler(priority = EventPriority.LOW)
    public void onUpdateNameTag(UpdatePlayerNameTagEvent event) {

        game.getPlayerWW(event.getPlayerUUID())
                .ifPresent(playerWW -> {
                    IRole role = playerWW.getRole();
                    if(role instanceof IInvisible){
                        if (event.isVisibility()) {
                            event.setVisibility(!((IInvisible)role).isInvisible());
                        }
                    }
                });
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
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        StringBuilder sb = new StringBuilder(event.getSuffix());

        IPlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (playerWW.isState(StatePlayer.DEATH)) {
            sb.append(" ").append(game.translate("werewolf.score_board.death"));
            event.setSuffix(sb.toString());
        }
    }

}

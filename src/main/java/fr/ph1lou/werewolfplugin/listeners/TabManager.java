package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.permissions.UpdateModeratorNameTagEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.RequestSeeWereWolfListEvent;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
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
import java.util.concurrent.atomic.AtomicReference;

public class TabManager implements Listener {

    private final WereWolfAPI game;

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
            if (team != null) {
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
                sb.append(game.translate("werewolf.commands.player.rank.tag"));
            }
        }

        UpdateModeratorNameTagEvent UpdateModeratorNameTagEvent = new UpdateModeratorNameTagEvent(uuid);

        Bukkit.getPluginManager().callEvent(UpdateModeratorNameTagEvent);

        players.forEach(player1 -> set(player, player1,
                UpdateModeratorNameTagEvent,
                sb.toString()));
    }

    private void set(Player player, Player target, UpdateModeratorNameTagEvent updateModeratorNameTagEvent, String prefix) {

        UpdatePlayerNameTagEvent updatePlayerNameTagEvent = new UpdatePlayerNameTagEvent(player.getUniqueId(), target.getUniqueId(), prefix);

        Bukkit.getPluginManager().callEvent(updatePlayerNameTagEvent);

        Scoreboard scoreboard = target.getScoreboard();
        Team team = scoreboard.getTeam(player.getName());
        StringBuilder sb = new StringBuilder(updatePlayerNameTagEvent.getPrefix());
        AtomicReference<ChatColor> chatColor = new AtomicReference<>(updatePlayerNameTagEvent.getChatColor());

        if (updatePlayerNameTagEvent.isTabVisibility()) {
            VersionUtils.getVersionUtils().showPlayer(target, player);
        } else {
            VersionUtils.getVersionUtils().hidePlayer(target, player);
        }

        if (team != null) {

            game.getPlayerWW(target.getUniqueId()).ifPresent(targetWW -> {
                RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(targetWW);
                Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);

                game.getPlayerWW(player.getUniqueId())
                        .ifPresent(playerWW -> {
                            if(playerWW.isState(StatePlayer.ALIVE)){
                                chatColor.set(targetWW.getColor(playerWW));
                            }
                            if (requestSeeWereWolfListEvent.isAccept()) {
                                AppearInWereWolfListEvent appearInWereWolfListEvent = new AppearInWereWolfListEvent(targetWW, playerWW);
                                Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);
                                if (appearInWereWolfListEvent.isAppear()) {
                                    if (game.getConfig().isConfigActive(ConfigBase.RED_NAME_TAG)) {
                                        chatColor.set(ChatColor.DARK_RED);
                                    }
                                }
                            }
                        });

            });

            String suffix = updatePlayerNameTagEvent.getSuffix();
            String prefix2 = sb.toString();
            boolean visibility = updatePlayerNameTagEvent.isVisibility();

            if (game.getModerationManager().getModerators().contains(target.getUniqueId())) {
                suffix = updateModeratorNameTagEvent.getSuffix() + suffix;
                chatColor.set(updateModeratorNameTagEvent.getPrefix());
                visibility = true;
            }
            team.setSuffix(suffix.substring(0, Math.min(16, suffix.length())));
            VersionUtils.getVersionUtils().setPrefixAndColor(team, prefix2, chatColor.get());
            VersionUtils.getVersionUtils().setTeamNameTagVisibility(team, visibility);
        }
    }

    @EventHandler
    public final void onModeratorScoreBoard(UpdateModeratorNameTagEvent event) {

        if(!game.isState(StateGame.GAME)){
            return;
        }

        ChatColor prefix;

        StringBuilder suffix = new StringBuilder(event.getSuffix());


        IPlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID()).orElse(null);

        if(playerWW == null) {
            return;
        }

        if (playerWW.isState(StatePlayer.DEATH)) return;

        if (playerWW.getRole().isNeutral()) {
            prefix = ChatColor.GOLD;
        } else if (playerWW.getRole().isWereWolf()) {
            prefix = ChatColor.DARK_RED;
        } else {
            prefix = ChatColor.GREEN;
        }

        playerWW.getLovers().forEach(iLover -> suffix.append(game.translate(iLover.getColor())).append(" ♥"));

        event.setPrefix(prefix);
        event.setSuffix(suffix.toString());
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
                    if (role instanceof IInvisible) {
                        if (event.isVisibility()) {
                            event.setVisibility(!((IInvisible) role).isInvisible());
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

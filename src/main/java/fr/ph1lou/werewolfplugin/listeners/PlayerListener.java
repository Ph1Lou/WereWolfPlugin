package fr.ph1lou.werewolfplugin.listeners;

import fr.mrmicky.fastboard.FastBoard;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.game.PlayerWW;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;


public class PlayerListener implements Listener {

    private final GameManager game;

    public PlayerListener(WereWolfAPI game) {
        this.game = (GameManager) game;
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {

        Player player = event.getPlayer();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            event.setCancelled(true);
        } else if (playerWW != null) {
            if (playerWW.isState(StatePlayer.JUDGEMENT)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (!(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();

        if (player.getWorld().equals(Bukkit.getWorlds().get(0))) {

            if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE) ||
                    !damager.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(ignoreCancelled = true)
    private void onPlayerDamage(EntityDamageEvent event) {


        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();
        World world = player.getWorld();

        //Wither effect = NO_FALL

        if (world.equals(game.getMapManager().getWorld()) &&
                game.getConfig().getTimerValue(TimerBase.INVULNERABILITY) > 0) {
            event.setCancelled(true);
            return;
        }

        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (playerWW.isState(StatePlayer.JUDGEMENT)) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause().equals(EntityDamageEvent.DamageCause.WITHER)) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {

            if (player.hasPotionEffect(PotionEffectType.WITHER)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onPreJoin(PlayerLoginEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (!game.isState(StateGame.LOBBY)) {
            if (!game.getModerationManager().isStaff(uuid) && !player.isOp()) {

                if (game.getConfig().getSpectatorMode() == 0 &&
                        (playerWW == null || playerWW.isState(StatePlayer.DEATH))) {
                    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                    event.setKickMessage(game.translate(Prefix.RED, "werewolf.check.spectator_disabled"));
                } else if (game.getConfig().getSpectatorMode() == 1 && playerWW == null) {
                    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                    event.setKickMessage(game.translate(Prefix.RED, "werewolf.check.death_spectator"));
                }
            }
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = (PlayerWW) game.getPlayerWW(uuid).orElse(null);
        IModerationManager moderationManager = game.getModerationManager();
        String playerName = player.getName();

        FastBoard fastboard = new FastBoard(player);
        fastboard.updateTitle(game.translate("werewolf.score_board.title"));
        game.getBoards().put(uuid, fastboard);
        event.setJoinMessage(null);

        if (game.isState(StateGame.LOBBY)) {

            if (moderationManager.getModerators().contains(uuid)) {
                player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.moderator.message"));
                player.setGameMode(GameMode.SPECTATOR);
                event.setJoinMessage(game.translate(Prefix.YELLOW, "werewolf.announcement.join_moderator",
                        Formatter.player(playerName)));

            } else if (moderationManager.getQueue().contains(uuid)) {

                moderationManager.checkQueue();
                player.setGameMode(GameMode.ADVENTURE);
                if (moderationManager.getQueue().contains(uuid)) {
                    event.setJoinMessage(game.translate(Prefix.YELLOW, "werewolf.announcement.queue_rejoin",
                            Formatter.player(playerName),
                            Formatter.number(game.getModerationManager().getQueue().indexOf(uuid) + 1)));
                }

            } else {
                game.join(player);
            }

        } else if (playerWW != null) {

            if (!playerWW.getName().equals(player.getName())) {
                playerWW.setName(player.getName());
            }
            player.setCompassTarget(playerWW.getSpawn());

            playerWW.updateAfterReconnect(player);

            if (playerWW.isState(StatePlayer.ALIVE)) {

                event.setJoinMessage(game.translate(Prefix.YELLOW, "werewolf.announcement.join_in_game",
                        Formatter.player(playerName)));
            } else if (playerWW.isState(StatePlayer.DEATH)) {
                player.setGameMode(GameMode.SPECTATOR);
                event.setJoinMessage(game.translate("werewolf.announcement.join_in_spec",
                        Formatter.player(playerName)));
            }
        } else {

            player.setCompassTarget(game.getMapManager().getWorld().getSpawnLocation());

            if (moderationManager.getModerators().contains(uuid)) {
                event.setJoinMessage(game.translate(Prefix.YELLOW, "werewolf.announcement.join_moderator",
                        Formatter.player(playerName)));
                player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.moderator.message"));
                player.setGameMode(GameMode.SPECTATOR);

            } else {
                player.setGameMode(GameMode.SPECTATOR);
                event.setJoinMessage(game.translate(Prefix.YELLOW, "werewolf.announcement.join_spec",
                        Formatter.player(playerName)));
                player.sendMessage(game.translate(Prefix.RED, "werewolf.check.already_begin"));

            }
            BukkitUtils.scheduleSyncDelayedTask(game, () ->
                    player.teleport(game.getMapManager().getWorld().getSpawnLocation()), 10);
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = (PlayerWW) game.getPlayerWW(uuid).orElse(null);
        String playerName = player.getName();

        event.setQuitMessage(null);
        FastBoard fastboard = game.getBoards().remove(player.getUniqueId());
        if (fastboard != null) {
            fastboard.delete();
        }

        if (playerWW != null) {

            playerWW.setDisconnectedLocation(player.getLocation().clone());

            if (game.isState(StateGame.LOBBY)) {
                game.remove(uuid);
                game.getModerationManager().checkQueue();
                event.setQuitMessage(game.translate("werewolf.announcement.leave",
                        Formatter.number(game.getPlayersCount()),
                        Formatter.format("&sum&", game.getTotalRoles()),
                        Formatter.player(player.getName())));
                playerWW.clearPlayer();
            } else if (game.isState(StateGame.END) || !playerWW.isState(StatePlayer.ALIVE)) {
                player.setGameMode(GameMode.SPECTATOR);
                playerWW.clearPlayer();
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                event.setQuitMessage(game.translate("werewolf.announcement.leave_in_spec",
                        Formatter.player(playerName)));
            } else {

                event.setQuitMessage(game.translate(Prefix.YELLOW, "werewolf.announcement.leave_in_game",
                        Formatter.player(playerName)));
                playerWW.setDisconnectedTime(game.getTimer());
            }
        } else {
            if (game.getModerationManager().getQueue().contains(uuid)) {
                event.setQuitMessage(game.translate(Prefix.RED, "werewolf.announcement.spectator_leave",
                        Formatter.player(playerName)));
            } else event.setQuitMessage(game.translate(Prefix.YELLOW, "werewolf.announcement.leave_spec",
                    Formatter.player(playerName)));
        }
    }
}

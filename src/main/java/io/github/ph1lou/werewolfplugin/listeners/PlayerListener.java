package io.github.ph1lou.werewolfplugin.listeners;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.ModerationManagerAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.*;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;


public class PlayerListener implements Listener {

	private final GameManager game;
	private final Main main;

	public PlayerListener(Main main) {
		this.game = (GameManager) main.getWereWolfAPI();
		this.main=main;
	}

    
	@EventHandler
	private void onDropItem(PlayerDropItemEvent event) {

		Player player = event.getPlayer();
		PlayerWW playerWW = game.getPlayerWW(player.getUniqueId());

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


	@EventHandler
	private void onPlayerDamage(EntityDamageEvent event) {


		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		UUID uuid = player.getUniqueId();
		World world = player.getWorld();

		//Wither effect = NO_FALL

		if (world.equals(game.getMapManager().getWorld()) &&
				game.getConfig().getTimerValues().get(TimersBase.INVULNERABILITY.getKey()) > 0) {
			event.setCancelled(true);
			return;
		}

		PlayerWW playerWW = game.getPlayerWW(uuid);

		if (playerWW == null) return;

		if (playerWW.isState(StatePlayer.JUDGEMENT)) {
			event.setCancelled(true);
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
	private void onPlayerRespawn(PlayerRespawnEvent event) {

		Player player = event.getPlayer();
		PlayerWW playerWW = game.getPlayerWW(player.getUniqueId());

		if (game.isState(StateGame.LOBBY)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () ->
							event.getPlayer().addPotionEffect(new PotionEffect(
									PotionEffectType.SATURATION,
									Integer.MAX_VALUE,
									0,
									false,
									false)),
					20L);
		} else if (playerWW == null) {
			event.setRespawnLocation(game.getMapManager().getWorld().getSpawnLocation());
		} else if (game.isState(StateGame.START) ||
				game.isState(StateGame.TRANSPORTATION) ||
				(game.isState(StateGame.GAME) &&
						game.getConfig().isTrollSV())) {

			event.setRespawnLocation(
					playerWW.getSpawn());
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
				event.getPlayer().removePotionEffect(PotionEffectType.WITHER);
				event.getPlayer().addPotionEffect(new PotionEffect(
						PotionEffectType.WITHER,
						400,
						-1,
						false,
						false));
			}, 1L);
		} else event.setRespawnLocation(game.getMapManager().getWorld().getSpawnLocation());
	}


	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event) {

		Player player = event.getEntity();
		UUID uuid = player.getUniqueId();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.spigot().respawn(), 10L);
		event.setKeepInventory(true);

		if (game.getConfig().isTrollSV()) return;

		if (game.isState(StateGame.GAME)) {

			event.setDeathMessage(null);
			event.setKeepLevel(true);

			PlayerWW playerWW = game.getPlayerWW(uuid);

			if (playerWW == null) return;

			if (!playerWW.isState(StatePlayer.ALIVE)) return;

			playerWW.setDeathTime(game.getScore().getTimer());
			playerWW.setSpawn(player.getLocation());
			playerWW.clearItemDeath();
			playerWW.setState(StatePlayer.JUDGEMENT);

			Inventory inv = Bukkit.createInventory(null, 45);

			for (int i = 0; i < 40; i++) {
				inv.setItem(i, player.getInventory().getItem(i));
			}

			playerWW.setItemDeath(inv.getContents());

			player.setGameMode(GameMode.ADVENTURE);
			player.sendMessage(game.translate("werewolf.announcement.potential_revive"));

			if (player.getKiller() != null) {

				Player killer = player.getKiller();
				UUID killerUUID = killer.getUniqueId();
				PlayerWW killerWW = game.getPlayerWW(killerUUID);
				playerWW.addKiller(killerWW);

				if (killerWW != null) {
					killerWW.addOneKill();
				}
			} else playerWW.addKiller(null);

			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                if (!game.isState(StateGame.END)) {
					FirstDeathEvent firstDeathEvent = new FirstDeathEvent(playerWW);
                    Bukkit.getPluginManager().callEvent(firstDeathEvent);
                }


            }, 20L);
		}
	}


	
	@EventHandler
	private void onJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerWW playerWW = game.getPlayerWW(uuid);
		ModerationManagerAPI moderationManager = game.getModerationManager();
		String playerName = player.getName();

		FastBoard fastboard = new FastBoard(player);
		fastboard.updateTitle(game.translate("werewolf.score_board.title"));
		game.getBoards().put(uuid, fastboard);
		event.setJoinMessage(null);

		if (game.isState(StateGame.LOBBY)) {

			if (moderationManager.getModerators().contains(uuid)) {
				player.sendMessage(game.translate("werewolf.commands.admin.moderator.message"));
				player.setGameMode(GameMode.SPECTATOR);
				event.setJoinMessage(game.translate("werewolf.announcement.join_moderator",
						playerName));

			} else if (moderationManager.getQueue().contains(uuid)) {

                moderationManager.checkQueue();

				if (moderationManager.getQueue().contains(uuid)) {
					event.setJoinMessage(game.translate("werewolf.announcement.queue_rejoin",
							playerName, game.getModerationManager().getQueue().indexOf(uuid) + 1));
				}

			} else {
				game.join(player);
			}

		} else if (playerWW != null) {

			if (!playerWW.getName().equals(playerName)) {
				playerWW.setName(playerName);
			}

			if (playerWW.isState(StatePlayer.ALIVE)) {

				event.setJoinMessage(game.translate("werewolf.announcement.join_in_game",
						playerName));

				if (game.isState(StateGame.GAME)) {
					if (!playerWW.hasKit()) {
						playerWW.getRole().roleAnnouncement();
					}
				}
			} else if (playerWW.isState(StatePlayer.DEATH)) {

				if (game.getConfig().getSpectatorMode() > 0 ||
						moderationManager.getHosts().contains(player.getUniqueId())) {
					player.setGameMode(GameMode.SPECTATOR);
					event.setJoinMessage(game.translate("werewolf.announcement.join_in_spec",
							playerName));
				} else if (!player.isOp()) {
					player.kickPlayer(game.translate("werewolf.check.death_spectator"));
				}
			}
		}
		else {
			if (moderationManager.getModerators().contains(uuid)) {
				event.setJoinMessage(game.translate("werewolf.announcement.join_moderator",
						playerName));
				player.sendMessage(game.translate("werewolf.commands.admin.moderator.message"));
				player.setGameMode(GameMode.SPECTATOR);
				Bukkit.getScheduler().scheduleSyncDelayedTask(main, () ->
						player.teleport(game.getMapManager().getWorld().getSpawnLocation()), 10);
			} else if (game.getConfig().getSpectatorMode() < 2) {
				player.kickPlayer(game.translate("werewolf.check.spectator_disabled"));
			} else {
				player.setGameMode(GameMode.SPECTATOR);
				event.setJoinMessage(game.translate("werewolf.announcement.join_spec", playerName));
				player.sendMessage(game.translate("werewolf.check.already_begin"));
				Bukkit.getScheduler().scheduleSyncDelayedTask(main, () ->
						player.teleport(game.getMapManager().getWorld().getSpawnLocation()), 10);

			}
		}
	}

	@EventHandler
	public void onLanguageUpdate(UpdateLanguageEvent event) {

		Bukkit.getOnlinePlayers()
				.stream()
				.map(Entity::getUniqueId)
				.filter(uuid -> game.getBoards().containsKey(uuid))
				.map(uuid -> game.getBoards().get(uuid))
				.forEach(fastBoard -> fastBoard.updateTitle(
						game.translate("werewolf.score_board.title")));
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerWW playerWW = game.getPlayerWW(uuid);
		String playerName = player.getName();

		event.setQuitMessage(null);
		FastBoard fastboard = game.getBoards().remove(player.getUniqueId());
		if (fastboard != null) {
			fastboard.delete();
		}

		if (playerWW != null) {

			if (game.isState(StateGame.LOBBY)) {
				game.getScore().removePlayerSize();
				game.remove(uuid);
				game.getModerationManager().checkQueue();
				event.setQuitMessage(game.translate("werewolf.announcement.leave",
						game.getScore().getPlayerSize(), game.getScore().getRole(), player.getName()));
				game.clearPlayer(player);
			} else if (game.isState(StateGame.END) || !playerWW.isState(StatePlayer.ALIVE)) {
				player.setGameMode(GameMode.SPECTATOR);
				game.clearPlayer(player);
				player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
				event.setQuitMessage(game.translate("werewolf.announcement.leave_in_spec",
						playerName));
			} else {

				event.setQuitMessage(game.translate("werewolf.announcement.leave_in_game",
						playerName));
				playerWW.setDisconnectedTime(game.getScore().getTimer());
			}
        } else {
			if (game.getModerationManager().getQueue().contains(uuid)) {
				event.setQuitMessage(game.translate("werewolf.announcement.spectator_leave",
						playerName));
			} else event.setQuitMessage(game.translate("werewolf.announcement.leave_spec",
					playerName));
        }
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFirstDeath(FirstDeathEvent event) {

		if (event.isCancelled()) return;

		PlayerWW playerWW = event.getPlayerWW();
		SecondDeathEvent secondDeathEvent = new SecondDeathEvent(playerWW);
		Bukkit.getPluginManager().callEvent(secondDeathEvent);
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> {
			if (!game.isState(StateGame.END)) {
				if (playerWW != null && playerWW.isState(StatePlayer.JUDGEMENT) && !secondDeathEvent.isCancelled()) {

					ThirdDeathEvent thirdDeathEvent = new ThirdDeathEvent(playerWW);
					Bukkit.getPluginManager().callEvent(thirdDeathEvent);
					Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> {
						if (!game.isState(StateGame.END)) {
							if (playerWW.isState(StatePlayer.JUDGEMENT) && !thirdDeathEvent.isCancelled()) {
								game.death(playerWW);
							}
						}

					}, 7 * 20);
				}
			}

		}, 7 * 20);
	}


	@EventHandler
	public void onFinalDeath(FinalDeathEvent event) {

		PlayerWW playerWW = event.getPlayerWW();
		Player player = Bukkit.getPlayer(playerWW.getUUID());
		World world = game.getMapManager().getWorld();

		String roleLG = playerWW.getRole().getKey();

		if (playerWW.isState(StatePlayer.ALIVE)) {

			playerWW.clearItemDeath();

			if (player != null) {
				playerWW.setSpawn(player.getLocation());

				Inventory inv = Bukkit.createInventory(null, 45);

				for (int j = 0; j < 40; j++) {
					inv.setItem(j, player.getInventory().getItem(j));
				}
				playerWW.setItemDeath(inv.getContents());
			}

			playerWW.setDeathTime(game.getScore().getTimer());

		}

		if (playerWW.isState(StatePlayer.DEATH)) return;

		if (playerWW.isThief()) {
			roleLG = RolesBase.THIEF.getKey();
		}

		game.getConfig().getRoleCount().put(roleLG, game.getConfig().getRoleCount().get(roleLG) - 1);

		AnnouncementDeathEvent announcementDeathEvent = new AnnouncementDeathEvent(playerWW.getName(),
				game.translate(roleLG),
				game.translate("werewolf.announcement.death_message"));

		Bukkit.getPluginManager().callEvent(announcementDeathEvent);

		if (!announcementDeathEvent.isCancelled()) {

			String deathMessage = announcementDeathEvent.getFormat();
			deathMessage = deathMessage.replace("&player&",
					announcementDeathEvent.getPlayerName());
			deathMessage = deathMessage.replace("&role&",
					announcementDeathEvent.getRole());

			Bukkit.broadcastMessage(deathMessage);
		}


		playerWW.setState(StatePlayer.DEATH);
		game.getScore().removePlayerSize();

		Stream.concat(playerWW.getItemDeath()
						.stream(),
				game.getStuffs().getDeathLoot()
						.stream())
				.filter(Objects::nonNull)
				.forEach(itemStack -> world.dropItem(playerWW.getSpawn(), itemStack));

		Bukkit.getOnlinePlayers()
				.forEach(Sounds.AMBIENCE_THUNDER::play);

		if (player != null) {

			player.setGameMode(GameMode.SPECTATOR);
			TextComponent msg = new TextComponent(game.translate("werewolf.bug"));
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
					"https://discord.gg/GXXCVUA"));
			player.spigot().sendMessage(msg);
			if (game.getConfig().getSpectatorMode() == 0 && !player.isOp()) {
				player.kickPlayer(game.translate("werewolf.check.death_spectator"));
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), game::checkVictory);
	}

	@EventHandler
	public void onResurrection(ResurrectionEvent event) {


		PlayerWW playerWW = event.getPlayerWW();

		if (playerWW.isState(StatePlayer.ALIVE)) return;

		playerWW.getRole().recoverPotionEffect();
		game.getMapManager().transportation(playerWW, Math.random() * Math.PI * 2,
				game.translate("werewolf.announcement.resurrection"));
		playerWW.setState(StatePlayer.ALIVE);

		Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), game::checkVictory);
	}


	@EventHandler
	public void onActionBarEventLobby(ActionBarEvent event) {

		if (!game.isState(StateGame.LOBBY)) return;

		Player player = Bukkit.getPlayer(event.getPlayerUUID());

		if (player == null) return;

		if (game.getMapManager().getPercentageGenerated() == 0) {

			if (game.getModerationManager()
					.checkAccessAdminCommand(
							"werewolf.commands.admin.generation.command",
							player,
							false)) {
				event.setActionBar(event.getActionBar() +
						game.translate("werewolf.action_bar.generation"));
			}

			return;
		}


		if (game.getMapManager().getPercentageGenerated() < 100) {
			event.setActionBar(event.getActionBar() +
					game.translate("werewolf.action_bar.progress",
							new DecimalFormat("0.0")
									.format(game.getMapManager()
											.getPercentageGenerated())));

			return;
		}

		event.setActionBar(event.getActionBar() +
				game.translate("werewolf.action_bar.complete"));


	}


}

package io.github.ph1lou.werewolfplugin.listeners;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.AuraModifier;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IModerationManager;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.enums.UpdateCompositionReason;
import io.github.ph1lou.werewolfapi.events.UpdateLanguageEvent;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.game.PlayerWW;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.ArrayUtils;
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

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;


public class PlayerListener implements Listener {

	private final GameManager game;

	public PlayerListener(io.github.ph1lou.werewolfapi.WereWolfAPI game) {
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


	@EventHandler
	private void onPlayerDamage(EntityDamageEvent event) {


		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		UUID uuid = player.getUniqueId();
		World world = player.getWorld();

		//Wither effect = NO_FALL

		if (world.equals(game.getMapManager().getWorld()) &&
				game.getConfig().getTimerValue(TimerBase.INVULNERABILITY.getKey()) > 0) {
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
	private void onPlayerRespawn(PlayerRespawnEvent event) {

		Player player = event.getPlayer();
		IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

		if(playerWW !=null){

			((PlayerWW)playerWW).updatePotionEffects(player);

			if (game.isState(StateGame.LOBBY)) {
				BukkitUtils.scheduleSyncDelayedTask(() ->
								event.getPlayer().addPotionEffect(new PotionEffect(
										PotionEffectType.SATURATION,
										Integer.MAX_VALUE,
										0,
										false,
										false)),
						20L);
			} else if (game.isState(StateGame.START) ||
					game.isState(StateGame.TRANSPORTATION) ||
					(game.isState(StateGame.GAME) &&
							game.getConfig().isTrollSV())) {

				event.setRespawnLocation(
						playerWW.getSpawn());
				BukkitUtils.scheduleSyncDelayedTask(() -> {
					event.getPlayer().removePotionEffect(PotionEffectType.WITHER);
					event.getPlayer().addPotionEffect(new PotionEffect(
							PotionEffectType.WITHER,
							400,
							-1,
							false,
							false));
				}, 1L);
			} else {
				event.setRespawnLocation(game.getMapManager().getWorld().getSpawnLocation());
			}
		}
		else{
			event.setRespawnLocation(game.getMapManager().getWorld().getSpawnLocation());
		}


	}


	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event) {

		Player player = event.getEntity();
		UUID uuid = player.getUniqueId();
		BukkitUtils.scheduleSyncDelayedTask(() -> player.spigot().respawn(), 10L);
		event.setKeepInventory(true);

		if (game.getConfig().isTrollSV()) return;

		if (game.isState(StateGame.GAME)) {

			event.setDeathMessage(null);
			event.setKeepLevel(true);

			IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

			if (playerWW == null) return;

			if (!playerWW.isState(StatePlayer.ALIVE)) return;

			playerWW.setDeathTime(game.getTimer());
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
				IPlayerWW killerWW = game.getPlayerWW(killerUUID).orElse(null);
				playerWW.addKiller(killerWW);

				if (killerWW != null) {
					killerWW.addOneKill(playerWW);
					killerWW.getRole().addAuraModifier(
							new AuraModifier("killer", Aura.DARK, 50, false));
				}
			} else playerWW.addKiller(null);

			BukkitUtils.scheduleSyncDelayedTask(() -> {
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
		IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);
		IModerationManager moderationManager = game.getModerationManager();
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
				player.setGameMode(GameMode.ADVENTURE);
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

			playerWW.updateAfterReconnect(player);

			if (playerWW.isState(StatePlayer.ALIVE)) {

				event.setJoinMessage(game.translate("werewolf.announcement.join_in_game",
						playerName));
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
				BukkitUtils.scheduleSyncDelayedTask(() ->
						player.teleport(game.getMapManager().getWorld().getSpawnLocation()), 10);
			} else if (game.getConfig().getSpectatorMode() < 2) {
				player.kickPlayer(game.translate("werewolf.check.spectator_disabled"));
			} else {
				player.setGameMode(GameMode.SPECTATOR);
				event.setJoinMessage(game.translate("werewolf.announcement.join_spec", playerName));
				player.sendMessage(game.translate("werewolf.check.already_begin"));
				BukkitUtils.scheduleSyncDelayedTask(() ->
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
		IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);
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
						game.getPlayerSize(), game.getRoleInitialSize(), player.getName()));
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
				playerWW.setDisconnectedTime(game.getTimer());
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

		IPlayerWW playerWW = event.getPlayerWW();

		if (!playerWW.isState(StatePlayer.JUDGEMENT)) return;

		SecondDeathEvent secondDeathEvent = new SecondDeathEvent(playerWW);
		Bukkit.getPluginManager().callEvent(secondDeathEvent);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSecondDeathEvent(SecondDeathEvent event) {

		if (event.isCancelled()) return;

		BukkitUtils.scheduleSyncDelayedTask(() -> {
			if (!game.isState(StateGame.END)) {
				if (event.getPlayerWW().isState(StatePlayer.JUDGEMENT)) {

					ThirdDeathEvent thirdDeathEvent = new ThirdDeathEvent(event.getPlayerWW());
					Bukkit.getPluginManager().callEvent(thirdDeathEvent);
				}
			}

		}, 7 * 20);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onThirdDeath(ThirdDeathEvent event) {

		if (event.isCancelled()) return;

		BukkitUtils.scheduleSyncDelayedTask(() -> {
			if (!game.isState(StateGame.END)) {
				if (event.getPlayerWW().isState(StatePlayer.JUDGEMENT)) {
					game.death(event.getPlayerWW());
				}
			}

		}, 7 * 20);
	}


	@EventHandler
	public void onFinalDeath(FinalDeathEvent event) {

		IPlayerWW playerWW = event.getPlayerWW();
		Player player = Bukkit.getPlayer(playerWW.getUUID());
		World world = game.getMapManager().getWorld();

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
			playerWW.setDeathTime(game.getTimer());
		}

		if (playerWW.isState(StatePlayer.DEATH)) return;

		playerWW.setState(StatePlayer.DEATH);
		game.setPlayerSize(game.getPlayerSize()-1);

		game.getPlayersWW().forEach(playerWW1 -> {
			AnnouncementDeathEvent announcementDeathEvent = new AnnouncementDeathEvent(playerWW, playerWW1,
					"werewolf.announcement.death_message");
			Bukkit.getPluginManager().callEvent(announcementDeathEvent);

			Formatter[] formatters = (Formatter[]) ArrayUtils.addAll(announcementDeathEvent.getFormatters().toArray(new Formatter[0]),
					new Formatter[]{Formatter.format("&player&", announcementDeathEvent.getPlayerName()),
							Formatter.format("&role&",game.translate(announcementDeathEvent.getRole()))});


			announcementDeathEvent.getTargetPlayer().sendMessageWithKey(announcementDeathEvent.getFormat(),formatters
			);
		});

		game.getModerationManager().getModerators().stream()
				.filter(uuid -> !game.getPlayerWW(uuid).isPresent())
				.map(Bukkit::getPlayer)
				.filter(Objects::nonNull)
				.forEach(player1 -> player1.sendMessage(this.sendOriginalDeathMessage(playerWW)));

		Bukkit.getConsoleSender().sendMessage(this.sendOriginalDeathMessage(playerWW));

		UpdateCompositionEvent updateCompositionReason = new UpdateCompositionEvent(playerWW.getRole().getKey(), UpdateCompositionReason.DEATH, -1);
		Bukkit.getPluginManager().callEvent(updateCompositionReason);

		if (!updateCompositionReason.isCancelled()) {
			game.getConfig().removeOneRole(playerWW.getRole().getKey());
		}

		Stream.concat(playerWW.getItemDeath()
						.stream(),
				game.getStuffs().getDeathLoot()
						.stream())
				.filter(Objects::nonNull)
				.forEach(itemStack -> world.dropItem(playerWW.getSpawn(), itemStack));

		Bukkit.getOnlinePlayers()
				.forEach(Sound.AMBIENCE_THUNDER::play);

		if (player != null) {

			player.setGameMode(GameMode.SPECTATOR);
			TextComponent msg = new TextComponent(game.translate("werewolf.bug"));
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
					"https://discord.gg/GXXCVUA"));
			player.spigot().sendMessage(msg);
			if (game.getConfig().getSpectatorMode() == 0 && !player.isOp()) {
				player.kickPlayer(game.translate("werewolf.check.death_spectator"));
			}
			Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));
		}
		BukkitUtils.scheduleSyncDelayedTask(game::checkVictory);
	}

	private String sendOriginalDeathMessage(IPlayerWW playerWW) {
		return game.translate("werewolf.announcement.death_message_with_role")
				.replace("&player&", playerWW.getName())
				.replace("&role&", game.translate(playerWW.getRole().getKey()));
	}

	@EventHandler
	public void onResurrection(ResurrectionEvent event) {

		IPlayerWW playerWW = event.getPlayerWW();

		if (playerWW.isState(StatePlayer.ALIVE)) return;

		playerWW.setState(StatePlayer.ALIVE);
		playerWW.getRole().addAuraModifier(new AuraModifier("resurrection", Aura.NEUTRAL, 10, false));
		playerWW.getRole().recoverPotionEffects();
		playerWW.sendMessageWithKey("werewolf.announcement.resurrection");
		game.getMapManager().transportation(playerWW, Math.random() * Math.PI * 2);

		Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
		BukkitUtils.scheduleSyncDelayedTask(game::checkVictory);
	}




	@EventHandler
	public void onChatWW(WereWolfChatEvent event) {
		if (event.isCancelled()) return;

		game.getModerationManager().getModerators().stream()
				.map(Bukkit::getPlayer)
				.filter(Objects::nonNull)
				.forEach(player -> player.sendMessage(game.translate("werewolf.commands.admin.ww_chat.modo",
						Formatter.format("&name&",event.getPlayerWW().getName()),
						Formatter.format("&message&",event.getMessage()))));

	}


}

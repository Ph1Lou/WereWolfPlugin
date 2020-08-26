package io.github.ph1lou.werewolfplugin.listener;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.FirstDeathEvent;
import io.github.ph1lou.werewolfapi.events.UpdateLanguageEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class PlayerListener implements Listener {

	private final GameManager game;
	private final Main main;

	public PlayerListener(Main main, GameManager game) {
		this.game = game;
		this.main=main;
	}

    
	@EventHandler
	private void onDropItem(PlayerDropItemEvent event) {

		Player player = event.getPlayer();

		if (player.getGameMode().equals(GameMode.SPECTATOR)) {
			event.setCancelled(true);
		} else if (game.getPlayersWW().containsKey(player.getUniqueId())) {
			if (game.getPlayersWW().get(player.getUniqueId()).isState(State.JUDGEMENT)) {
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

			if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE) || !damager.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
				event.setCancelled(true);
			}
		}
	}


	@EventHandler
	private void onPlayerDamage(EntityDamageEvent event) {


		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		UUID uuid = player.getUniqueId();
		World world =player.getWorld();

		//Wither effect = NO_FALL

		if (world.equals(game.getWorld()) && game.getConfig().getTimerValues().get("werewolf.menu.timers.invulnerability") > 0) {
			event.setCancelled(true);
			return;
		}

		if (!game.getPlayersWW().containsKey(uuid)) return;

		PlayerWW plg = game.getPlayersWW().get(uuid);

		if (plg.isState(State.JUDGEMENT)) {
			event.setCancelled(true);
		}

		if (event.getCause().equals(EntityDamageEvent.DamageCause.WITHER)) {
			event.setCancelled(true);
			return;
		}

		if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {

			if (player.hasPotionEffect(PotionEffectType.WITHER)) {
				event.setCancelled(true);
				return;
			}
			if (plg.hasSalvation()) {
				event.setCancelled(true);
				return;
			}
			for (List<UUID> loversCursed : game.getCursedLoversRange()) {
				if (loversCursed.contains(uuid)) {
					event.setCancelled(true);
					break;
				}
			}
		}
	}


	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event) {

		if (game.isState(StateLG.LOBBY)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false)), 20L);
		} else if (!game.getPlayersWW().containsKey(event.getPlayer().getUniqueId())) {
			event.setRespawnLocation(game.getWorld().getSpawnLocation());
		} else if (game.isState(StateLG.START) || game.isState(StateLG.TRANSPORTATION) || (game.isState(StateLG.GAME) && game.getConfig().isTrollSV())) {
			event.setRespawnLocation(game.getPlayersWW().get(event.getPlayer().getUniqueId()).getSpawn());
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
				event.getPlayer().removePotionEffect(PotionEffectType.WITHER);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, -1, false, false));
			}, 1L);
		} else event.setRespawnLocation(game.getWorld().getSpawnLocation());
	}


	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event) {

		Player player = event.getEntity();
		UUID uuid = player.getUniqueId();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.spigot().respawn(), 10L);
		event.setKeepInventory(true);

		if (game.getConfig().isTrollSV()) return;

		if (game.isState(StateLG.GAME)) {

			event.setDeathMessage(null);
			event.setKeepLevel(true);

			if (!game.getPlayersWW().containsKey(uuid)) return;

			PlayerWW plg = game.getPlayersWW().get(uuid);

			if (!plg.isState(State.ALIVE)) return;

			plg.setSpawn(player.getLocation());
			plg.clearItemDeath();
			plg.setState(State.JUDGEMENT);

			Inventory inv = Bukkit.createInventory(null, 45);

			for (int i = 0; i < 40; i++) {
				inv.setItem(i, player.getInventory().getItem(i));
			}

			plg.setItemDeath(inv.getContents());

			player.setGameMode(GameMode.ADVENTURE);
			player.sendMessage(game.translate("werewolf.announcement.potential_revive"));

			if (player.getKiller() != null) {

				Player killer = player.getKiller();
				UUID killerUUID = killer.getUniqueId();
				plg.addKiller(killerUUID);

				if (game.getPlayersWW().containsKey(killerUUID)) {

					PlayerWW klg = game.getPlayersWW().get(killerUUID);

					klg.addOneKill();
				}
			} else plg.addKiller(null);

			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {

				FirstDeathEvent firstDeathEvent = new FirstDeathEvent(uuid);
				Bukkit.getPluginManager().callEvent(firstDeathEvent);

				if(firstDeathEvent.isCancelled()) return;

				game.deathStep1(uuid);
			}, 20L);
		}
	}


	
	@EventHandler
	private void onJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		String playerName = player.getName();
		UUID uuid = player.getUniqueId();

		FastBoard fastboard = new FastBoard(player);
		fastboard.updateTitle(game.translate("werewolf.score_board.title"));
		game.getBoards().put(uuid, fastboard);
		VersionUtils.getVersionUtils().sendTabTitle(player, game.translate("werewolf.tab.top"), game.translate("werewolf.tab.bot"));

		if (game.isState(StateLG.LOBBY)) {

			event.setJoinMessage(null);

			if (game.getModerators().contains(uuid)) {
				player.sendMessage(game.translate("werewolf.commands.admin.moderator.message"));
				player.setGameMode(GameMode.SPECTATOR);
			} else {
				game.join(player);
			}
		} else if (game.getPlayersWW().containsKey(uuid)) {

			PlayerWW plg = game.getPlayersWW().get(uuid);
			if(!plg.getName().equals(playerName)){
				plg.setName(playerName);
			}
			player.setScoreboard(plg.getScoreBoard());

			if (plg.isState(State.ALIVE)) {

				event.setJoinMessage(game.translate("werewolf.announcement.join_in_game", playerName));

				if (game.isState(StateLG.GAME)) {
					if (!plg.hasKit()) {
						plg.getRole().recoverPower();
					}
					if (plg.getAnnounceCursedLoversAFK()) {
						game.getLoversManage().announceCursedLovers(player);
					}
					if (plg.getAnnounceLoversAFK()) {
						game.getLoversManage().announceLovers(player);
					}
				}
			}
			else if (plg.isState(State.DEATH)) {

				if(game.getSpectatorMode()>0 || game.getHosts().contains(player.getUniqueId())){
					player.setGameMode(GameMode.SPECTATOR);
				}
				else {
					player.kickPlayer(game.translate("werewolf.check.death_spectator"));
				}
			}
		}
		else {
			if (game.getModerators().contains(uuid)) {
				player.sendMessage(game.translate("werewolf.commands.admin.moderator.message"));
				player.setGameMode(GameMode.SPECTATOR);
				Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.teleport(game.getWorld().getSpawnLocation()), 10);
			} else if (game.getSpectatorMode() < 2) {
				player.kickPlayer(game.translate("werewolf.check.spectator_disabled"));
			} else {
				player.setGameMode(GameMode.SPECTATOR);
				player.sendMessage(game.translate("werewolf.check.already_begin"));
				Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.teleport(game.getWorld().getSpawnLocation()), 10);

			}
		}

		game.getScore().updateBoard();
		game.updateNameTag();
		game.updateCompass();
	}

	@EventHandler
	public void onLanguageUpdate(UpdateLanguageEvent event) {

		for (Player player : Bukkit.getOnlinePlayers()) {
			VersionUtils.getVersionUtils().sendTabTitle(player, game.translate("werewolf.tab.top"), game.translate("werewolf.tab.bot"));
			if (game.getBoards().containsKey(player.getUniqueId())) {
				game.getBoards().get(player.getUniqueId()).updateTitle(game.translate("werewolf.score_board.title"));
			}
			player.closeInventory();
		}
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();
		String playerName = player.getName();
		UUID uuid = player.getUniqueId();

		FastBoard fastboard = game.getBoards().remove(player.getUniqueId());
		if (fastboard != null) {
			fastboard.delete();
		}

		if (game.getPlayersWW().containsKey(uuid)) {

			PlayerWW plg = game.getPlayersWW().get(uuid);

			if (game.isState(StateLG.LOBBY)) {
				game.getScore().removePlayerSize();
				game.getPlayersWW().remove(uuid);
				game.checkQueue();
				event.setQuitMessage(game.translate("werewolf.announcement.leave", game.getScore().getPlayerSize(), game.getScore().getRole(), player.getName()));
				game.clearPlayer(player);
			} else if (game.isState(StateLG.END) || !plg.isState(State.ALIVE)) {
				player.setGameMode(GameMode.SPECTATOR);
				game.clearPlayer(player);
				player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
				event.setQuitMessage(game.translate("werewolf.announcement.leave_in_spec",playerName));
			} else {
				event.setQuitMessage(game.translate("werewolf.announcement.leave_in_game", playerName));
				plg.setDeathTime((int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
			}
		}
    }


}

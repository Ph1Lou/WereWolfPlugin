package io.github.ph1lou.pluginlg.listener;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.*;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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


public class PlayerListener implements Listener {

	private final GameManager game;
	private final MainLG main;

	public PlayerListener(MainLG main, GameManager game) {
		this.game = game;
		this.main=main;
	}

    
	@EventHandler
	private void onDropItem(PlayerDropItemEvent event) {

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		String playerName = player.getName();

		if (!game.playerLG.containsKey(playerName)) return;

		if (!game.playerLG.get(playerName).isState(State.LIVING) && !player.hasPermission("a.use") && !game.getHosts().contains(uuid) && !game.getModerators().contains(uuid)) {
			event.setCancelled(true);
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
			return;
		}

		if (game.config.timerValues.get(TimerLG.PVP) > 0) {
			event.setCancelled(true);
		}
	}


	@EventHandler
	private void onPlayerDamage(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();

		String playerName = player.getName();

		//Wither effect = NOFALL
		if (player.getWorld().equals(game.getWorld()) && game.config.timerValues.get(TimerLG.INVULNERABILITY) > 0) {
			event.setCancelled(true);
			return;
		}

		if (!game.playerLG.containsKey(playerName)) return;

		PlayerLG plg = game.playerLG.get(playerName);

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
			if (plg.isRole(RoleLG.CORBEAU) || plg.hasSalvation()) {
				event.setCancelled(true);
				return;
			}
			for (List<String> loversCursed : game.loversManage.cursedLoversRange) {
				if (loversCursed.contains(playerName)) {
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
		} else if (!game.playerLG.containsKey(event.getPlayer().getName())) {
			event.setRespawnLocation(game.getWorld().getSpawnLocation());
		} else if (game.isState(StateLG.DEBUT) || game.isState(StateLG.TRANSPORTATION) || (game.isState(StateLG.LG) && game.config.isTrollSV())) {
			event.setRespawnLocation(game.playerLG.get(event.getPlayer().getName()).getSpawn());
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
				event.getPlayer().removePotionEffect(PotionEffectType.WITHER);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, -1, false, false));
			}, 1L);
		} else event.setRespawnLocation(game.getWorld().getSpawnLocation());
	}
	
	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event) {

		Player player = event.getEntity();
		String playername = player.getName();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.spigot().respawn(), 10L);
		event.setKeepInventory(true);

		if (game.isState(StateLG.LG) && !game.config.isTrollSV()) {

			event.setDeathMessage(null);
			event.setKeepLevel(true);

			if (!game.playerLG.containsKey(playername)) return;

			PlayerLG plg = game.playerLG.get(playername);

			if (!plg.isState(State.LIVING)) return;

			plg.setSpawn(player.getLocation());
			plg.clearItemDeath();
			plg.setDeathTime(game.score.getTimer());
			plg.setState(State.JUDGEMENT);

			Inventory inv = Bukkit.createInventory(null, 45, playername);

			for (int i = 0; i < 40; i++) {
				inv.setItem(i, player.getInventory().getItem(i));
			}

			plg.setItemDeath(inv.getContents());

			player.setGameMode(GameMode.ADVENTURE);
			player.sendMessage(game.text.getText(130));

			if (player.getKiller() != null) {

				Player killer = player.getKiller();
				String killerName = killer.getName();
				plg.setKiller(killerName);

				if (game.playerLG.containsKey(killerName)) {

					PlayerLG klg = game.playerLG.get(killerName);

					klg.addOneKill();

					if (!klg.isCamp(Camp.VILLAGE)) {
						killer.removePotionEffect(PotionEffectType.ABSORPTION);
						killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 0, false, false));
						killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 0, false, false));
					}

					if (klg.isRole(RoleLG.VOLEUR) && klg.hasPower()) {
						plg.setStolen(true);
						klg.setPower(false);
						return;
					}
				}
			} else plg.setKiller(game.text.getText(81));

			if (plg.isRole(RoleLG.SUCCUBUS) && !plg.getAffectedPlayer().isEmpty() && !plg.hasPower()) {

				String targetName = plg.getAffectedPlayer().get(0);
				if (game.playerLG.containsKey(targetName)) {
					PlayerLG trg = game.playerLG.get(targetName);
					if (trg.isState(State.LIVING)) {
						trg.removeTargetOf(playername);
						plg.clearAffectedPlayer();

						if (Bukkit.getPlayer(targetName) == null) {
							game.death_manage.death(targetName);
						} else {
							Player target = Bukkit.getPlayer(targetName);
							target.damage(10000);
							target.sendMessage(game.text.getText(266));
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> game.death_manage.resurrection(playername), 20L);
						return;
					}
				}
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> game.death_manage.deathStep1(playername), 20L);
		}
	}

	
	@EventHandler
	private void onJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();
		String playerName = player.getName();
		UUID uuid = player.getUniqueId();

		FastBoard fastboard = new FastBoard(player);
		fastboard.updateTitle(game.getText(125));
		game.boards.put(uuid, fastboard);
		Title.sendTabTitle(player, game.getText(125), game.getText(184));

		if (game.isState(StateLG.LOBBY)) {

			event.setJoinMessage(null);

			if (game.getModerators().contains(uuid)) {
				player.sendMessage(game.getText(294));
				player.setGameMode(GameMode.SPECTATOR);
				player.setScoreboard(game.board);
			} else {
				game.join(player);
			}
		} else if (game.playerLG.containsKey(playerName)) {

			PlayerLG plg = game.playerLG.get(playerName);

			player.setScoreboard(plg.getScoreBoard());

			if (plg.isState(State.LIVING)) {

				event.setJoinMessage(String.format(game.text.getText(193), playerName));

				if (game.isState(StateLG.LG)) {
					if (!plg.hasKit()) {
						game.roleManage.recoverRolePower(playerName);
					}
					if (plg.getAnnounceCursedLoversAFK()) {
						game.loversManage.announceCursedLovers(player);
					}
					if (plg.getAnnounceLoversAFK()) {
						game.loversManage.announceLovers(player);
					}
				}
			}
			else if (plg.isState(State.MORT)) {

				if(game.getSpectatorMode()>0 || game.getHosts().contains(player.getUniqueId())){
					player.setGameMode(GameMode.SPECTATOR);
				}
				else {
					player.kickPlayer(game.getText(277));
					return;
				}
			}
		}
		else {
			if (game.getModerators().contains(uuid)) {
				player.sendMessage(game.getText(294));
				player.setGameMode(GameMode.SPECTATOR);
				player.teleport(game.getWorld().getSpawnLocation());
				player.setScoreboard(game.board);
			} else if (game.getSpectatorMode() < 2) {
				player.kickPlayer(game.getText(277));
			} else {
				player.setGameMode(GameMode.SPECTATOR);
				player.sendMessage(game.getText(38));
				player.teleport(game.getWorld().getSpawnLocation());
			}
		}

		if (game.isState(StateLG.FIN)) {
			fastboard.updateLines(game.score.getScoreboard3());
		}
		game.optionlg.updateNameTag();
		game.optionlg.updateCompass();
	}	
	
	@EventHandler
	private void onQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();
		String playerName = player.getName();

		FastBoard fastboard = game.boards.remove(player.getUniqueId());
		if (fastboard != null) {
			fastboard.delete();
		}

        if(game.playerLG.containsKey(playerName)) {

			PlayerLG plg = game.playerLG.get(playerName);

			if (game.isState(StateLG.LOBBY)) {
				game.score.removePlayerSize();
				game.board.getTeam(playerName).unregister();
				game.playerLG.remove(playerName);
				game.checkQueue();
				event.setQuitMessage(String.format(game.text.getText(195), game.score.getPlayerSize(), game.score.getRole(), player.getName()));
				game.clearPlayer(player);
			} else if (game.isState(StateLG.FIN) || !plg.isState(State.LIVING)) {
				player.setGameMode(GameMode.SPECTATOR);
				game.clearPlayer(player);
				player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
				event.setQuitMessage(String.format(game.getText(280), playerName));
			} else {
				event.setQuitMessage(String.format(game.text.getText(196), event.getPlayer().getName()));
				plg.setDeathTime(game.score.getTimer());
			}
		}
    }
}

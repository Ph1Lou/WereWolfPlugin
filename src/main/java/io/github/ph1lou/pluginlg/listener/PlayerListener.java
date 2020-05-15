package io.github.ph1lou.pluginlg.listener;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.Succubus;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.Thief;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Raven;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.utils.Title;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
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

		if (!game.playerLG.containsKey(uuid)) return;

		if (!game.playerLG.get(uuid).isState(State.ALIVE) && !player.hasPermission("a.use") && !game.getHosts().contains(uuid) && !game.getModerators().contains(uuid)) {
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

		if (game.config.getTimerValues().get(TimerLG.PVP) > 0) {
			event.setCancelled(true);
		}
	}


	@EventHandler
	private void onPlayerDamage(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();
		UUID uuid = player.getUniqueId();

		//Wither effect = NO_FALL
		if (player.getWorld().equals(game.getWorld()) && game.config.getTimerValues().get(TimerLG.INVULNERABILITY) > 0) {
			event.setCancelled(true);
			return;
		}

		if (!game.playerLG.containsKey(uuid)) return;

		PlayerLG plg = game.playerLG.get(uuid);

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
			if (plg.getRole() instanceof Raven || plg.hasSalvation()) {
				event.setCancelled(true);
				return;
			}
			for (List<UUID> loversCursed : game.loversManage.cursedLoversRange) {
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
		} else if (!game.playerLG.containsKey(event.getPlayer().getUniqueId())) {
			event.setRespawnLocation(game.getWorld().getSpawnLocation());
		} else if (game.isState(StateLG.START) || game.isState(StateLG.TRANSPORTATION) || (game.isState(StateLG.GAME) && game.config.isTrollSV())) {
			event.setRespawnLocation(game.playerLG.get(event.getPlayer().getUniqueId()).getSpawn());
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

		if (game.isState(StateLG.GAME) && !game.config.isTrollSV()) {

			event.setDeathMessage(null);
			event.setKeepLevel(true);

			if (!game.playerLG.containsKey(uuid)) return;

			PlayerLG plg = game.playerLG.get(uuid);

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

				if (game.playerLG.containsKey(killerUUID)) {

					PlayerLG klg = game.playerLG.get(killerUUID);

					klg.addOneKill();

					if (!klg.getRole().isCamp(Camp.VILLAGER)) {
						killer.removePotionEffect(PotionEffectType.ABSORPTION);
						killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 0, false, false));
						killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 0, false, false));
					}

					if (klg.getRole() instanceof Thief) {
						Thief thief = (Thief) klg.getRole();
						if(thief.hasPower()){
							thief.setPower(false);
							Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
								if (klg.isState(State.ALIVE)) {
									game.roleManage.thief_recover_role(killerUUID, uuid);
								} else {
									game.death_manage.deathStep1(uuid);
								}
							},7*20);
							return;
						}
					}
					if (plg.getRole() instanceof Succubus){

						Succubus succubus = (Succubus) plg.getRole();

						if(!succubus.getAffectedPlayers().isEmpty() && !succubus.hasPower()) {

							UUID targetUUID = succubus.getAffectedPlayers().get(0);

							PlayerLG trg = game.playerLG.get(targetUUID);

							if (trg.isState(State.ALIVE)) {

								succubus.clearAffectedPlayer();

								if (Bukkit.getPlayer(targetUUID) == null) {
									game.death_manage.death(targetUUID);
								} else {
									Player target = Bukkit.getPlayer(targetUUID);
									target.damage(10000);
									target.sendMessage(game.translate("werewolf.role.succubus.free_of_succubus"));
								}
								Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> game.death_manage.resurrection(uuid), 20L);

								return;
							}

						}
					}
				}
			} else plg.addKiller(null);


			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> game.death_manage.deathStep1(uuid), 20L);
		}
	}

	
	@EventHandler
	private void onJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();
		String playerName = player.getName();
		UUID uuid = player.getUniqueId();

		FastBoard fastboard = new FastBoard(player);
		fastboard.updateTitle(game.translate("werewolf.score_board.title"));
		game.boards.put(uuid, fastboard);
		Title.sendTabTitle(player, game.translate("werewolf.tab.top"), game.translate("werewolf.tab.bot"));

		if (game.isState(StateLG.LOBBY)) {

			event.setJoinMessage(null);

			if (game.getModerators().contains(uuid)) {
				player.sendMessage(game.translate("werewolf.commands.admin.moderator.message"));
				player.setGameMode(GameMode.SPECTATOR);
				player.setScoreboard(game.board);
			} else {
				game.join(player);
			}
		} else if (game.playerLG.containsKey(uuid)) {

			PlayerLG plg = game.playerLG.get(uuid);
			if(!plg.getName().equals(playerName)){
				plg.setName(playerName);
			}
			player.setScoreboard(plg.getScoreBoard());

			if (plg.isState(State.ALIVE)) {

				event.setJoinMessage(game.translate("werewolf.announcement.join_in_game", playerName));

				if (game.isState(StateLG.GAME)) {
					if (!plg.hasKit()) {
						game.roleManage.recoverRolePower(uuid);
					}
					if (plg.getAnnounceCursedLoversAFK()) {
						game.loversManage.announceCursedLovers(player);
					}
					if (plg.getAnnounceLoversAFK()) {
						game.loversManage.announceLovers(player);
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
				player.teleport(game.getWorld().getSpawnLocation());
				player.setScoreboard(game.board);
			} else if (game.getSpectatorMode() < 2) {
				player.kickPlayer(game.translate("werewolf.check.spectator_disabled"));
			} else {
				player.setGameMode(GameMode.SPECTATOR);
				player.sendMessage(game.translate("werewolf.check.already_begin"));
				player.teleport(game.getWorld().getSpawnLocation());
			}
		}

		if (game.isState(StateLG.END)) {
			fastboard.updateLines(game.score.getScoreboard3());
		}
		game.optionlg.updateNameTag();
		game.optionlg.updateCompass();
	}	
	
	@EventHandler
	private void onQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();
		String playerName = player.getName();
		UUID uuid = player.getUniqueId();

		FastBoard fastboard = game.boards.remove(player.getUniqueId());
		if (fastboard != null) {
			fastboard.delete();
		}

        if(game.playerLG.containsKey(uuid)) {

			PlayerLG plg = game.playerLG.get(uuid);

			if (game.isState(StateLG.LOBBY)) {
				game.score.removePlayerSize();
				game.playerLG.remove(uuid);
				game.checkQueue();
				event.setQuitMessage(game.translate("werewolf.announcement.leave", game.score.getPlayerSize(), game.score.getRole(), player.getName()));
				game.clearPlayer(player);
			} else if (game.isState(StateLG.END) || !plg.isState(State.ALIVE)) {
				player.setGameMode(GameMode.SPECTATOR);
				game.clearPlayer(player);
				player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
				event.setQuitMessage(game.translate("werewolf.announcement.leave_in_spec",playerName));
			} else {
				event.setQuitMessage(game.translate("werewolf.announcement.leave_in_game", playerName));
				plg.setDeathTime(game.score.getTimer());
			}
		}
    }
}

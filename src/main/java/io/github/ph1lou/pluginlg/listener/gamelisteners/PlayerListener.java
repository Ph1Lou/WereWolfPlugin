package io.github.ph1lou.pluginlg.listener.gamelisteners;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.*;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import io.github.ph1lou.pluginlg.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
		if(!player.getWorld().equals(game.getWorld())) return;

		String playerName = player.getName();

		if (!game.playerLG.containsKey(playerName)) return;

		if (!game.playerLG.get(playerName).isState(State.LIVING) && !player.hasPermission("adminLG.use") && !player.hasPermission("drop.use")) {
			event.setCancelled(true);
		}
	}


	@EventHandler
	private void onPlayerDamage(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();

		if(!player.getWorld().equals(game.getWorld())) return;

		String playerName = player.getName();

		//Wither effect = Invulnerability

		if (player.hasPotionEffect(PotionEffectType.WITHER)) {
			event.setCancelled(true);
			return;
		}

		if (!game.isState(StateLG.LOBBY) && game.config.timerValues.get(TimerLG.INVULNERABILITY) > 0) {
			event.setCancelled(true);
			return;
		}

		if (!game.playerLG.containsKey(playerName)) return;

		PlayerLG plg = game.playerLG.get(playerName);

		if (plg.isState(State.JUDGEMENT)) {
			event.setCancelled(true);
		}

		if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
			if (plg.isRole(RoleLG.CORBEAU) || plg.hasSalvation()) {
				event.setCancelled(true);
				return;
			}
			for (List<String> loversCursed : game.loversManage.cursedLoversRange) {
				if (loversCursed.contains(playerName)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}


	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event) {

		if(!event.getPlayer().getWorld().equals(game.getWorld())) return;

		if (!game.playerLG.containsKey(event.getPlayer().getName())) {
			event.setRespawnLocation(game.getWorld().getSpawnLocation());
			return;
		}

		if (game.isState(StateLG.DEBUT) || game.isState(StateLG.TRANSPORTATION) || (game.isState(StateLG.LG) && game.config.isTrollSV())) {
			event.setRespawnLocation(game.playerLG.get(event.getPlayer().getName()).getSpawn());
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
				event.getPlayer().removePotionEffect(PotionEffectType.WITHER);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, -1, false, false));
			}, 1L);
		} else {
			if (game.isState(StateLG.LOBBY)){
				Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false)), 20L);
			}
			event.setRespawnLocation(game.getWorld().getSpawnLocation());
		}

	}
	
	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event) {

		if(!event.getEntity().getWorld().equals(game.getWorld())) return;

		Player player = event.getEntity();
		String playername = player.getName();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.spigot().respawn(), 10L);

		if (game.isState(StateLG.LG) && !game.config.isTrollSV()) {
			event.setDeathMessage(null);
			event.setKeepInventory(true);
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
						if (Bukkit.getPlayer(targetName) == null) {
							game.death_manage.death(targetName);
						} else {
							Player target = Bukkit.getPlayer(targetName);
							target.damage(10000);
							target.sendMessage(game.text.getText(266));
						}
						plg.clearAffectedPlayer();
						plg.setPower(true);
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

		if(!event.getPlayer().getWorld().equals(game.getWorld())) return;

		Player player = event.getPlayer();
		String playerName = player.getName();
		TextLG text = game.text;
		FastBoard fastboard = new FastBoard(player);
		fastboard.updateTitle(text.getText(125));
		game.boards.put(player.getUniqueId(), fastboard);
		Title.sendTabTitle(player, text.getText(125), text.getText(184));

		if (game.playerLG.containsKey(playerName)){

			PlayerLG plg = game.playerLG.get(playerName);

			player.setScoreboard(plg.getScoreBoard());

			if(plg.isState(State.LIVING)) {

				event.setJoinMessage(null);

				for(Player p:Bukkit.getOnlinePlayers()) {
					if (game.getWorld().equals(p.getWorld())) {
						p.sendMessage(String.format(game.text.getText(193), playerName));
					}
				}
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
					player.performCommand("lg leave");
					return;
				}
			}

		}
		else {
			if (game.getSpectatorMode()==2 || game.getModerators().contains(player.getUniqueId())) {
				player.setGameMode(GameMode.SPECTATOR);
				player.sendMessage(text.getText(38));
			} else {
				player.performCommand("lg leave");
				return;
			}
		}

		if(game.isState(StateLG.FIN)){
			game.score.updateBoard();
		}
		game.optionlg.updateNameTag();
		game.optionlg.updateCompass();
	}	
	
	@EventHandler
	private void onQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();
		String playerName = player.getName();

		if(!player.getWorld().equals(game.getWorld())) return;

		FastBoard fastboard = game.boards.remove(player.getUniqueId());
		if (fastboard != null) {
			fastboard.delete();
		}

        if(game.playerLG.containsKey(playerName)){

        	PlayerLG plg = game.playerLG.get(playerName);

			if(game.isState(StateLG.LOBBY)) {
				event.setQuitMessage(null);
				game.score.removePlayerSize();
				game.board.getTeam(playerName).unregister();
				game.playerLG.remove(playerName);
				game.checkQueue();
				for(Player p:Bukkit.getOnlinePlayers()) {
					if (game.getWorld().equals(p.getWorld())) {
						p.sendMessage(String.format(game.text.getText(195), game.score.getPlayerSize(), game.score.getRole(), player.getName()));
					}
				}
				player.setGameMode(GameMode.ADVENTURE);
				game.clearPlayer(player);
				player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			}
			else if(game.isState(StateLG.FIN)) {
				player.setGameMode(GameMode.ADVENTURE);
				game.clearPlayer(player);
				player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			}
        	else if(plg.isState(State.LIVING)) {
				event.setQuitMessage(null);
				plg.setDeathTime(game.score.getTimer());
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (game.getWorld().equals(p.getWorld())) {
						p.sendMessage(String.format(game.text.getText(196), event.getPlayer().getName()));
					}
				}
			}
		}
    }
}

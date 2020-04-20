package io.github.ph1lou.pluginlg.listener;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.enumlg.*;
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

	private final MainLG main;

	public PlayerListener(MainLG main) {
		this.main = main;
	}

    
	@EventHandler
	private void onDropItem(PlayerDropItemEvent event) {

		String playerName = event.getPlayer().getName();

		if (!main.playerLG.containsKey(playerName)) return;

		if (!main.playerLG.get(playerName).isState(State.LIVING)) {
			event.setCancelled(true);
		}
	}


	@EventHandler
	private void onPlayerDamage(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();
		String playerName = player.getName();

		//Wither effect = Invulnerability

		if (player.hasPotionEffect(PotionEffectType.WITHER)) {
			event.setCancelled(true);
			return;
		}

		if (!main.isState(StateLG.LOBBY) && main.config.timerValues.get(TimerLG.INVULNERABILITY) > 0) {
			event.setCancelled(true);
			return;
		}

		if (!main.playerLG.containsKey(playerName)) return;

		PlayerLG plg = main.playerLG.get(playerName);

		if (plg.isState(State.JUDGEMENT)) {
			event.setCancelled(true);
		}

		if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
			if (plg.isRole(RoleLG.CORBEAU) || plg.hasSalvation()) {
				event.setCancelled(true);
				return;
			}
			for (List<String> loversCursed : main.loversManage.cursedLoversRange) {
				if (loversCursed.contains(playerName)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}


	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!main.playerLG.containsKey(event.getPlayer().getName())) return;

		if (main.isState(StateLG.DEBUT) || main.isState(StateLG.TRANSPORTATION) || (main.isState(StateLG.LG) && main.config.isTrollSV())) {
			event.setRespawnLocation(main.playerLG.get(event.getPlayer().getName()).getSpawn());
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
				event.getPlayer().removePotionEffect(PotionEffectType.WITHER);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, -1, false, false));
			}, 1L);
		} else if (main.isState(StateLG.LOBBY))
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false)), 20L);
	}
	
	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event) {

		Player player = event.getEntity();
		String playername = player.getName();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.spigot().respawn(), 10L);

		if (main.isState(StateLG.LG) && !main.config.isTrollSV()) {
			event.setDeathMessage(null);
			event.setKeepInventory(true);
			event.setKeepLevel(true);
			if (!main.playerLG.containsKey(playername)) return;

			PlayerLG plg = main.playerLG.get(playername);

			if (!plg.isState(State.LIVING)) return;

			plg.setSpawn(player.getLocation());
			plg.clearItemDeath();
			plg.setDeathTime(main.score.getTimer());
			plg.setState(State.JUDGEMENT);

			Inventory inv = Bukkit.createInventory(null, 45, playername);

			for (int i = 0; i < 40; i++) {
				inv.setItem(i, player.getInventory().getItem(i));
			}

			plg.setItemDeath(inv.getContents());

			player.setGameMode(GameMode.ADVENTURE);
			player.sendMessage(main.text.getText(130));

			if (player.getKiller() != null) {

				Player killer = player.getKiller();
				String killerName = killer.getName();
				plg.setKiller(killerName);

				if (main.playerLG.containsKey(killerName)) {

					PlayerLG klg = main.playerLG.get(killerName);

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
			} else plg.setKiller(main.text.getText(81));

			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> main.death_manage.deathStep1(playername), 20L);
		}
	}

	
	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		main.joinPlayer(event.getPlayer());
		if (main.isState(StateLG.LOBBY)) {
			event.setJoinMessage(String.format(main.text.getText(194), Bukkit.getOnlinePlayers().size(), main.score.getRole(), event.getPlayer().getName()));
		} else if (main.playerLG.containsKey(event.getPlayer().getName()) && main.playerLG.get(event.getPlayer().getName()).isState(State.LIVING)) {
			event.setJoinMessage(String.format(main.text.getText(193), event.getPlayer().getName()));
		}
	}	
	
	@EventHandler
	private void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        FastBoard board = main.boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
        if(main.isState(StateLG.LOBBY)) {
        	main.score.removePlayerSize();
			main.board.getTeam(player.getName()).unregister();
        	main.playerLG.remove(player.getName());
        	event.setQuitMessage(String.format(main.text.getText(195),main.score.getPlayerSize(),main.score.getRole(),event.getPlayer().getName()));
        }
        else if(main.playerLG.containsKey(event.getPlayer().getName()) && main.playerLG.get(event.getPlayer().getName()).isState(State.LIVING)) {
        	main.playerLG.get(event.getPlayer().getName()).setDeathTime(main.score.getTimer());
			event.setQuitMessage(String.format(main.text.getText(196),event.getPlayer().getName()));
		}
    }
}

package io.github.ph1lou.werewolfplugin.listeners;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.ModerationManagerAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.*;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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

		if (player.getGameMode().equals(GameMode.SPECTATOR)) {
			event.setCancelled(true);
		} else if (game.getPlayersWW().containsKey(player.getUniqueId())) {
			if (game.getPlayersWW().get(player.getUniqueId()).isState(StatePlayer.JUDGEMENT)) {
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
		World world = player.getWorld();

		//Wither effect = NO_FALL

		if (world.equals(game.getMapManager().getWorld()) && game.getConfig().getTimerValues().get(Timers.INVULNERABILITY.getKey()) > 0) {
			event.setCancelled(true);
			return;
		}

		if (!game.getPlayersWW().containsKey(uuid)) return;

		PlayerWW plg = game.getPlayersWW().get(uuid);

		if (plg.isState(StatePlayer.JUDGEMENT)) {
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

		if (game.isState(StateGame.LOBBY)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false)), 20L);
		} else if (!game.getPlayersWW().containsKey(event.getPlayer().getUniqueId())) {
			event.setRespawnLocation(game.getMapManager().getWorld().getSpawnLocation());
		} else if (game.isState(StateGame.START) || game.isState(StateGame.TRANSPORTATION) || (game.isState(StateGame.GAME) && game.getConfig().isTrollSV())) {
			event.setRespawnLocation(game.getPlayersWW().get(event.getPlayer().getUniqueId()).getSpawn());
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
				event.getPlayer().removePotionEffect(PotionEffectType.WITHER);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, -1, false, false));
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

			if (!game.getPlayersWW().containsKey(uuid)) return;

			PlayerWW plg = game.getPlayersWW().get(uuid);

			if (!plg.isState(StatePlayer.ALIVE)) return;

			plg.setSpawn(player.getLocation());
			plg.clearItemDeath();
			plg.setState(StatePlayer.JUDGEMENT);

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
                if (!game.isState(StateGame.END)) {
                    FirstDeathEvent firstDeathEvent = new FirstDeathEvent(uuid);
                    Bukkit.getPluginManager().callEvent(firstDeathEvent);
                }


            }, 20L);
		}
	}


	
	@EventHandler
	private void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        ModerationManagerAPI moderationManager = game.getModerationManager();
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();

        FastBoard fastboard = new FastBoard(player);
        fastboard.updateTitle(game.translate("werewolf.score_board.title"));
        game.getBoards().put(uuid, fastboard);
        VersionUtils.getVersionUtils().sendTabTitle(player, game.translate("werewolf.tab.top"), game.translate("werewolf.tab.bot"));
        event.setJoinMessage(null);

        if (game.isState(StateGame.LOBBY)) {

            if (moderationManager.getModerators().contains(uuid)) {
                player.sendMessage(game.translate("werewolf.commands.admin.moderator.message"));
                player.setGameMode(GameMode.SPECTATOR);
                event.setJoinMessage(game.translate("werewolf.announcement.join_moderator", playerName));

            } else if (moderationManager.getQueue().contains(uuid)) {

                moderationManager.checkQueue();

                if (moderationManager.getQueue().contains(uuid)) {
                    event.setJoinMessage(game.translate("werewolf.announcement.queue_rejoin", playerName, game.getModerationManager().getQueue().indexOf(uuid) + 1));
                }

            } else {
                game.join(player);
            }

        } else if (game.getPlayersWW().containsKey(uuid)) {

			PlayerWW plg = game.getPlayersWW().get(uuid);
			if(!plg.getName().equals(playerName)){
				plg.setName(playerName);
			}

			if (plg.isState(StatePlayer.ALIVE)) {

				event.setJoinMessage(game.translate("werewolf.announcement.join_in_game", playerName));

				if (game.isState(StateGame.GAME)) {
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
			else if (plg.isState(StatePlayer.DEATH)) {

				if (game.getConfig().getSpectatorMode() > 0 || moderationManager.getHosts().contains(player.getUniqueId())) {
                    player.setGameMode(GameMode.SPECTATOR);
                    event.setJoinMessage(game.translate("werewolf.announcement.join_in_spec"));
                } else {
					player.kickPlayer(game.translate("werewolf.check.death_spectator"));
				}
			}
		}
		else {
			if (moderationManager.getModerators().contains(uuid)) {
                event.setJoinMessage(game.translate("werewolf.announcement.join_moderator", playerName));
                player.sendMessage(game.translate("werewolf.commands.admin.moderator.message"));
                player.setGameMode(GameMode.SPECTATOR);
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.teleport(game.getMapManager().getWorld().getSpawnLocation()), 10);
            } else if (game.getConfig().getSpectatorMode() < 2) {
				player.kickPlayer(game.translate("werewolf.check.spectator_disabled"));
			} else {
                player.setGameMode(GameMode.SPECTATOR);
                event.setJoinMessage(game.translate("werewolf.announcement.join_spec", playerName));
                player.sendMessage(game.translate("werewolf.check.already_begin"));
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.teleport(game.getMapManager().getWorld().getSpawnLocation()), 10);

            }
		}
		game.getScenarios().updateCompass();
	}

	@EventHandler
	public void onLanguageUpdate(UpdateLanguageEvent event) {

		for (Player player : Bukkit.getOnlinePlayers()) {
			VersionUtils.getVersionUtils().sendTabTitle(player, game.translate("werewolf.tab.top"), game.translate("werewolf.tab.bot"));
			if (game.getBoards().containsKey(player.getUniqueId())) {
				game.getBoards().get(player.getUniqueId()).updateTitle(game.translate("werewolf.score_board.title"));
			}
		}
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        event.setQuitMessage(null);
        FastBoard fastboard = game.getBoards().remove(player.getUniqueId());
        if (fastboard != null) {
            fastboard.delete();
        }

        if (game.getPlayersWW().containsKey(uuid)) {

            PlayerWW plg = game.getPlayersWW().get(uuid);

            if (game.isState(StateGame.LOBBY)) {
				game.getScore().removePlayerSize();
				game.getPlayersWW().remove(uuid);
				game.getModerationManager().checkQueue();
				event.setQuitMessage(game.translate("werewolf.announcement.leave", game.getScore().getPlayerSize(), game.getScore().getRole(), player.getName()));
				game.clearPlayer(player);
			} else if (game.isState(StateGame.END) || !plg.isState(StatePlayer.ALIVE)) {
                player.setGameMode(GameMode.SPECTATOR);
                game.clearPlayer(player);
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                event.setQuitMessage(game.translate("werewolf.announcement.leave_in_spec", playerName));
            } else {

                event.setQuitMessage(game.translate("werewolf.announcement.leave_in_game", playerName));
                plg.setDeathTime(game.getScore().getTimer());
            }
        } else {
            if (game.getModerationManager().getQueue().contains(uuid)) {
                event.setQuitMessage(game.translate("werewolf.announcement.spectator_leave", playerName));
            } else event.setQuitMessage(game.translate("werewolf.announcement.leave_spec", playerName));
        }
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFirstDeath(FirstDeathEvent event) {

		if (event.isCancelled()) return;

		UUID uuid = event.getUuid();
		PlayerWW plg = game.getPlayersWW().get(uuid);
		SecondDeathEvent secondDeathEvent = new SecondDeathEvent(uuid);
		Bukkit.getPluginManager().callEvent(secondDeathEvent);
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> {
            if (!game.isState(StateGame.END)) {
                if (plg.isState(StatePlayer.JUDGEMENT) && !secondDeathEvent.isCancelled()) {

                    plg.setCanBeInfect(false);
                    ThirdDeathEvent thirdDeathEvent = new ThirdDeathEvent(uuid);
                    Bukkit.getPluginManager().callEvent(thirdDeathEvent);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> {
                        if (!game.isState(StateGame.END)) {
                            if (plg.isState(StatePlayer.JUDGEMENT) && !thirdDeathEvent.isCancelled()) {
                                game.death(uuid);
                            }
                        }

                    }, 7 * 20);
                }
            }

        }, 7 * 20);
	}


	@EventHandler
	public void onFinalDeath(FinalDeathEvent event) {

		UUID uuid = event.getUuid();
		Player player = Bukkit.getPlayer(uuid);
		PlayerWW playerWW = game.getPlayersWW().get(uuid);
		World world = game.getMapManager().getWorld();
		String roleLG = playerWW.getRole().getKey();

		if (player != null) {

			if (playerWW.isState(StatePlayer.ALIVE)) {

				playerWW.setSpawn(player.getLocation());
				playerWW.clearItemDeath();

				Inventory inv = Bukkit.createInventory(null, 45);

				for (int j = 0; j < 40; j++) {
					inv.setItem(j, player.getInventory().getItem(j));
				}
				playerWW.setItemDeath(inv.getContents());
			}
		}

		if (playerWW.isState(StatePlayer.DEATH)) return;

		playerWW.setDeathTime(game.getScore().getTimer());


		if (playerWW.isThief()) {
			roleLG = "werewolf.role.thief.display";
		}

		game.getConfig().getRoleCount().put(roleLG, game.getConfig().getRoleCount().get(roleLG) - 1);

		if (game.getConfig().getConfigValues().get(Configs.SHOW_ROLE_TO_DEATH.getKey())) {
			Bukkit.broadcastMessage(game.translate("werewolf.announcement.death_message_with_role", playerWW.getName(), game.translate(roleLG)));
		} else Bukkit.broadcastMessage(game.translate("werewolf.announcement.death_message", playerWW.getName()));

		playerWW.setState(StatePlayer.DEATH);
		game.getScore().removePlayerSize();


		for (ItemStack i : Stream.concat(playerWW.getItemDeath().stream(), game.getStuffs().getDeathLoot().stream()).collect(Collectors.toList())) {
			if (i != null) {
				world.dropItem(playerWW.getSpawn(), i);
			}
		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			Sounds.AMBIENCE_THUNDER.play(p);
		}

		if (!playerWW.getLovers().isEmpty()) {
			game.getLoversManage().checkLovers(uuid);
		}
		if (playerWW.getCursedLovers() != null) {
			game.getLoversManage().checkCursedLovers(uuid);
		}
		if (playerWW.getAmnesiacLoverUUID() != null) {
			game.getLoversManage().checkAmnesiacLovers(uuid);
		}

		if (player != null) {

			player.setGameMode(GameMode.SPECTATOR);
			TextComponent msg = new TextComponent(game.translate("werewolf.bug"));
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/GXXCVUA"));
			player.spigot().sendMessage(msg);
			if (game.getConfig().getSpectatorMode() == 0) {
				player.kickPlayer(game.translate("werewolf.check.death_spectator"));
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), game::checkVictory);
	}

	@EventHandler
	public void onResurrection(ResurrectionEvent event) {

		UUID uuid = event.getPlayerUUID();
		PlayerWW plg = game.getPlayersWW().get(uuid);
		Player player = Bukkit.getPlayer(uuid);

		if (plg.isState(StatePlayer.ALIVE)) return;

		if (player != null) {
			plg.getRole().recoverPotionEffect(player);
		}
		game.getMapManager().transportation(uuid, Math.random() * Math.PI * 2, game.translate("werewolf.announcement.resurrection"));
		plg.setState(StatePlayer.ALIVE);

		Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), game::checkVictory);
	}


}

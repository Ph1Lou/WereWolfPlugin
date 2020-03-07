package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.*;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import fr.mrmicky.fastboard.FastBoard;


public class PlayerListener implements Listener {

	private final MainLG main;
	
	public PlayerListener(MainLG main) {
		this.main=main;
	}
	
	@EventHandler
	private void onCloseInvent(InventoryCloseEvent event) {
		
		Player player =  (Player) event.getPlayer();
		Inventory invent = player.getInventory();
		if(!main.isState(StateLG.LG)) return;
		if(!main.isDay(Day.NIGHT)) return;
		if(!main.playerlg.containsKey(player.getName()) || (!main.playerlg.get(player.getName()).isRole(RoleLG.PETITE_FILLE) && !main.playerlg.get(player.getName()).isRole(RoleLG.LOUP_PERFIDE))) return;

		if(invent.getItem(36)==null && invent.getItem(37)==null && invent.getItem(38)==null && invent.getItem(39)==null) {
			if(main.playerlg.get(player.getName()).hasPower()) {
				player.sendMessage(main.text.getText(129));
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE,0,false,false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE,0,false,false));
				if(main.playerlg.get(player.getName()).isCamp(Camp.LG) ) {
					player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				}
				main.playerlg.get(player.getName()).setPower(false);
				main.optionlg.updateScenario();
			}	
		}
		else if(!main.playerlg.get(player.getName()).hasPower()) {
			player.sendMessage(main.text.getText(18));
			if(main.playerlg.get(player.getName()).isCamp(Camp.LG) ) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1,false,false));
			}
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.removePotionEffect(PotionEffectType.WEAKNESS);
			main.playerlg.get(player.getName()).setPower(true);
			main.optionlg.updateScenario();
		}
	}
    
	@EventHandler
	private void onDropItem(PlayerDropItemEvent event) {
		if(event.getPlayer().getGameMode().equals(GameMode.ADVENTURE) || event.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
			event.setCancelled(true);
		}
	}



	@EventHandler
	private void onPlayerDamage(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();
		String playername = player.getName();
		if(player.getGameMode().equals(GameMode.ADVENTURE)) {
			event.setCancelled(true);
			return;
		}
		if (main.config.scenario.get(ScenarioLG.FIRE_LESS) && (event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)) {
			event.setCancelled(true);
		}
		if (!main.playerlg.containsKey(playername)) return;
		if ((main.playerlg.get(playername).isRole(RoleLG.CORBEAU) || main.playerlg.get(playername).hasSalvation() || main.isState(StateLG.TELEPORTATION) || main.config.scenario.get(ScenarioLG.NO_FALL)) && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
			event.setCancelled(true);
			return;
		}
		if(event.getCause().equals(EntityDamageEvent.DamageCause.POISON) && main.config.scenario.get(ScenarioLG.NO_POISON)) {
			event.setCancelled(true);
			return;
		}
		if (player.getKiller() == null) return;
		if(main.config.scenario.get(ScenarioLG.SLOW_BOW) && event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE )) {
			player.removePotionEffect(PotionEffectType.SLOW);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160,0,false,false));
			return;
		}

		String killername = player.getKiller().getName();
		if (!main.playerlg.containsKey(killername)) return;
		if ((main.playerlg.get(killername).isRole(RoleLG.ASSASSIN) && !main.isDay(Day.NIGHT)) || (main.playerlg.get(killername).isCamp(Camp.LG) && main.isDay(Day.NIGHT)) ) {
			if(!player.getItemInHand().getType().equals(Material.DIAMOND_SWORD) && !player.getItemInHand().getType().equals(Material.IRON_SWORD)){
				return;
			}
			event.setDamage(event.getDamage()*main.config.getStrengthRate());
		}
		
	}

	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event) {
		if(!main.playerlg.containsKey(event.getPlayer().getName())) return;
		if(main.isState(StateLG.DEBUT) || main.isState(StateLG.TELEPORTATION)) event.setRespawnLocation(main.playerlg.get(event.getPlayer().getName()).getSpawn());
		else if(main.isState(StateLG.LOBBY)) Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0,false,false)), 20L);
	}
	
	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event) {
		
		Player player = event.getEntity();
		String playername = player.getName();

		if (main.isState(StateLG.LG)) {
			event.setDeathMessage(null);
			event.setKeepInventory(true);
			event.setKeepLevel(true);
			if(!main.playerlg.containsKey(playername)) return;
			
			PlayerLG plg = main.playerlg.get(playername);
			
			if(!plg.isState(State.VIVANT)) return;
			plg.setSpawn(player.getLocation());
			plg.clearItemDeath();
			plg.setItemDeath(player.getInventory().getContents().clone());
			
			for(ItemStack i:main.stufflg.getDeathLoot()) {
				plg.addItemDeath(i);	
			}
			
			if(player.getInventory().getHelmet()!=null) {
				plg.addItemDeath(player.getInventory().getHelmet());
			}
			if(player.getInventory().getChestplate()!=null) {
				plg.addItemDeath(player.getInventory().getChestplate());
			}
			if(player.getInventory().getLeggings()!=null) {
				plg.addItemDeath(player.getInventory().getLeggings());
			}
			if(player.getInventory().getBoots()!=null) {
				plg.addItemDeath(player.getInventory().getBoots());
			}
			
			player.setGameMode(GameMode.ADVENTURE);
			player.sendMessage(main.text.getText(130));
			
			if(player.getKiller()!=null) {
				String killername = player.getKiller().getName();
				main.death_manage.deathstep1(killername,playername);
			}
			else{
				main.death_manage.deathstep1("§2PVE",playername);
			}
		}
		
	}
	
	@EventHandler
	private void onChat(AsyncPlayerChatEvent event) {
		
		if (!main.config.tool_switch.get(ToolLG.CHAT)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(main.text.getText(123));
		}
	}
	
	@EventHandler
	private void onCommand(PlayerCommandPreprocessEvent event) {
		
		Player player = event.getPlayer();
		String[] args = event.getMessage().split(" ");
        if (args[0].equalsIgnoreCase("/me") || args[0].equalsIgnoreCase("/minecraft:me")) {
            event.setCancelled(true);
            player.sendMessage(main.text.getText(131));
        }
        
        if (args[0].equalsIgnoreCase("/tellraw") || args[0].equalsIgnoreCase("/msg") || args[0].equalsIgnoreCase("/tell") || args[0].equalsIgnoreCase("/minecraft:tell")) {
        	
        	event.setCancelled(true);
        	if(args.length<=2) return;
        	if (Bukkit.getPlayer(args[1])==null) {
        		player.sendMessage(main.text.getText(132));
        		return;
        	}
        	if (Bukkit.getPlayer(args[1]).hasPermission("adminlg.use") || player.hasPermission("adminlg.use")) {
        		Player receveur = Bukkit.getPlayer(args[1]);
        		StringBuilder sb = new StringBuilder();
    			for(String w:args) {
    				sb.append(w).append(" ");
    			}
    			sb.delete(0,args[0].length()+args[1].length()+2);
        		receveur.sendMessage(String.format(main.text.getText(133),player.getName(),sb.toString()));
        		player.sendMessage(String.format(main.text.getText(134),args[1],sb.toString()));
				receveur.playSound(receveur.getLocation(),Sound.ANVIL_USE, 1, 20);
				return;
        	}
        	player.sendMessage(main.text.getText(131));
        }
	}
	
	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		main.joinPlayer(event.getPlayer()) ;
		if(main.isState(StateLG.LOBBY)) {
			event.setJoinMessage(String.format(main.text.getText(194),Bukkit.getOnlinePlayers().size(),main.score.getRole(),event.getPlayer().getName()));
		}
		else if(main.playerlg.containsKey(event.getPlayer().getName()) && main.playerlg.get(event.getPlayer().getName()).isState(State.VIVANT)) {
			event.setJoinMessage(String.format(main.text.getText(193),event.getPlayer().getName()));
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
        	main.playerlg.remove(player.getName());
        	event.setQuitMessage(String.format(main.text.getText(195),main.score.getPlayerSize(),main.score.getRole(),event.getPlayer().getName()));
        }
        else if(main.playerlg.containsKey(event.getPlayer().getName()) && main.playerlg.get(event.getPlayer().getName()).isState(State.VIVANT)) {
        	main.playerlg.get(event.getPlayer().getName()).setDeathTime(main.score.getTimer());
			event.setQuitMessage(String.format(main.text.getText(196),event.getPlayer().getName()));
        }	
        main.score.updateBoard();	
    }
}

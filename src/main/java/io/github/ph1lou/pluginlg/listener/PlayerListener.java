package io.github.ph1lou.pluginlg.listener;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


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
		if(!main.playerLG.containsKey(player.getName()) || (!main.playerLG.get(player.getName()).isRole(RoleLG.PETITE_FILLE) && !main.playerLG.get(player.getName()).isRole(RoleLG.LOUP_PERFIDE))) return;

		if(invent.getItem(36)==null && invent.getItem(37)==null && invent.getItem(38)==null && invent.getItem(39)==null) {
			if(main.playerLG.get(player.getName()).hasPower()) {
				player.sendMessage(main.text.getText(129));
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE,0,false,false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE,0,false,false));
				if(main.playerLG.get(player.getName()).isCamp(Camp.LG) ) {
					player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				}
				main.playerLG.get(player.getName()).setPower(false);
				main.optionlg.updateNameTag();
			}	
		}
		else if(!main.playerLG.get(player.getName()).hasPower()) {
			player.sendMessage(main.text.getText(18));
			if(main.playerLG.get(player.getName()).isCamp(Camp.LG) ) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1,false,false));
			}
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.removePotionEffect(PotionEffectType.WEAKNESS);
			main.playerLG.get(player.getName()).setPower(true);
			main.optionlg.updateNameTag();
		}
	}
    
	@EventHandler
	private void onDropItem(PlayerDropItemEvent event) {
		if(event.getPlayer().getGameMode().equals(GameMode.ADVENTURE) || event.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void onPlayerDamage(EntityDamageByEntityEvent event) {

		if(!(event.getEntity() instanceof Player)) return;
		if(!(event.getDamager() instanceof Player)) return;
		Player damager = (Player) event.getDamager();
		for(PotionEffect p:damager.getActivePotionEffects()){
			if(p.getType().equals(PotionEffectType.INCREASE_DAMAGE)){
				if(damager.getItemInHand().getType().equals(Material.DIAMOND_SWORD) || damager.getItemInHand().getType().equals(Material.IRON_SWORD)){
					event.setDamage(event.getDamage()*(main.config.getStrengthRate()/100f));
				}
			}
		}
	}



	@EventHandler
	private void onPlayerDamage(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();
		String playername = player.getName();
		for(PotionEffect p:player.getActivePotionEffects()){
			if(p.getType().equals(PotionEffectType.WITHER)){
				event.setCancelled(true);
				return;
			}
		}
		if(player.getGameMode().equals(GameMode.ADVENTURE) || main.config.value.get(TimerLG.INVULNERABILITY)>0) {
			event.setCancelled(true);
		}
		else if (main.config.scenario.get(ScenarioLG.FIRE_LESS) && (event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)) {
			event.setCancelled(true);
		}
		else if (main.playerLG.containsKey(playername) && (main.playerLG.get(playername).isRole(RoleLG.CORBEAU) || main.playerLG.get(playername).hasSalvation() || main.config.scenario.get(ScenarioLG.NO_FALL)) && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
			event.setCancelled(true);
		}
		else if(event.getCause().equals(EntityDamageEvent.DamageCause.POISON) && main.config.scenario.get(ScenarioLG.NO_POISON)) {
			event.setCancelled(true);
		}
		else if(main.config.scenario.get(ScenarioLG.SLOW_BOW) && event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			player.removePotionEffect(PotionEffectType.SLOW);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160,0,false,false));
		}

	}

	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event) {
		if(!main.playerLG.containsKey(event.getPlayer().getName())) return;
		if(main.isState(StateLG.DEBUT) || main.isState(StateLG.TRANSPORTATION)) event.setRespawnLocation(main.playerLG.get(event.getPlayer().getName()).getSpawn());
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
			if(!main.playerLG.containsKey(playername)) return;
			
			PlayerLG plg = main.playerLG.get(playername);
			
			if(!plg.isState(State.LIVING)) return;
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
				main.death_manage.deathStep1(killername,playername);
			}
			else{
				main.death_manage.deathStep1(main.text.getText(81),playername);
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
        		Player recipient = Bukkit.getPlayer(args[1]);
        		StringBuilder sb = new StringBuilder();
    			for(String w:args) {
    				sb.append(w).append(" ");
    			}
    			sb.delete(0,args[0].length()+args[1].length()+2);
        		recipient.sendMessage(String.format(main.text.getText(133),player.getName(),sb.toString()));
        		player.sendMessage(String.format(main.text.getText(134),args[1],sb.toString()));
				recipient.playSound(recipient.getLocation(),Sound.ANVIL_USE, 1, 20);
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
		else if(main.playerLG.containsKey(event.getPlayer().getName()) && main.playerLG.get(event.getPlayer().getName()).isState(State.LIVING)) {
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
        	main.playerLG.remove(player.getName());
        	event.setQuitMessage(String.format(main.text.getText(195),main.score.getPlayerSize(),main.score.getRole(),event.getPlayer().getName()));
        }
        else if(main.playerLG.containsKey(event.getPlayer().getName()) && main.playerLG.get(event.getPlayer().getName()).isState(State.LIVING)) {
        	main.playerLG.get(event.getPlayer().getName()).setDeathTime(main.score.getTimer());
			event.setQuitMessage(String.format(main.text.getText(196),event.getPlayer().getName()));
        }	
        main.score.updateBoard();	
    }
}

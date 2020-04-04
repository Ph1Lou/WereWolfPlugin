package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;



public class DeathManagementLG {
	
	final MainLG main;
	
	public DeathManagementLG(MainLG main) {
		this.main=main;
	}
	
	public void deathStep1(String killername, String playername) {
		
		PlayerLG plg = main.playerLG.get(playername);
		plg.setKiller(killername);
		plg.setDeathTime(main.score.getTimer());
		plg.setState(State.JUDGEMENT);
		
		if(Bukkit.getPlayer(playername)!=null) {
			Player player = Bukkit.getPlayer(playername);
			player.spigot().respawn();
		}
		
		if(main.playerLG.containsKey(killername)) {
			
			PlayerLG klg = main.playerLG.get(killername);
			
			main.playerLG.get(killername).addOneKill();
			
			if(Bukkit.getPlayer(killername)!=null) {
				
				Player killer = Bukkit.getPlayer(killername);
				if(main.config.scenario.get(ScenarioLG.NO_CLEAN_UP)){
					killer.setHealth(Math.min(killer.getHealth() + 4, killer.getMaxHealth()));
				}
				if(!klg.isCamp(Camp.VILLAGE)){
					killer.removePotionEffect(PotionEffectType.ABSORPTION);
					killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200,0,false,false));
					killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200,0,false,false));	
				}	
				if(klg.isRole(RoleLG.VOLEUR) && klg.hasPower()) {
					plg.setStolen(true);
					main.playerLG.get(killername).setPower(false);
					return;
				}
			}	
		}
		deathStep2(killername,playername);
	}

	private void deathStep2(String killername, String playername) {
		
		PlayerLG plg = main.playerLG.get(playername);
		
		if (plg.isRole(RoleLG.ANCIEN) && plg.hasPower()){
				
			plg.setPower(false);

			if (Bukkit.getPlayer(playername)!=null) {
				
				Player player=Bukkit.getPlayer(playername);
				
				if(main.playerLG.containsKey(killername) && main.playerLG.get(killername).isCamp(Camp.VILLAGE)) {
					player.setMaxHealth(player.getHealth()-6);
				}
				player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			}
			resurrection(playername);
		}
		else if(main.playerLG.containsKey(killername) && (main.playerLG.get(killername).isCamp(Camp.LG) || main.playerLG.get(killername).isRole(RoleLG.LOUP_GAROU_BLANC))) {
			
			plg.setCanBeInfect(true);
			
			if(main.config.tool_switch.get(ToolLG.AUTO_REZ_INFECT) && plg.isRole(RoleLG.INFECT) && plg.hasPower()) {
				plg.setPower(false);
				plg.setCanBeInfect(false);
				resurrection(playername);
				return;
			}
			for(String infect_name:main.playerLG.keySet()) {
				
				if(main.playerLG.get(infect_name).isState(State.LIVING) && main.playerLG.get(infect_name).isRole(RoleLG.INFECT) && !infect_name.equals(playername) && main.playerLG.get(infect_name).hasPower() && Bukkit.getPlayer(infect_name)!=null) {
					TextComponent infect_msg = new TextComponent(String.format(main.text.powerUse.get(RoleLG.INFECT),playername));
					infect_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/lg infecter "+playername));
					Bukkit.getPlayer(infect_name).spigot().sendMessage(infect_msg);
				}
			}					
		}
		else {
			plg.setDeathTime(main.score.getTimer()-7);
			deathStep3(playername);
		}
	}
	
	private void deathStep3(String playername) {
		
		if(main.config.tool_switch.get(ToolLG.AUTO_REZ_WITCH) && main.playerLG.get(playername).isRole(RoleLG.SORCIERE) && main.playerLG.get(playername).hasPower()) {
			main.playerLG.get(playername).setPower(false);
			resurrection(playername);
			return;
		}
		
		for(String witch_name:main.playerLG.keySet()) {
			
			if(main.playerLG.get(witch_name).isState(State.LIVING) && main.playerLG.get(witch_name).isRole(RoleLG.SORCIERE) && !witch_name.equals(playername) && main.playerLG.get(witch_name).hasPower() && Bukkit.getPlayer(witch_name)!=null ) {
				TextComponent witch_msg = new TextComponent(String.format(main.text.powerUse.get(RoleLG.SORCIERE),playername));
				witch_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/lg sauver "+playername));
				Bukkit.getPlayer(witch_name).spigot().sendMessage(witch_msg);
			}
		}
	}
	
	public void deathTimer() {
		
		for(String playername:main.playerLG.keySet()) {
			
			PlayerLG plg = main.playerLG.get(playername);
			
			if(plg.isState(State.JUDGEMENT)) {

				int timer = main.score.getTimer() - plg.getDeathTime();
				
				if (timer>7 && plg.canBeInfect()) {
					plg.setCanBeInfect(false);
					deathStep3(playername);
				}
				else if (timer>14) {
					
					if(plg.hasBeenStolen() ) {
						
						if(main.playerLG.get(plg.getKiller()).isState(State.LIVING)) {
							main.role_manage.thief_recover_role(plg.getKiller(),playername);
						}
						else {
							plg.setDeathTime(main.score.getTimer());
							String killername = plg.getKiller();
							plg.setStolen(false);
							deathStep2(killername,playername);
						}
					}
					else death(playername);
				}
			}
		}			
	}
	

	public void death(String playername) {
		
		World world = Bukkit.getWorld("world");
		PlayerLG plg = main.playerLG.get(playername);
		RoleLG role = plg.getRole();
		if(plg.isThief()) {
			role=RoleLG.VOLEUR;
		}
		else if((plg.isRole(RoleLG.ANGE_GARDIEN) || plg.isRole(RoleLG.ANGE_DECHU)) && !plg.hasPower()) {
			role=RoleLG.ANGE;
		}
		main.config.role_count.put(role,main.config.role_count.get(role)-1);
		plg.setState(State.MORT);
		main.score.removePlayerSize();

		if(main.config.tool_switch.get(ToolLG.SHOW_ROLE_TO_DEATH)) {
			Bukkit.broadcastMessage(String.format(main.text.getText(28),playername,main.text.translateRole.get(role)));
		}
		else Bukkit.broadcastMessage(String.format(main.text.getText(29),playername));
		
		for(ItemStack i:plg.getItemDeath()) {
			if(i!=null) {
				if(role.equals(RoleLG.LOUP_PERFIDE) ||role.equals(RoleLG.PETITE_FILLE)){
					if(i.getEnchantments().containsKey(Enchantment.KNOCKBACK)){
						i.removeEnchantment(Enchantment.KNOCKBACK);
					}
				}
				world.dropItem(plg.getSpawn(),i);
			}		
		}
		
		for(Player p:Bukkit.getOnlinePlayers()) {
			
			if(main.config.scenario.get(ScenarioLG.COMPASS_TARGET_LAST_DEATH)) {
				if(plg.getSpawn()!=null) {
					p.setCompassTarget(plg.getSpawn());
				}
			}
			p.playSound(p.getLocation(),Sound.AMBIENCE_THUNDER, 1, 20);
			if(p.getName().equals(playername)) {
				p.setGameMode(GameMode.SPECTATOR);

				TextComponent msg = new TextComponent(main.text.getText(186));
				msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://discord.gg/GXXCVUA"));
				p.spigot().sendMessage(msg);
				
			}
		
		}	

		if(main.playerLG.containsKey(plg.getKiller()) && plg.isCamp(Camp.VILLAGE) && main.playerLG.get(plg.getKiller()).isRole(RoleLG.LOUP_AMNESIQUE)  && main.playerLG.get(plg.getKiller()).hasPower()){
			main.role_manage.newLG(plg.getKiller());
			main.playerLG.get(plg.getKiller()).setPower(false);
		}

		if(main.playerLG.containsKey(plg.getKiller()) && main.playerLG.get(plg.getKiller()).isRole(RoleLG.TUEUR_EN_SERIE)){
			if(Bukkit.getPlayer(plg.getKiller())!=null){
				Player killer = Bukkit.getPlayer(plg.getKiller());
				killer.setMaxHealth(killer.getMaxHealth()+2);
			}
		}
		
		if (role.equals(RoleLG.TRUBLION)) {
			troublemakerDeath();
		}
		if (!plg.getTargetOf().isEmpty()) {
			targetDeath(playername);
		}
		if(!plg.getDisciple().isEmpty()) {
			masterDeath(playername);
		}
		if(role.equals(RoleLG.SOEUR)) {
			sisterDeath(playername) ;
		}

		if(!plg.getCouple().isEmpty()) {
			check_couple(playername);
		}
		
		if(!main.isState(StateLG.FIN)) {
			main.endlg.check_victory();
		}
		else return;

		if(main.config.tool_switch.get(ToolLG.EVENT_SEER_DEATH) && (role.equals(RoleLG.VOYANTE) || role.equals(RoleLG.VOYANTE_BAVARDE))) {
			main.eventslg.event1();
			main.config.tool_switch.put(ToolLG.EVENT_SEER_DEATH,false);
		}
	}





	private void check_couple(String playername) {
		
		int i=0;
		
		while(i<main.couple_manage.couple_range.size() && !main.couple_manage.couple_range.get(i).contains(playername)) {
			i++;
		}
		
		if(i<main.couple_manage.couple_range.size()) {
			
			main.couple_manage.couple_range.get(i).remove(playername);
			
			while(!main.couple_manage.couple_range.get(i).isEmpty() && main.playerLG.get(main.couple_manage.couple_range.get(i).get(0)).isState(State.MORT)) {
				main.couple_manage.couple_range.get(i).remove(0);
			}
			
			if(!main.couple_manage.couple_range.get(i).isEmpty()) {
				String c1=main.couple_manage.couple_range.get(i).get(0);
				PlayerLG plc1 = main.playerLG.get(c1);
				Bukkit.broadcastMessage(String.format(main.text.getText(30),c1));
				if(Bukkit.getPlayer(c1)!=null) {
					if(plc1.isState(State.LIVING)){
						Player player = Bukkit.getPlayer(c1);
						plc1.setSpawn(player.getLocation());
						plc1.clearItemDeath();
						plc1.setItemDeath(player.getInventory().getContents());
						plc1.addItemDeath(player.getInventory().getHelmet());
						plc1.addItemDeath(player.getInventory().getChestplate());
						plc1.addItemDeath(player.getInventory().getBoots());
						plc1.addItemDeath(player.getInventory().getLeggings());
					}
				}
				main.playerLG.get(playername).setKiller("§dLove");
				death(c1);
			}
			else {
				main.couple_manage.couple_range.remove(i);
				main.config.role_count.put(RoleLG.COUPLE,main.config.role_count.get(RoleLG.COUPLE)-1);
			}
		}		
	}

	public void resurrection(String playername) {
		
		if(Bukkit.getPlayer(playername)!=null) {
			Player player = Bukkit.getPlayer(playername);
			for(PotionEffectType p:main.role_manage.effect_recover(playername)) {
				player.addPotionEffect(new PotionEffect(p,Integer.MAX_VALUE,0,false,false));
			}
		}	
		transportation(playername, Math.random()*Bukkit.getOnlinePlayers().size(), main.text.getText(31));
		main.playerLG.get(playername).setState(State.LIVING);
		if(!main.isState(StateLG.FIN)) {
			main.endlg.check_victory();
		}
	}

	private void troublemakerDeath() {
		int i=0;
		for (String p:main.playerLG.keySet()) {
			if(main.playerLG.get(p).isState(State.LIVING)) {
				transportation(p, i,main.text.getText(32));
				i++;
			}
		}
	}
	
	private void sisterDeath(String playername) {
		
		for(String sister_name:main.playerLG.keySet()) {
			if(main.playerLG.get(sister_name).isState(State.LIVING) && main.playerLG.get(sister_name).isRole(RoleLG.SOEUR) && Bukkit.getPlayer(sister_name)!=null) {
				Bukkit.getPlayer(sister_name).sendMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.SOEUR),playername,main.playerLG.get(playername).getKiller()));
			}
		}
	}
	
	private void targetDeath(String playername) {
		
		for(String angel_name:main.playerLG.get(playername).getTargetOf()) {
			
			if(main.playerLG.get(angel_name).isState(State.LIVING) && Bukkit.getPlayer(angel_name) != null) {
				
				Player ange = Bukkit.getPlayer(angel_name);
				
				if(main.playerLG.get(angel_name).isRole(RoleLG.ANGE_DECHU)) {
					if(main.playerLG.get(playername).getKiller().equals(angel_name)) {
						ange.setMaxHealth(ange.getMaxHealth()+6);
						ange.sendMessage(main.text.powerUse.get(RoleLG.ANGE_DECHU));
					}	
				}
				else {
					ange.setMaxHealth(ange.getMaxHealth()-4);
					ange.sendMessage(main.text.powerUse.get(RoleLG.ANGE_GARDIEN));
				}
			}	
		}
		
	}
	
	private void masterDeath(String playername) {
		
		for(String savage_name:main.playerLG.get(playername).getDisciple()) {
			
			if(main.playerLG.get(savage_name).isState(State.LIVING) && !main.playerLG.get(savage_name).isCamp(Camp.LG)) {
				main.role_manage.newLG(savage_name);
			}	
		}
	}

	public void transportation(String playername, double d, String message) {

		if(Bukkit.getPlayer(playername)!=null) {

			Player player = Bukkit.getPlayer(playername);
			World world = player.getWorld();
			WorldBorder wb = world.getWorldBorder();
			double a = d*2*Math.PI/Bukkit.getOnlinePlayers().size();
			int x = (int) (Math.round(wb.getSize()/3*Math.cos(a)+world.getSpawnLocation().getX()));
			int z = (int) (Math.round(wb.getSize()/3*Math.sin(a)+world.getSpawnLocation().getZ()));
			player.setFoodLevel(20);
			player.setSaturation(20);
			player.setGameMode(GameMode.SURVIVAL);
			player.sendMessage(message);
			player.removePotionEffect(PotionEffectType.WITHER);
			player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400,-1,false,false));
			player.teleport(new Location(world,x,world.getHighestBlockYAt(x,z)+100,z));
		}
	}


}

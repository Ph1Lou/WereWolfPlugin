package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Random;


public class DeathManagementLG {
	
	MainLG main;
	
	public DeathManagementLG(MainLG main) {
		this.main=main;
	}
	
	public void deathstep1(String killername,String playername) {
		
		PlayerLG plg = main.playerlg.get(playername);
		
		
		plg.setKiller(killername);
		plg.setDeathTime(main.score.getTimer());
		plg.setState(State.JUGEMENT);
		
		if(Bukkit.getPlayer(playername)!=null) {
			
			Player player = Bukkit.getPlayer(playername);
			player.spigot().respawn();
		}
		
		if(main.playerlg.containsKey(killername)) {
			
			PlayerLG klg = main.playerlg.get(killername);
			
			main.playerlg.get(killername).addOneKill();
			
			if(Bukkit.getPlayer(killername)!=null) {
				
				Player killer = Bukkit.getPlayer(killername);
				
				
				if(!klg.isCamp(Camp.VILLAGE)){
	
					killer.removePotionEffect(PotionEffectType.ABSORPTION);
					killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200,0,false,false));
					killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200,0,false,false));	
				}	
				if(klg.isRole(RoleLG.VOLEUR) && klg.hasPower()) {
					plg.setStolen(true);
					main.playerlg.get(killername).setPower(false);
					return;
				}
			}	
		}
		deathstep2(killername,playername);
	}

	private void deathstep2(String killername,String playername) {
		
		PlayerLG plg = main.playerlg.get(playername);
		
		if (plg.isRole(RoleLG.ANCIEN) && plg.hasPower()){
				
			plg.setPower(false);

			if (Bukkit.getPlayer(playername)!=null) {
				
				Player player=Bukkit.getPlayer(playername);
				
				if(main.playerlg.containsKey(killername) && main.playerlg.get(killername).isCamp(Camp.VILLAGE)) {
					player.setMaxHealth(player.getHealth()-6);
				}
				player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			}
			resurrection(playername);
		}
		else if(main.playerlg.containsKey(killername) && (main.playerlg.get(killername).isCamp(Camp.LG) || main.playerlg.get(killername).isRole(RoleLG.LOUP_GAROU_BLANC))) {
			
			plg.setcanBeInfect(true);
			
			if(main.config.tool_switch.get(ToolLG.autorezinfect) && plg.isRole(RoleLG.INFECT) && plg.hasPower()) {
				plg.setPower(false);
				resurrection(playername);
				return;
			}
			for(String infectname:main.playerlg.keySet()) {
				
				if(main.playerlg.get(infectname).isState(State.VIVANT) && main.playerlg.get(infectname).isRole(RoleLG.INFECT) && !infectname.equals(playername) && main.playerlg.get(infectname).hasPower() && Bukkit.getPlayer(infectname)!=null) {
					TextComponent msginfect = new TextComponent(main.texte.esthetique("§m", "§6",playername+main.texte.poweruse.get(RoleLG.INFECT)));
					msginfect.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/lg infecter "+playername));
					Bukkit.getPlayer(infectname).spigot().sendMessage(msginfect);		
				}
			}					
		}
		else {
			plg.setDeathTime(main.score.getTimer()-7);
			deathstep3(playername);	
		}
	}
	
	private void deathstep3(String playername) {
		
		if(main.config.tool_switch.get(ToolLG.autorezsorciere) && main.playerlg.get(playername).isRole(RoleLG.SORCIERE) && main.playerlg.get(playername).hasPower()) {
			main.playerlg.get(playername).setPower(false);
			resurrection(playername);
			return;
		}
		
		for(String sorcierename:main.playerlg.keySet()) {
			
			if(main.playerlg.get(sorcierename).isState(State.VIVANT) && main.playerlg.get(sorcierename).isRole(RoleLG.SORCIERE) && !sorcierename.equals(playername) && main.playerlg.get(sorcierename).hasPower() && Bukkit.getPlayer(sorcierename)!=null ) {
				TextComponent msgsrc = new TextComponent(main.texte.esthetique("§m", "§6",playername+main.texte.poweruse.get(RoleLG.SORCIERE)));
				msgsrc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/lg sauver "+playername));
				Bukkit.getPlayer(sorcierename).spigot().sendMessage(msgsrc);		
			}
		}
	}
	
	public void deathtimer() {
		
		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			
			if(plg.isState(State.JUGEMENT)) {

				int diftimer = main.score.getTimer() - plg.getDeathTime();
				
				if (diftimer>7 && plg.canBeInfect()) {
					plg.setcanBeInfect(false);
					deathstep3(playername);
				}
				else if (diftimer>14) {
					
					if(plg.hasBeenStolen() ) {
						
						if(main.playerlg.get(plg.getKiller()).isState(State.VIVANT)) {
							main.rolemanage.thief_recover_role(plg.getKiller(),playername);
						}
						else {
							plg.setDeathTime(main.score.getTimer());
							String killername = plg.getKiller();
							plg.setStolen(false);
							deathstep2(killername,playername);
						}
					}
					else mortdefinitive(playername);
				}
			}
		}			
	}
	

	public void mortdefinitive(String playername) {
		
		World world = Bukkit.getWorld("world");
		PlayerLG plg = main.playerlg.get(playername);
		RoleLG role = plg.getRole();
		if(plg.isVoleur()) {
			role=RoleLG.VOLEUR;
		}
		else if((plg.isRole(RoleLG.ANGE_GARDIEN) || plg.isRole(RoleLG.ANGE_DECHU)) && !plg.hasPower()) {
			role=RoleLG.ANGE;
		}
		main.config.role_count.put(role,main.config.role_count.get(role)-1);
		plg.setState(State.MORT);
		main.score.removePlayerSize();
		
		
		if(main.config.tool_switch.get(ToolLG.show_role_to_death)) {
			Bukkit.broadcastMessage(main.texte.esthetique("§m", "§4",playername+main.texte.getText(28)+main.texte.translaterole.get(role)));
		}
		else Bukkit.broadcastMessage(main.texte.esthetique("§m", "§4",playername+main.texte.getText(29)));
	

		
		for(ItemStack i:plg.getItemDeath()) {
			if(i!=null) {
				world.dropItem(plg.getSpawn(),i);
			}		
		}
		
		for(Player p:Bukkit.getOnlinePlayers()) {
			
			if(main.config.tool_switch.get(ToolLG.compass_target_last_death)) {
				if(plg.getSpawn()!=null) {
					p.setCompassTarget(plg.getSpawn());
				}
			}
			p.playSound(p.getLocation(),Sound.AMBIENCE_THUNDER, 1, 20);
			if(p.getName().equals(playername)) {
				p.setGameMode(GameMode.SPECTATOR);

				TextComponent msgbug = new TextComponent(main.texte.getText(186));
				msgbug.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://discord.gg/GXXCVUA"));
				p.spigot().sendMessage(msgbug);
				
			}
		
		}	
			
	
		if(main.playerlg.containsKey(plg.getKiller()) && plg.isCamp(Camp.VILLAGE) && main.playerlg.get(plg.getKiller()).isRole(RoleLG.LOUP_AMNESIQUE)  && main.playerlg.get(plg.getKiller()).hasPower()){
			main.rolemanage.newLG(plg.getKiller());
			main.playerlg.get(plg.getKiller()).setPower(false);
		}
		
		if (role.equals(RoleLG.TRUBLION)) {
			trubliondeath();
		}
		if (!plg.getCibleOf().isEmpty()) {
			cibledeath(playername);
		}
		if(!plg.getDisciple().isEmpty()) {
			masterdeath(playername);
		}

		if(role.equals(RoleLG.SOEUR)) {
			soeurdeath(playername) ;
		}
		if(!plg.getCouple().isEmpty()) {
			check_couple(playername);
		}
		
		if(!main.isState(StateLG.FIN)) {
			main.check_victory();
		}
		
		
		if(main.config.tool_switch.get(ToolLG.event_voyante_death) && (role.equals(RoleLG.VOYANTE) || role.equals(RoleLG.VOYANTE_BAVARDE))) {
			main.eventslg.event1();
			main.config.tool_switch.put(ToolLG.event_voyante_death,false);
		}
	}
	
	

	private void check_couple(String playername) {
		
		int i=0;
		
		while(i<main.couplerange.size() && !main.couplerange.get(i).contains(playername)) {
			i++;
		}
		
		if(i<main.couplerange.size()) {
			
			main.couplerange.get(i).remove(playername);
			
			while(!main.couplerange.get(i).isEmpty() && main.playerlg.get(main.couplerange.get(i).get(0)).isState(State.MORT)) {
				main.couplerange.get(i).remove(0);
			}
			
			if(!main.couplerange.get(i).isEmpty()) {
				String c1=main.couplerange.get(i).get(0);
				PlayerLG plc1 = main.playerlg.get(c1);
				Bukkit.broadcastMessage(main.texte.esthetique("§m", "§5",c1+main.texte.getText(30)));
				if(Bukkit.getPlayer(c1)!=null) {
					Player player = Bukkit.getPlayer(c1);
					plc1.setSpawn(player.getLocation());
					plc1.clearItemDeath();
					plc1.setItemDeath(player.getInventory().getContents());
					plc1.addItemDeath(player.getInventory().getHelmet());
					plc1.addItemDeath(player.getInventory().getChestplate());
					plc1.addItemDeath(player.getInventory().getBoots());
					plc1.addItemDeath(player.getInventory().getLeggings());
					player.setGameMode(GameMode.SPECTATOR);
				}
				main.playerlg.get(playername).setKiller("§dLove");
				mortdefinitive(c1);
			}
			else {
				main.couplerange.remove(i);
				main.config.role_count.put(RoleLG.COUPLE,main.config.role_count.get(RoleLG.COUPLE)-1);
			}
		}		
	}

	public void resurrection(String playername) {
		
		if(Bukkit.getPlayer(playername)!=null) {
			Player player = Bukkit.getPlayer(playername);
			for(PotionEffectType p:main.rolemanage.effect_recover(playername)) {
				player.addPotionEffect(new PotionEffect(p,Integer.MAX_VALUE,0,false,false));
			}
			player.setHealth(player.getMaxHealth());
		}	
		main.eparpillement(playername, new Random(System.currentTimeMillis()).nextFloat()*Bukkit.getOnlinePlayers().size(), main.texte.getText(31));
		main.playerlg.get(playername).setState(State.VIVANT);
		if(!main.isState(StateLG.FIN)) {
			main.check_victory();
		}
	}
	
	private void trubliondeath() {
		int i=0;
		for (String p:main.playerlg.keySet()) {
			if(main.playerlg.get(p).isState(State.VIVANT)) {
				main.eparpillement(p, i,main.texte.getText(32));
				i++;
			}
		}
	}
	
	private void soeurdeath(String playername) {
		
		for(String soeurname:main.playerlg.keySet()) {
			if(main.playerlg.get(soeurname).isState(State.VIVANT) && main.playerlg.get(soeurname).isRole(RoleLG.SOEUR) && Bukkit.getPlayer(soeurname)!=null) {
				Bukkit.getPlayer(soeurname).sendMessage(main.texte.esthetique("§m", "§e",playername+main.texte.getText(33)+main.playerlg.get(playername).getKiller()));
			}
		}
	}
	
	private void cibledeath(String playername) {
		
		for(String angename:main.playerlg.get(playername).getCibleOf()) {
			
			if(main.playerlg.get(angename).isState(State.VIVANT) && Bukkit.getPlayer(angename) != null) {
				
				Player ange = Bukkit.getPlayer(angename);
				
				if(main.playerlg.get(angename).isRole(RoleLG.ANGE_DECHU)) {
					if(main.playerlg.get(playername).getKiller().equals(angename)) {
						ange.setMaxHealth(ange.getMaxHealth()+6);
						ange.sendMessage(main.texte.getText(34));
					}	
				}
				else {
					ange.setMaxHealth(ange.getMaxHealth()-4);
					ange.sendMessage(main.texte.getText(35));
				}
			}	
		}
		
	}
	
	private void masterdeath(String playername) {
		
		for(String sauvagename:main.playerlg.get(playername).getDisciple()) {
			
			if(main.playerlg.get(sauvagename).isState(State.VIVANT) && !main.playerlg.get(sauvagename).isCamp(Camp.LG)) {
				main.rolemanage.newLG(sauvagename);
			}	
		}
	}
	
}

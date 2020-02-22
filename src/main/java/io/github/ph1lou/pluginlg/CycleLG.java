package io.github.ph1lou.pluginlg;

import java.util.ArrayList;
import java.util.List;

import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class CycleLG {
	
	MainLG main;
	
	public CycleLG(MainLG main) {
		this.main=main;	
	}
	
	public void nuit() {
		
		Bukkit.broadcastMessage(main.texte.getText(124));
		
		if(!main.isState(StateLG.LG)) return;
			
		main.score.groupsizechange() ;
		
		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			
			if(plg.isState(State.VIVANT) && Bukkit.getPlayer(playername)!=null){
				
				Player player = Bukkit.getPlayer(playername);	
				
				player.playSound(player.getLocation(), Sound.DOOR_CLOSE,1,20);
				
				if(plg.isRole(RoleLG.ASSASSIN)){
					player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);	
				}
				if(plg.isCamp(Camp.LG)){
					player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
				}
				if(plg.isRole(RoleLG.PETITE_FILLE)){
					player.sendMessage(main.texte.getText(14));
				}
				if(plg.isRole(RoleLG.LOUP_PERFIDE)){
					player.sendMessage(main.texte.getText(14));
				}
				if(plg.isRole(RoleLG.LOUP_GAROU_BLANC)){
					player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
				}
			}	
		}
	}
	
	public void finselection() {
		
		if(!main.isState(StateLG.LG)) return;
		
		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			
			if(plg.isState(State.VIVANT) && Bukkit.getPlayer(playername)!=null){
				
				Player player = Bukkit.getPlayer(playername);
				
				if((plg.isRole(RoleLG.CORBEAU) || plg.isRole(RoleLG.SALVATEUR) || plg.isRole(RoleLG.DETECTIVE) || plg.isRole(RoleLG.VOYANTE) || plg.isRole(RoleLG.VOYANTE_BAVARDE)) && plg.hasPower()) {
					plg.setPower(false);
					player.sendMessage(main.texte.getText(13));
				}
				if(plg.isRole(RoleLG.LOUP_FEUTRE)) {
					List <String> playerv = new ArrayList<>();
					for(String p:main.playerlg.keySet()) {
						if(main.playerlg.get(p).isState(State.VIVANT) && !p.equals(playername)) {
							playerv.add(p);
						}
					}
					String pc = playerv.get((int) Math.floor(Math.random()*playerv.size()));
					plg.setCampFeutre(main.playerlg.get(pc).getCamp());
					plg.setRoleFeutre(main.playerlg.get(pc).getRole());
					player.sendMessage(main.texte.esthetique("§m", "§e",main.texte.getText(15)+main.texte.translaterole.get(main.playerlg.get(pc).getRole())));
				}
			}
		}
	}
	public void prejour() {
		
	
		if(!main.isState(StateLG.LG)) return;
		
		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			
			if(plg.isState(State.VIVANT) && Bukkit.getPlayer(playername)!=null){
				
				Player player = Bukkit.getPlayer(playername);	
				
				player.playSound(player.getLocation(), Sound.DOOR_OPEN,1,20);
				
				if(plg.isRole(RoleLG.LOUP_PERFIDE) || plg.isRole(RoleLG.PETITE_FILLE)){
					player.sendMessage(main.texte.getText(197));
				}
			}
		}
	}
				
				
				
				
	public void jour() {
		
	
		Bukkit.broadcastMessage(main.texte.getText(16));
		
		if(!main.isState(StateLG.LG)) return;
		
		if(main.config.tool_switch.get(ToolLG.vote) && main.score.getTimer()>=main.config.value.get(TimerLG.vote_begin)) {
			Bukkit.broadcastMessage(main.texte.esthetique("§m", "§6",main.texte.getText(17)+main.conversion(main.config.value.get(TimerLG.vote_duration))));

		}
		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			
			if(plg.isState(State.VIVANT) && Bukkit.getPlayer(playername)!=null){
				
				Player player = Bukkit.getPlayer(playername);	
				
				player.playSound(player.getLocation(), Sound.DOOR_OPEN,1,20);
				
				if(plg.isRole(RoleLG.MONTREUR_OURS)){
					
					StringBuilder builder=new StringBuilder();
					Boolean ok=false;
					
					Location ourslocation = Bukkit.getPlayer(playername).getLocation();	
					
					for(Player pls:Bukkit.getOnlinePlayers()) {
						
						if(main.playerlg.containsKey(pls.getName())) {
							
							PlayerLG plgf = main.playerlg.get(pls.getName());
							
							if(plgf.isRole(RoleLG.LOUP_FEUTRE) && !plgf.isCampFeutre(Camp.LG)) {
								
							}
							else if((plgf.isCamp(Camp.LG) || plgf.isRole(RoleLG.LOUP_GAROU_BLANC)) && plgf.isState(State.VIVANT)) {
								if(ourslocation.distance(pls.getLocation())<50) {
									builder.append("Grrrr ");
									ok=true;
								}
							}
						}
						
					}
					if(ok) {
						Bukkit.broadcastMessage(main.texte.esthetique("§m", "§6", builder.toString()));
						for(Player pls:Bukkit.getOnlinePlayers()) {
							pls.playSound(pls.getLocation(),Sound.WOLF_GROWL, 1, 20);
						}
					}
				}
				if(plg.getLostHeart()>0){
					player.setMaxHealth(player.getMaxHealth()+plg.getLostHeart());
					plg.clearLostHeart();
				}
				if(plg.isCamp(Camp.LG)){
					player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				}
				if(plg.isRole(RoleLG.LOUP_GAROU_BLANC)){
					player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);	
				}
				if(plg.isRole(RoleLG.ASSASSIN)){
					player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
				}
				if((plg.isRole(RoleLG.LOUP_PERFIDE) || plg.isRole(RoleLG.PETITE_FILLE)) && !plg.hasPower()) {
					player.removePotionEffect(PotionEffectType.INVISIBILITY);
					player.removePotionEffect(PotionEffectType.WEAKNESS);
					plg.setPower(true);
					player.sendMessage(main.texte.getText(18));
				}
				if(plg.hasMaudit()) {
					plg.setMaudit(false);
					player.removePotionEffect(PotionEffectType.JUMP);
					player.sendMessage(main.texte.getText(19));
				}
				if(plg.hasSalvation()) {
					plg.setSalvation(false);
					if (!((plg.isRole(RoleLG.ANCIEN) || plg.isRole(RoleLG.VOLEUR) ) && plg.hasPower())) {	
						player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					}
					player.sendMessage(main.texte.getText(20));	
				}
				if(plg.isRole(RoleLG.CORBEAU)) {
					plg.setPower(true);
					player.sendMessage(main.texte.esthetique("§2§m", "§e",main.texte.poweruse.get(RoleLG.CORBEAU)+main.conversion(main.config.value.get(TimerLG.use_power))));
				}
				if(plg.isRole(RoleLG.SALVATEUR)) {
					plg.setPower(true);
					player.sendMessage(main.texte.esthetique("§2§m", "§e",main.texte.poweruse.get(RoleLG.SALVATEUR)+main.conversion(main.config.value.get(TimerLG.use_power))));
				}
				if(plg.isRole(RoleLG.DETECTIVE)) {
					plg.setPower(true);
					player.sendMessage(main.texte.esthetique("§2§m", "§e",main.texte.poweruse.get(RoleLG.DETECTIVE)+main.conversion(main.config.value.get(TimerLG.use_power))));
				}
				if(plg.isRole(RoleLG.RENARD)) {
					plg.setPower(true);
					player.sendMessage(main.texte.poweruse.get(RoleLG.RENARD));
				}
				if(plg.isRole(RoleLG.VOYANTE) || plg.isRole(RoleLG.VOYANTE_BAVARDE)) {
					plg.setPower(true);
					player.sendMessage(main.texte.esthetique("§2§m", "§e",main.texte.poweruse.get(RoleLG.VOYANTE)+main.conversion(main.config.value.get(TimerLG.use_power))));
				}
			}
		}	
	}

}

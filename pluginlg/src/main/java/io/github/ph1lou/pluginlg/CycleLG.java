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
	
	final MainLG main;
	
	public CycleLG(MainLG main) {
		this.main=main;	
	}
	
	public void nuit() {
		
		Bukkit.broadcastMessage(main.text.getText(124));
		
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
					player.sendMessage(main.text.getText(14));
				}
				if(plg.isRole(RoleLG.LOUP_PERFIDE)){
					player.sendMessage(main.text.getText(14));
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
					player.sendMessage(main.text.getText(13));
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
					player.sendMessage(main.text.esthetique("§m", "§e",main.text.getText(15)+main.text.translaterole.get(main.playerlg.get(pc).getRole())));
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
				
				if(plg.isRole(RoleLG.LOUP_PERFIDE) || plg.isRole(RoleLG.PETITE_FILLE)){
					player.sendMessage(main.text.getText(197));
				}
			}
		}
	}

	public void prevoteresult() {


		if(!main.isState(StateLG.LG)) return;

		for(String playername:main.playerlg.keySet()) {

			PlayerLG plg = main.playerlg.get(playername);

			if(plg.isState(State.VIVANT) && Bukkit.getPlayer(playername)!=null){

				Player player = Bukkit.getPlayer(playername);

				Title.removeBar(player);

				if(plg.isRole(RoleLG.CITOYEN)){
					player.sendMessage(main.text.esthetique("§m", "§6",RoleLG.CITOYEN.getPowerUse()+main.conversion(main.config.value.get(TimerLG.CITIZEN_DURATION))));
				}
			}
		}
	}
				
				
	public void jour() {
		
	
		Bukkit.broadcastMessage(main.text.getText(16));
		
		if(!main.isState(StateLG.LG)) return;
		

		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			
			if(plg.isState(State.VIVANT) && Bukkit.getPlayer(playername)!=null){
				
				Player player = Bukkit.getPlayer(playername);	
				
				player.playSound(player.getLocation(), Sound.DOOR_OPEN,1,20);

				if(main.config.tool_switch.get(ToolLG.VOTE) && main.score.getTimer()>=main.config.value.get(TimerLG.VOTE_BEGIN)) {
					player.sendMessage(main.text.esthetique("§m", "§6",main.text.getText(17)+main.conversion(main.config.value.get(TimerLG.VOTE_DURATION))));
					Title.removeBar(player);
					Title.sendBar(player, "Vote", 100);
				}
				
				if(plg.isRole(RoleLG.MONTREUR_OURS)){
					
					StringBuilder builder=new StringBuilder();
					boolean ok=false;
					
					Location ourslocation = Bukkit.getPlayer(playername).getLocation();	
					
					for(Player pls:Bukkit.getOnlinePlayers()) {
						
						if(main.playerlg.containsKey(pls.getName())) {
							
							PlayerLG plgf = main.playerlg.get(pls.getName());

							if (!plgf.isRole(RoleLG.LOUP_FEUTRE) || plgf.isCampFeutre(Camp.LG)) {
								if((plgf.isCamp(Camp.LG) || plgf.isRole(RoleLG.LOUP_GAROU_BLANC)) && plgf.isState(State.VIVANT)) {
									if(ourslocation.distance(pls.getLocation())<50) {
										builder.append("Grrrr ");
										ok=true;
									}
								}
							}
						}
						
					}
					if(ok) {
						Bukkit.broadcastMessage(main.text.esthetique("§m", "§6", builder.toString()));
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
					player.sendMessage(main.text.getText(18));
					main.optionlg.updateScenario();
				}
				if(plg.hasMaudit()) {
					plg.setMaudit(false);
					player.removePotionEffect(PotionEffectType.JUMP);
					player.sendMessage(main.text.getText(19));
				}
				if(plg.hasSalvation()) {
					plg.setSalvation(false);
					if (!((plg.isRole(RoleLG.ANCIEN) || plg.isRole(RoleLG.VOLEUR) ) && plg.hasPower())) {	
						player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					}
					player.sendMessage(main.text.getText(20));
				}
				if(plg.isRole(RoleLG.CORBEAU)) {
					plg.setPower(true);
					player.sendMessage(main.text.esthetique("§2§m", "§e",main.text.poweruse.get(RoleLG.CORBEAU)+main.conversion(main.config.value.get(TimerLG.POWER_DURATION))));
				}
				if(plg.isRole(RoleLG.SALVATEUR)) {
					plg.setPower(true);
					player.sendMessage(main.text.esthetique("§2§m", "§e",main.text.poweruse.get(RoleLG.SALVATEUR)+main.conversion(main.config.value.get(TimerLG.POWER_DURATION))));
				}
				if(plg.isRole(RoleLG.DETECTIVE)) {
					plg.setPower(true);
					player.sendMessage(main.text.esthetique("§2§m", "§e",main.text.poweruse.get(RoleLG.DETECTIVE)+main.conversion(main.config.value.get(TimerLG.POWER_DURATION))));
				}
				if(plg.isRole(RoleLG.RENARD)) {
					plg.setPower(true);
					player.sendMessage(main.text.poweruse.get(RoleLG.RENARD));
				}
				if(plg.isRole(RoleLG.VOYANTE) || plg.isRole(RoleLG.VOYANTE_BAVARDE)) {
					plg.setPower(true);
					player.sendMessage(main.text.esthetique("§2§m", "§e",main.text.poweruse.get(RoleLG.VOYANTE)+main.conversion(main.config.value.get(TimerLG.POWER_DURATION))));
				}
			}
		}	
	}

}

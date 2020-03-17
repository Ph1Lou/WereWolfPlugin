package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class CycleLG {
	
	final MainLG main;
	
	public CycleLG(MainLG main) {
		this.main=main;	
	}
	
	public void night() {
		
		Bukkit.broadcastMessage(main.text.getText(124));
		
		if(!main.isState(StateLG.LG)) return;
			
		main.score.groupSizeChange() ;
		
		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			
			if(plg.isState(State.LIVING) && Bukkit.getPlayer(playername)!=null){
				
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
	
	public void selectionEnd() {
		
		if(!main.isState(StateLG.LG)) return;
		
		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			
			if(plg.isState(State.LIVING) && Bukkit.getPlayer(playername)!=null){
				
				Player player = Bukkit.getPlayer(playername);
				
				if((plg.isRole(RoleLG.CORBEAU) || plg.isRole(RoleLG.SALVATEUR) || plg.isRole(RoleLG.DETECTIVE) || plg.isRole(RoleLG.VOYANTE) || plg.isRole(RoleLG.VOYANTE_BAVARDE)) && plg.hasPower()) {
					plg.setPower(false);
					player.sendMessage(main.text.getText(13));
				}
				if(plg.isRole(RoleLG.LOUP_FEUTRE)) {
					List <String> players = new ArrayList<>();
					for(String p:main.playerlg.keySet()) {
						if(main.playerlg.get(p).isState(State.LIVING) && !p.equals(playername)) {
							players.add(p);
						}
					}
					String pc = players.get((int) Math.floor(Math.random()*players.size()));
					plg.setPosterCamp(main.playerlg.get(pc).getCamp());
					plg.setPosterRole(main.playerlg.get(pc).getRole());
					player.sendMessage(String.format(main.text.poweruse.get(RoleLG.LOUP_FEUTRE),main.text.translaterole.get(main.playerlg.get(pc).getRole())));
				}
			}
		}
	}
	public void preDay() {

		if(!main.isState(StateLG.LG)) return;
		
		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			
			if(plg.isState(State.LIVING) && Bukkit.getPlayer(playername)!=null){
				
				Player player = Bukkit.getPlayer(playername);
				
				if(plg.isRole(RoleLG.LOUP_PERFIDE) || plg.isRole(RoleLG.PETITE_FILLE)){
					player.sendMessage(main.text.getText(197));
				}
			}
		}
	}

	public void preVoteResult() {


		if(!main.isState(StateLG.LG)) return;

		for(String playername:main.playerlg.keySet()) {

			PlayerLG plg = main.playerlg.get(playername);

			if(plg.isState(State.LIVING) && Bukkit.getPlayer(playername)!=null){

				Player player = Bukkit.getPlayer(playername);

				Title.removeBar(player);

				if(plg.isRole(RoleLG.CITOYEN)){
					player.sendMessage(String.format(main.text.poweruse.get(RoleLG.CITOYEN),main.score.conversion(main.config.value.get(TimerLG.CITIZEN_DURATION))));
				}
			}
		}
	}
				
				
	public void day() {
		
	
		Bukkit.broadcastMessage(main.text.getText(16));
		
		if(!main.isState(StateLG.LG)) return;

		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			
			if(plg.isState(State.LIVING) && Bukkit.getPlayer(playername)!=null){
				
				Player player = Bukkit.getPlayer(playername);	
				
				player.playSound(player.getLocation(), Sound.DOOR_OPEN,1,20);

				if(main.config.tool_switch.get(ToolLG.VOTE) && main.config.value.get(TimerLG.VOTE_BEGIN)<0) {
					player.sendMessage(String.format(main.text.getText(17),main.score.conversion(main.config.value.get(TimerLG.VOTE_DURATION))));
				}
				
				if(plg.isRole(RoleLG.MONTREUR_OURS)){
					
					StringBuilder builder=new StringBuilder();
					boolean ok=false;
					
					Location oursLocation = Bukkit.getPlayer(playername).getLocation();
					
					for(Player pls:Bukkit.getOnlinePlayers()) {
						
						if(main.playerlg.containsKey(pls.getName())) {
							
							PlayerLG plo = main.playerlg.get(pls.getName());

							if (!plo.isRole(RoleLG.LOUP_FEUTRE) || plo.isPosterCamp(Camp.LG)) {
								if((plo.isCamp(Camp.LG) || plo.isRole(RoleLG.LOUP_GAROU_BLANC)) && plo.isState(State.LIVING)) {
									if(oursLocation.distance(pls.getLocation())<50) {
										builder.append(main.text.powerhasbeenuse.get(RoleLG.MONTREUR_OURS));
										ok=true;
									}
								}
							}
						}
						
					}
					if(ok) {
						Bukkit.broadcastMessage(String.format(main.text.poweruse.get(RoleLG.MONTREUR_OURS), builder.toString()));
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
					main.optionlg.updateNameTag();
				}
				if(plg.hasDamn()) {
					plg.setDamn(false);
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
					player.sendMessage(String.format(main.text.poweruse.get(RoleLG.CORBEAU),main.score.conversion(main.config.value.get(TimerLG.POWER_DURATION))));
				}
				if(plg.isRole(RoleLG.SALVATEUR)) {
					plg.setPower(true);
					player.sendMessage(String.format(main.text.poweruse.get(RoleLG.SALVATEUR),main.score.conversion(main.config.value.get(TimerLG.POWER_DURATION))));
				}
				if(plg.isRole(RoleLG.DETECTIVE)) {
					plg.setPower(true);
					player.sendMessage(String.format(main.text.poweruse.get(RoleLG.DETECTIVE),main.score.conversion(main.config.value.get(TimerLG.POWER_DURATION))));
				}
				if(plg.isRole(RoleLG.RENARD)) {
					plg.setPower(true);
					player.sendMessage(main.text.poweruse.get(RoleLG.RENARD));
				}
				if(plg.isRole(RoleLG.VOYANTE) || plg.isRole(RoleLG.VOYANTE_BAVARDE)) {
					plg.setPower(true);
					player.sendMessage(String.format(main.text.poweruse.get(RoleLG.VOYANTE),main.score.conversion(main.config.value.get(TimerLG.POWER_DURATION))));
				}
			}
		}	
	}

}

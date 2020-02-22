package io.github.ph1lou.pluginlg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.ph1lou.pluginlg.enumlg.Camp;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;




public class ProximityLG {
	
	private MainLG main;
	public ProximityLG(MainLG main) {
		
		this.main=main;	
	
	}
	
	public void sister_proximity() {
		
		Map<String,Location> locsisters = new HashMap<>();
		List<String> soeurs = new ArrayList<>();
		
		for(String soeurname:main.playerlg.keySet()) {
			if(main.playerlg.get(soeurname).isRole(RoleLG.SOEUR) && main.playerlg.get(soeurname).isState(State.VIVANT) && Bukkit.getPlayer(soeurname) != null){
				Player soeur = Bukkit.getPlayer(soeurname);
				Location loc= soeur.getLocation();
				locsisters.put(soeurname,loc);
				soeurs.add(soeurname);
			}
		}
		
		for(int i=0;i<soeurs.size()-1;i++) {
			for(int j=i+1;j<soeurs.size();j++) {
				if (Bukkit.getPlayer(soeurs.get(i)) != null && Bukkit.getPlayer(soeurs.get(j)) != null && locsisters.get(soeurs.get(i)).distance(locsisters.get(soeurs.get(j)))<=20) {
					Player soeur1 = Bukkit.getPlayer(soeurs.get(i));
					Player soeur2 = Bukkit.getPlayer(soeurs.get(j));
					soeur1.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,100,0,false,false));
					soeur2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,100,0,false,false));
				}
			}
		}
	}

	public void renard_proximity() {
		
		for(String playername:main.playerlg.keySet()) {
			
			
			if(main.playerlg.get(playername).isState(State.VIVANT) && main.playerlg.get(playername).isRole(RoleLG.RENARD) && !main.playerlg.get(playername).getAffectedPlayer().isEmpty()) {
				String playerflairer = main.playerlg.get(playername).getAffectedPlayer().get(0);
				
				if(Bukkit.getPlayer(playerflairer)!=null && Bukkit.getPlayer(playername)!=null) { 
					
					Player pflair =Bukkit.getPlayer(playerflairer);
					Player player =Bukkit.getPlayer(playername);
					
					Location renardlocation = player.getLocation();
					Location pflairlocation = pflair.getLocation();
						
					if(renardlocation.distance(pflairlocation)<=20) {
						
						float temp=main.playerlg.get(playername).getFlair()+100f/(main.config.value.get(TimerLG.flair_renard)+1);
	
						main.playerlg.get(playername).setFlair(temp);
						
						
						if(temp%10>0 && temp%10<=100f/(main.config.value.get(TimerLG.flair_renard)+1)) {
							player.sendMessage(main.texte.esthetique("§m", "§e",main.texte.getText(39)+Math.floor(temp)+"%"));
						}
						
						if(temp>=100) {
							if(main.playerlg.get(playerflairer).isRole(RoleLG.LOUP_FEUTRE) && !main.playerlg.get(playerflairer).isCampFeutre(Camp.LG)) {
								player.sendMessage(main.texte.esthetique("§m", "§e",playerflairer + main.texte.getText(40)));
							}
							else if (main.playerlg.get(playerflairer).isCamp(Camp.LG)) {
							player.sendMessage(main.texte.esthetique("§m", "§e",playerflairer + main.texte.getText(41)));
							}
							else {
							player.sendMessage(main.texte.esthetique("§m", "§e",playerflairer + main.texte.getText(40)));
							}
							main.playerlg.get(playername).clearAffectedPlayer();
							main.playerlg.get(playername).setFlair(0f);
						}			
					}
					
					
				}
			}
									
		}
	}	
}

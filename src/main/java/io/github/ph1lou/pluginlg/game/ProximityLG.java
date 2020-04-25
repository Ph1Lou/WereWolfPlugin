package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.enumlg.Camp;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class ProximityLG {
	
	private final GameManager game;
	
	public ProximityLG(GameManager game) {
		
		this.game=game;	
	
	}
	
	public void sister_proximity() {
		
		Map<String,Location> sisters_location = new HashMap<>();
		List<String> sisters = new ArrayList<>();
		
		for(String sister_name:game.playerLG.keySet()) {
			if(game.playerLG.get(sister_name).isRole(RoleLG.SOEUR) && game.playerLG.get(sister_name).isState(State.LIVING) && Bukkit.getPlayer(sister_name) != null){
				Player sister = Bukkit.getPlayer(sister_name);
				Location loc= sister.getLocation();
				sisters_location.put(sister_name,loc);
				sisters.add(sister_name);
			}
		}
		
		for(int i=0;i<sisters.size()-1;i++) {
			for(int j=i+1;j<sisters.size();j++) {
				if (Bukkit.getPlayer(sisters.get(i)) != null && Bukkit.getPlayer(sisters.get(j)) != null && sisters_location.get(sisters.get(i)).distance(sisters_location.get(sisters.get(j)))<=20) {
					Player sister1 = Bukkit.getPlayer(sisters.get(i));
					Player sister2 = Bukkit.getPlayer(sisters.get(j));
					sister1.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,100,0,false,false));
					sister2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,100,0,false,false));
				}
			}
		}
	}

	public void renard_proximity() {
		
		for(String playername:game.playerLG.keySet()) {

			PlayerLG plg = game.playerLG.get(playername);

			if(plg.isState(State.LIVING) && plg.isRole(RoleLG.RENARD) && !plg.getAffectedPlayer().isEmpty()) {

                String playerSmell = plg.getAffectedPlayer().get(0);
                PlayerLG plf = game.playerLG.get(playerSmell);

                if (plf.isState(State.LIVING) && Bukkit.getPlayer(playerSmell) != null && Bukkit.getPlayer(playername) != null) {

                    Player flair = Bukkit.getPlayer(playerSmell);
                    Player player = Bukkit.getPlayer(playername);

                    Location renardLocation = player.getLocation();
                    Location playerLocation = flair.getLocation();

                    if (renardLocation.distance(playerLocation) <= game.config.getDistanceFox()) {

                        float temp = plg.getFlair() + 100f / (game.config.timerValues.get(TimerLG.RENARD_SMELL_DURATION) + 1);

                        plg.setFlair(temp);

                        if (temp % 10 > 0 && temp % 10 <= 100f / (game.config.timerValues.get(TimerLG.RENARD_SMELL_DURATION) + 1)) {
                            player.sendMessage(String.format(game.text.getText(39), Math.floor(temp)));
                        }

                        if (temp >= 100) {

                            if (plf.isRole(RoleLG.LOUP_FEUTRE) && (!plf.isPosterCamp(Camp.LG) && !plf.isPosterRole(RoleLG.LOUP_GAROU_BLANC))) {
                                player.sendMessage(String.format(game.text.getText(40), playerSmell));
                            } else if (plf.isCamp(Camp.LG) || plf.isRole(RoleLG.LOUP_GAROU_BLANC)) {
                                player.sendMessage(String.format(game.text.getText(41), playerSmell));
							}
							else {
							player.sendMessage(String.format(game.text.getText(40),playerSmell));
							}
							plg.clearAffectedPlayer();
							plg.setFlair(0f);
						}			
					}
				}
			}
									
		}
	}	
}

package io.github.ph1lou.pluginlg;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class CoupleManagement {

	private final MainLG main;
	public final List<List<String>> couple_range = new ArrayList<>();

	public CoupleManagement(MainLG main) {
		this.main=main;	
	}
	
	public void auto_couple() {

		Random r =new Random(System.currentTimeMillis());
		List<String> pcouple =new ArrayList<>();
		for(String p:main.playerlg.keySet()) {
			if(main.playerlg.get(p).isState(State.VIVANT)) {
				pcouple.add(p);
			}
		}
		if(main.score.getPlayerSize()<2 && main.config.role_count.get(RoleLG.CUPIDON)+main.config.role_count.get(RoleLG.COUPLE)>0){
			Bukkit.broadcastMessage(main.text.getText(12));
			return;
		}
		
		Boolean polygamie =main.config.tool_switch.get(ToolLG.POLYGAMY);
		
		if(!polygamie && (main.config.role_count.get(RoleLG.COUPLE)==0 && main.config.role_count.get(RoleLG.CUPIDON)*2>=main.score.getPlayerSize()) || (main.config.role_count.get(RoleLG.COUPLE)!=0 && (main.config.role_count.get(RoleLG.CUPIDON)+main.config.role_count.get(RoleLG.COUPLE))*2>main.score.getPlayerSize())) {
			polygamie=true;
			Bukkit.broadcastMessage(main.text.getText(192));
		}
		String j1;
		String j2;
		
		for(String playername:main.playerlg.keySet()) {
			
			if(main.playerlg.get(playername).isRole(RoleLG.CUPIDON)) {
				
				if (main.playerlg.get(playername).hasPower() || !main.playerlg.get(main.playerlg.get(playername).getAffectedPlayer().get(0)).isState(State.VIVANT) || !main.playerlg.get(main.playerlg.get(playername).getAffectedPlayer().get(1)).isState(State.VIVANT)) {
					
					if(pcouple.contains(playername)) {
						pcouple.remove(playername);
						j1 = pcouple.get((int) Math.floor(r.nextFloat()*pcouple.size()));
						pcouple.remove(j1);
						j2 = pcouple.get((int) Math.floor(r.nextFloat()*pcouple.size()));
						pcouple.add(j1);
						pcouple.add(playername);
					}
					else {
						j1 = pcouple.get((int) Math.floor(r.nextFloat()*pcouple.size()));
						pcouple.remove(j1);
						j2 = pcouple.get((int) Math.floor(r.nextFloat()*pcouple.size()));
						pcouple.add(j1);
					}
					
					main.playerlg.get(playername).clearAffectedPlayer();
					main.playerlg.get(playername).addAffectedPlayer(j1);
					main.playerlg.get(playername).addAffectedPlayer(j2);
					main.playerlg.get(playername).setPower(false);	
					if(Bukkit.getPlayer(playername)!=null) {
						Bukkit.getPlayer(playername).sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.CUPIDON),j1,j2));
					}	
				}
				else {
					j1 = main.playerlg.get(playername).getAffectedPlayer().get(0);
					j2 = main.playerlg.get(playername).getAffectedPlayer().get(1);
				}	
				if(!polygamie) {
					pcouple.remove(j1);
					pcouple.remove(j2);
				}
				if(!main.playerlg.get(j1).getCouple().contains(j2)) {
					main.playerlg.get(j1).addCouple(j2);
				}
				
				if(!main.playerlg.get(j2).getCouple().contains(j1)) {
					main.playerlg.get(j2).addCouple(j1);
				}
			}
		}
		for(int i = 0; i< main.config.role_count.get(RoleLG.COUPLE); i++) {
			
			j1 = pcouple.get((int) Math.floor(r.nextFloat()*pcouple.size()));
			pcouple.remove(j1);
			j2 = pcouple.get((int) Math.floor(r.nextFloat()*pcouple.size()));
			pcouple.add(j1);
			
			if(!polygamie) {
				pcouple.remove(j1);
				pcouple.remove(j2);
			}
			if(!main.playerlg.get(j1).getCouple().contains(j2)) {
				main.playerlg.get(j1).addCouple(j2);
			}
			
			if(!main.playerlg.get(j2).getCouple().contains(j1)) {
				main.playerlg.get(j2).addCouple(j1);
			}
		}
		
		
		range_couple();
		announceCouple();
		if(!main.isState(StateLG.FIN)) {
			main.endlg.check_victory();
		}
	}
	
	private void announceCouple() {

		for (List<String> strings : couple_range) {
			for (int j = 0; j < strings.size(); j++) {
				String p = strings.get(j);
				PlayerLG plg = main.playerlg.get(p);
				plg.clearCouple();
				for (String string : strings) {
					if (!string.equals(p)) {
						plg.addCouple(string);
					}
				}
				if (Bukkit.getPlayer(p) != null) {

					Player pj2 = Bukkit.getPlayer(p);

					for (ItemStack k : main.stufflg.role_stuff.get(RoleLG.COUPLE)) {

						if (pj2.getInventory().firstEmpty() == -1) {
							pj2.getWorld().dropItem(pj2.getLocation(), k);
						} else {
							pj2.getInventory().addItem(k);
							pj2.updateInventory();
						}
					}

					StringBuilder strb = new StringBuilder();

					for (String c : plg.getCouple()) {
						strb.append(c).append(" ");
					}
					pj2.sendMessage(String.format(main.text.description.get(RoleLG.COUPLE),strb.toString()));
					pj2.playSound(pj2.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
				}
			}
		}
		
		
	}
	
	private void range_couple() {
		
		List<String> allcouple = new ArrayList<>();
		main.config.role_count.put(RoleLG.COUPLE,0);
		
		for(String playername:main.playerlg.keySet()) {
			if(!main.playerlg.get(playername).getCouple().isEmpty()){
				allcouple.add(playername);
			}
		}
		
		while(!allcouple.isEmpty()) {
			
			List<String> couplelie= new ArrayList<>();
			couplelie.add(allcouple.get(0));
			allcouple.remove(0);
			
			for(int j=0;j<couplelie.size();j++) {
				for(String playername:main.playerlg.keySet()) {
					if(main.playerlg.get(playername).getCouple().contains(couplelie.get(j))) {
						if(!couplelie.contains(playername)) {
							couplelie.add(playername);
							allcouple.remove(playername);
						}
					}		
				}
			}
			couple_range.add(couplelie);
			main.config.role_count.put(RoleLG.COUPLE,main.config.role_count.get(RoleLG.COUPLE)+1);
		}		
	}



	public void thief_couplerange(String killername, String playername) {
		
		int cp=-1; 
		int ck=-1;
		for(int i = 0; i< couple_range.size(); i++) {
			if(couple_range.get(i).contains(playername) && !couple_range.get(i).contains(killername)) {
				couple_range.get(i).remove(playername);
				couple_range.get(i).add(killername);
				cp=i;
			}
			else if(!couple_range.get(i).contains(playername) && couple_range.get(i).contains(killername)) {
				ck=i;
			}
		}
		if(cp!=-1&&ck!=-1) {
			couple_range.get(ck).remove(killername);
			couple_range.get(cp).addAll(couple_range.get(ck));
			couple_range.remove(ck);
		}
				
	}
}

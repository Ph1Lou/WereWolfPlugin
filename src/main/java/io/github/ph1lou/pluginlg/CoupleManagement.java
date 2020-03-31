package io.github.ph1lou.pluginlg;


import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CoupleManagement {

	private final MainLG main;
	public final List<List<String>> couple_range = new ArrayList<>();

	public CoupleManagement(MainLG main) {
		this.main=main;	
	}
	
	public void auto_couple() {

		Random r =new Random(System.currentTimeMillis());
		List<String> couples =new ArrayList<>();
		for(String p:main.playerLG.keySet()) {
			if(main.playerLG.get(p).isState(State.LIVING)) {
				couples.add(p);
			}
		}
		if(main.score.getPlayerSize()<2 && main.config.role_count.get(RoleLG.CUPIDON)+main.config.role_count.get(RoleLG.COUPLE)>0){
			Bukkit.broadcastMessage(main.text.getText(12));
			return;
		}
		
		Boolean polygamy =main.config.tool_switch.get(ToolLG.POLYGAMY);
		
		if(!polygamy && (main.config.role_count.get(RoleLG.COUPLE)==0 && main.config.role_count.get(RoleLG.CUPIDON)*2>=main.score.getPlayerSize()) || (main.config.role_count.get(RoleLG.COUPLE)!=0 && (main.config.role_count.get(RoleLG.CUPIDON)+main.config.role_count.get(RoleLG.COUPLE))*2>main.score.getPlayerSize())) {
			polygamy=true;
			Bukkit.broadcastMessage(main.text.getText(192));
		}
		String j1;
		String j2;
		
		for(String playername:main.playerLG.keySet()) {
			
			if(main.playerLG.get(playername).isRole(RoleLG.CUPIDON)) {
				
				if (main.playerLG.get(playername).hasPower() || !main.playerLG.get(main.playerLG.get(playername).getAffectedPlayer().get(0)).isState(State.LIVING) || !main.playerLG.get(main.playerLG.get(playername).getAffectedPlayer().get(1)).isState(State.LIVING)) {
					
					if(couples.contains(playername)) {
						couples.remove(playername);
						j1 = couples.get((int) Math.floor(r.nextFloat()*couples.size()));
						couples.remove(j1);
						j2 = couples.get((int) Math.floor(r.nextFloat()*couples.size()));
						couples.add(j1);
						couples.add(playername);
					}
					else {
						j1 = couples.get((int) Math.floor(r.nextFloat()*couples.size()));
						couples.remove(j1);
						j2 = couples.get((int) Math.floor(r.nextFloat()*couples.size()));
						couples.add(j1);
					}
					
					main.playerLG.get(playername).clearAffectedPlayer();
					main.playerLG.get(playername).addAffectedPlayer(j1);
					main.playerLG.get(playername).addAffectedPlayer(j2);
					main.playerLG.get(playername).setPower(false);
					if(Bukkit.getPlayer(playername)!=null) {
						Bukkit.getPlayer(playername).sendMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.CUPIDON),j1,j2));
					}	
				}
				else {
					j1 = main.playerLG.get(playername).getAffectedPlayer().get(0);
					j2 = main.playerLG.get(playername).getAffectedPlayer().get(1);
				}	
				if(!polygamy) {
					couples.remove(j1);
					couples.remove(j2);
				}
				if(!main.playerLG.get(j1).getCouple().contains(j2)) {
					main.playerLG.get(j1).addCouple(j2);
				}
				
				if(!main.playerLG.get(j2).getCouple().contains(j1)) {
					main.playerLG.get(j2).addCouple(j1);
				}
			}
		}
		for(int i = 0; i< main.config.role_count.get(RoleLG.COUPLE); i++) {
			
			j1 = couples.get((int) Math.floor(r.nextFloat()*couples.size()));
			couples.remove(j1);
			j2 = couples.get((int) Math.floor(r.nextFloat()*couples.size()));
			couples.add(j1);
			
			if(!polygamy) {
				couples.remove(j1);
				couples.remove(j2);
			}
			if(!main.playerLG.get(j1).getCouple().contains(j2)) {
				main.playerLG.get(j1).addCouple(j2);
			}
			
			if(!main.playerLG.get(j2).getCouple().contains(j1)) {
				main.playerLG.get(j2).addCouple(j1);
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
				PlayerLG plg = main.playerLG.get(p);
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

					StringBuilder couple = new StringBuilder();

					for (String c : plg.getCouple()) {
						couple.append(c).append(" ");
					}
					pj2.sendMessage(String.format(main.text.description.get(RoleLG.COUPLE),couple.toString()));
					pj2.playSound(pj2.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
				}
			}
		}
		
		
	}
	
	private void range_couple() {
		
		List<String> couples = new ArrayList<>();
		main.config.role_count.put(RoleLG.COUPLE,0);
		
		for(String playername:main.playerLG.keySet()) {
			if(!main.playerLG.get(playername).getCouple().isEmpty()){
				couples.add(playername);
			}
		}
		
		while(!couples.isEmpty()) {
			
			List<String> linkCouple= new ArrayList<>();
			linkCouple.add(couples.get(0));
			couples.remove(0);
			
			for(int j=0;j<linkCouple.size();j++) {
				for(String playername:main.playerLG.keySet()) {
					if(main.playerLG.get(playername).getCouple().contains(linkCouple.get(j))) {
						if(!linkCouple.contains(playername)) {
							linkCouple.add(playername);
							couples.remove(playername);
						}
					}		
				}
			}
			couple_range.add(linkCouple);
			main.config.role_count.put(RoleLG.COUPLE,main.config.role_count.get(RoleLG.COUPLE)+1);
		}		
	}



	public void thiefCoupleRange(String killername, String playername) {
		
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

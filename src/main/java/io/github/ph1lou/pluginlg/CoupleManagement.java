package io.github.ph1lou.pluginlg;


import java.util.ArrayList;
import java.util.List;

import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class CoupleManagement {

	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

	private MainLG main;
	
	public CoupleManagement(MainLG main) {
		this.main=main;	
	}
	
	public void auto_couple() {
		
		List<String> pcouple =new ArrayList<String>();
		for(String p:main.playerlg.keySet()) {
			if(main.playerlg.get(p).isState(State.VIVANT)) {
				pcouple.add(p);
			}
		}
		
		Boolean polygamie =main.config.tool_switch.get(ToolLG.polygamie);
		
		if(!polygamie && (main.config.rolecount.get(RoleLG.COUPLE) + main.config.rolecount.get(RoleLG.CUPIDON))*2>=main.score.getPlayerSize()) {
			polygamie=true;
			Bukkit.broadcastMessage(main.texte.getText(192));
		}
		String j1;
		String j2;
		
		for(String playername:main.playerlg.keySet()) {
			
			if(main.playerlg.get(playername).isRole(RoleLG.CUPIDON)) {
				
				if (main.playerlg.get(playername).hasPower() || !main.playerlg.get(main.playerlg.get(playername).getAffectedPlayer().get(0)).isState(State.VIVANT) || !main.playerlg.get(main.playerlg.get(playername).getAffectedPlayer().get(1)).isState(State.VIVANT)) {
					
					if(pcouple.contains(playername)) {
						pcouple.remove(playername);
						j1 = pcouple.get((int) Math.floor(Math.random()*pcouple.size()));
						pcouple.remove(j1);
						j2 = pcouple.get((int) Math.floor(Math.random()*pcouple.size()));
						pcouple.add(j1);
						pcouple.add(playername);
					}
					else {
						j1 = pcouple.get((int) Math.floor(Math.random()*pcouple.size()));
						pcouple.remove(j1);
						j2 = pcouple.get((int) Math.floor(Math.random()*pcouple.size()));
						pcouple.add(j1);
					}
					
					main.playerlg.get(playername).clearAffectedPlayer();
					main.playerlg.get(playername).addAffectedPlayer(j1);
					main.playerlg.get(playername).addAffectedPlayer(j2);
					main.playerlg.get(playername).setPower(false);	
					if(Bukkit.getPlayer(playername)!=null) {
						Bukkit.getPlayer(playername).sendMessage(main.texte.getText(12)+j1+" et "+j2);
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
		for(int i=0; i< main.config.rolecount.get(RoleLG.COUPLE);i++) {
			
			j1 = pcouple.get((int) Math.floor(Math.random()*pcouple.size()));
			pcouple.remove(j1);
			j2 = pcouple.get((int) Math.floor(Math.random()*pcouple.size()));
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
		announcecouple();
		if(!main.isState(StateLG.FIN)) {
			main.check_victory();
		}
	}
	
	
	
	
	private void announcecouple() {
		
		for(int i=0;i<main.couplerange.size();i++) {
			for(int j=0;j<main.couplerange.get(i).size();j++) {
				String p=main.couplerange.get(i).get(j);
				
				
				PlayerLG plg = main.playerlg.get(p);
				plg.clearCouple();
				for(int l=0;l<main.couplerange.get(i).size();l++) {
					if(main.couplerange.get(i).get(l)!=p) {
						plg.addCouple(main.couplerange.get(i).get(l));
					}
					
				}
				if(Bukkit.getPlayer(p)!=null) {
					
					Player pj2 = Bukkit.getPlayer(p);
					
					for(ItemStack k:main.stufflg.rolestuff.get(RoleLG.COUPLE)) {
						
						if(pj2.getInventory().firstEmpty()==-1) {
							pj2.getWorld().dropItem(pj2.getLocation(),k);
						}
						else {
							pj2.getInventory().addItem(k);
							pj2.updateInventory();
						}
					}
					
					StringBuilder strb =new StringBuilder();
					
					for(String c:plg.getCouple()) {
						strb.append(c+" ");
						
					}
					pj2.sendMessage(main.texte.esthetique("§m", "§d",main.texte.powerhasbeenuse.get(RoleLG.COUPLE)+strb.toString()+main.texte.getText(11)));
					pj2.playSound(pj2.getLocation(), Sound.SHEEP_SHEAR,1,20);
				}	
			}
		}
		
		
	}
	
	private void range_couple() {
		
		List<String> allcouple = new ArrayList<>();
		main.config.rolecount.put(RoleLG.COUPLE,0);
		
		for(String playername:main.playerlg.keySet()) {
			if(!main.playerlg.get(playername).getCouple().isEmpty()){
				allcouple.add(playername);
			}
		}
		
		while(!allcouple.isEmpty()) {
			
			List<String> couplelie= new ArrayList<String>();
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
			main.couplerange.add(couplelie);
			main.config.rolecount.put(RoleLG.COUPLE,main.config.rolecount.get(RoleLG.COUPLE)+1);	
		}		
	}

	public void couple_life() {
		
		if(main.config.tool_switch.get(ToolLG.barre_couple)){
			
			for(int i=0;i<main.couplerange.size();i++) {
				
				double health =0;
				int nbok=0;
				for(int j=0;j<main.couplerange.get(i).size();j++) {
					String c0= main.couplerange.get(i).get(j);
					if(Bukkit.getPlayer(c0)!=null) {
						Player c=Bukkit.getPlayer(c0);
						nbok++;
						health+=c.getHealth()/c.getMaxHealth();
					}
				}
				health/=nbok;
				
				for(int j=0;j<main.couplerange.get(i).size();j++) {
					String c0= main.couplerange.get(i).get(j);
					if(Bukkit.getPlayer(c0)!=null) {
						Player c =Bukkit.getPlayer(c0);
						if(health*c.getMaxHealth()>12) {
							Bukkit.getPlayer(c0).setHealth(health*c.getMaxHealth());
						}
					}
				}
			}
		}
	}

	public void thief_couplerange(String killername, String playername) {
		
		int cp=-1; 
		int ck=-1;
		for(int i=0;i<main.couplerange.size();i++) {
			if(main.couplerange.get(i).contains(playername) && !main.couplerange.get(i).contains(killername)) {
				main.couplerange.get(i).remove(playername);
				main.couplerange.get(i).add(killername);
				cp=i;
			}
			else if(!main.couplerange.get(i).contains(playername) && main.couplerange.get(i).contains(killername)) {
				ck=i;
			}
		}
		if(cp!=-1&&ck!=-1) {
			main.couplerange.get(ck).remove(killername);
			main.couplerange.get(cp).addAll(main.couplerange.get(ck));
			main.couplerange.remove(ck);
		}
				
	}
}

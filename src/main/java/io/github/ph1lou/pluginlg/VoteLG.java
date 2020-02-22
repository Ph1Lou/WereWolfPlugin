package io.github.ph1lou.pluginlg;


import java.util.ArrayList;
import java.util.List;

import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;





public class VoteLG {
	
	
	private MainLG main;
	public List<String> templayer = new ArrayList<>();
	
	public VoteLG(MainLG main) {
		this.main=main;
	}
	
	public void setUnVote(Player votant,String cible){
		
		if(!main.playerlg.containsKey(votant.getName())) {
			votant.sendMessage(main.texte.getText(67));	
		}
		else if(!main.playerlg.get(votant.getName()).isState(State.VIVANT)) {
			votant.sendMessage(main.texte.getText(155));	
		}
		else if(main.score.getTimer()<main.config.value.get(TimerLG.vote_begin)) {
			votant.sendMessage(main.texte.getText(156));
		}	
		else if(!main.config.tool_switch.get(ToolLG.vote)) {
			votant.sendMessage(main.texte.getText(157));
		}
		else if (main.score.getTimer()%(main.config.value.get(TimerLG.day_duration)*2) >= main.config.value.get(TimerLG.vote_duration) ){
			votant.sendMessage(main.texte.getText(158));
		}	
		else if (main.playerlg.get(votant.getName()).hasVote()) {
			votant.sendMessage(main.texte.getText(159));	
		}
		else if (!main.playerlg.containsKey(cible) || main.playerlg.get(cible).isState(State.MORT)){
			votant.sendMessage(main.texte.getText(160));
		}
		else if (templayer.contains(cible)){
			votant.sendMessage(main.texte.getText(161));	
		}
		else {
			main.playerlg.get(votant.getName()).setVote(true);
			
			if(main.playerlg.get(votant.getName()).isRole(RoleLG.CORBEAU)){
				main.playerlg.get(cible).incVote();
				
			}
			main.playerlg.get(cible).incVote();
			votant.sendMessage(main.texte.esthetique("§m", "§2",main.texte.getText(162)+ cible));
		}
				
	}
		
	public void resetvote() {
		for(String playername:main.playerlg.keySet()) {
			main.playerlg.get(playername).resetVote();
			main.playerlg.get(playername).setVote(false);
		}
	}
		
	public void resultatvote() {
		
		int maxvote=0;
		String playermax="";
		
		for(String playername:main.playerlg.keySet()) {
			
			if (main.playerlg.get(playername).getVote()>maxvote)  {
				maxvote = main.playerlg.get(playername).getVote();
				playermax=playername;
			}	
		}
		if(maxvote==0) return;
		if(maxvote<=1) Bukkit.broadcastMessage(main.texte.getText(191));

		else if(main.playerlg.get(playermax).isState(State.VIVANT)) {
			templayer.add(playermax);
			Bukkit.broadcastMessage(main.texte.esthetique("§m", "§e",playermax+main.texte.getText(163)+Integer.toString(maxvote)+main.texte.getText(164)));
			if(Bukkit.getPlayer(playermax)!=null){
				Player player =Bukkit.getPlayer(playermax);
				double life =player.getMaxHealth();
				player.setMaxHealth(life-10);
				if(player.getHealth()>player.getMaxHealth()) {
					player.setHealth(life-10);
				}
				main.playerlg.get(playermax).addKLostHeart(10);
			}			
		}
		resetvote();
		
	}
}

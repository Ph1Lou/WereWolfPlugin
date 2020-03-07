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
	
	
	private final MainLG main;
	public final List<String> templayer = new ArrayList<>();
	
	public VoteLG(MainLG main) {
		this.main=main;
	}
	
	public void setUnVote(Player votant,String cible){
		
		if(!main.playerlg.containsKey(votant.getName())) {
			votant.sendMessage(main.text.getText(67));
		}
		else if(!main.playerlg.get(votant.getName()).isState(State.VIVANT)) {
			votant.sendMessage(main.text.getText(155));
		}
		else if(main.score.getTimer()<main.config.value.get(TimerLG.VOTE_BEGIN)) {
			votant.sendMessage(main.text.getText(156));
		}	
		else if(!main.config.tool_switch.get(ToolLG.VOTE)) {
			votant.sendMessage(main.text.getText(157));
		}
		else if (main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2) >= main.config.value.get(TimerLG.VOTE_DURATION) ){
			votant.sendMessage(main.text.getText(158));
		}	
		else if (!main.playerlg.get(votant.getName()).getVotedPlayer().equals("")) {
			votant.sendMessage(main.text.getText(159));
		}
		else if (!main.playerlg.containsKey(cible) || main.playerlg.get(cible).isState(State.MORT)){
			votant.sendMessage(main.text.getText(160));
		}
		else if (templayer.contains(cible)){
			votant.sendMessage(main.text.getText(161));
		}
		else {
			main.playerlg.get(votant.getName()).setVote(cible);
			
			if(main.playerlg.get(votant.getName()).isRole(RoleLG.CORBEAU)){
				main.playerlg.get(cible).incVote();
				
			}
			main.playerlg.get(cible).incVote();
			votant.sendMessage(String.format(main.text.getText(162),cible));
		}
				
	}
		
	public void resetvote() {
		for(String playername:main.playerlg.keySet()) {
			main.playerlg.get(playername).resetVote();
			main.playerlg.get(playername).setVote("");
		}
	}

	public void depouiller(Player player) {
		player.sendMessage(main.text.getText(95));
		for(String playername:main.playerlg.keySet()) {
			if(!main.playerlg.get(playername).getVotedPlayer().equals("")){
				player.sendMessage(String.format(main.text.getText(96),playername,main.playerlg.get(playername).getVotedPlayer()));
			}
		}
	}

	public String getResult(){
		int maxvote=0;
		String playermax="";

		for(String playername:main.playerlg.keySet()) {

			if (main.playerlg.get(playername).getVote()>maxvote)  {
				maxvote = main.playerlg.get(playername).getVote();
				playermax=playername;
			}
		}
		if(maxvote==0) return "";
		if(maxvote<=1) {
			Bukkit.broadcastMessage(main.text.getText(191));
			return "";
		}
		return playermax;
	}

	public void showresultatvote(String playermax) {

		if(main.playerlg.containsKey(playermax) && main.playerlg.get(playermax).isState(State.VIVANT)) {
			templayer.add(playermax);
			Bukkit.broadcastMessage(String.format(main.text.getText(163),playermax, main.playerlg.get(playermax).getVote()));
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

package io.github.ph1lou.pluginlg;


import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;





public class VoteLG {
	
	
	private final MainLG main;
	public final List<String> tempPlayer = new ArrayList<>();
	
	public VoteLG(MainLG main) {
		this.main=main;
	}
	
	public void setUnVote(Player elector,String vote){
		
		if(!main.playerLG.containsKey(elector.getName())) {
			elector.sendMessage(main.text.getText(67));
		}
		else if (!main.playerLG.get(elector.getName()).isState(State.LIVING)) {
            elector.sendMessage(main.text.getText(155));
        } else if (main.config.timerValues.get(TimerLG.VOTE_BEGIN) > 0) {
            elector.sendMessage(main.text.getText(156));
        } else if (!main.config.configValues.get(ToolLG.VOTE)) {
            elector.sendMessage(main.text.getText(157));
        } else if (main.score.getTimer() % (main.config.timerValues.get(TimerLG.DAY_DURATION) * 2) >= main.config.timerValues.get(TimerLG.VOTE_DURATION)) {
            elector.sendMessage(main.text.getText(158));
        } else if (!main.playerLG.get(elector.getName()).getVotedPlayer().equals("")) {
            elector.sendMessage(main.text.getText(159));
        } else if (!main.playerLG.containsKey(vote)) {
            elector.sendMessage(main.text.getText(132));
        } else if (main.playerLG.get(vote).isState(State.MORT)) {
            elector.sendMessage(main.text.getText(132));
		}
		else if (tempPlayer.contains(vote)){
			elector.sendMessage(main.text.getText(161));
		}
		else {
			main.playerLG.get(elector.getName()).setVote(vote);
			
			if(main.playerLG.get(elector.getName()).isRole(RoleLG.CORBEAU)){
				main.playerLG.get(vote).incVote();
				
			}
			main.playerLG.get(vote).incVote();
			elector.sendMessage(String.format(main.text.getText(162),vote));
		}
				
	}
		
	public void resetVote() {
		for(String playername:main.playerLG.keySet()) {
			main.playerLG.get(playername).resetVote();
			main.playerLG.get(playername).setVote("");
		}
	}

	public void seeVote(Player player) {
		player.sendMessage(main.text.getText(95));
		for(String playername:main.playerLG.keySet()) {
			if(!main.playerLG.get(playername).getVotedPlayer().equals("")){
				player.sendMessage(String.format(main.text.getText(96),playername,main.playerLG.get(playername).getVotedPlayer()));
			}
		}
	}

	public String getResult(){
		int maxVote=0;
		String playerVote="";

		for(String playername:main.playerLG.keySet()) {

			if (main.playerLG.get(playername).getVote()>maxVote)  {
				maxVote = main.playerLG.get(playername).getVote();
				playerVote=playername;
			}
		}
		if(maxVote==0) return "";
		if(maxVote<=1) {
			Bukkit.broadcastMessage(main.text.getText(191));
			return "";
		}
		return playerVote;
	}

	public void showResultVote(String playerVote) {

		if(main.playerLG.containsKey(playerVote) && main.playerLG.get(playerVote).isState(State.LIVING)) {
			tempPlayer.add(playerVote);
			if(Bukkit.getPlayer(playerVote)!=null){
				Player player =Bukkit.getPlayer(playerVote);
				double life =player.getMaxHealth();
				player.setMaxHealth(life-10);
				if(player.getHealth()>player.getMaxHealth()) {
					player.setHealth(life-10);
				}
				Bukkit.broadcastMessage(String.format(main.text.getText(163),playerVote, main.playerLG.get(playerVote).getVote()));
				main.playerLG.get(playerVote).addKLostHeart(10);
			}			
		}
		resetVote();
	}
}

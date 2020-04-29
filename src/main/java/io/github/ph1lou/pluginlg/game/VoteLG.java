package io.github.ph1lou.pluginlg.game;


import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;





public class VoteLG {
	
	
	private final GameManager game;
	public final List<String> tempPlayer = new ArrayList<>();
	
	public VoteLG(GameManager game) {
		this.game=game;
	}
	
	public void setUnVote(Player elector,String vote){
		
		PlayerLG plg = game.playerLG.get(elector.getName());
		
		if (!plg.isState(State.LIVING)) {
            elector.sendMessage(game.text.getText(155));
        } else if (game.config.timerValues.get(TimerLG.VOTE_BEGIN) > 0) {
            elector.sendMessage(game.text.getText(156));
        } else if (!game.config.configValues.get(ToolLG.VOTE)) {
            elector.sendMessage(game.text.getText(157));
        } else if (game.score.getTimer() % (game.config.timerValues.get(TimerLG.DAY_DURATION) * 2) >= game.config.timerValues.get(TimerLG.VOTE_DURATION)) {
            elector.sendMessage(game.text.getText(158));
        } else if (!game.playerLG.get(elector.getName()).getVotedPlayer().equals("")) {
            elector.sendMessage(game.text.getText(159));
        } else if (!game.playerLG.containsKey(vote)) {
            elector.sendMessage(game.text.getText(132));
        } else if (game.playerLG.get(vote).isState(State.MORT)) {
            elector.sendMessage(game.text.getText(132));
		}
		else if (tempPlayer.contains(vote)){
			elector.sendMessage(game.text.getText(161));
		}
		else {
			game.playerLG.get(elector.getName()).setVote(vote);
			
			if(game.playerLG.get(elector.getName()).isRole(RoleLG.CORBEAU)){
				game.playerLG.get(vote).incVote();
				
			}
			game.playerLG.get(vote).incVote();
			elector.sendMessage(String.format(game.text.getText(162),vote));
		}
				
	}
		
	public void resetVote() {
		for(String playername:game.playerLG.keySet()) {
			game.playerLG.get(playername).resetVote();
			game.playerLG.get(playername).setVote("");
		}
	}

	public void seeVote(Player player) {

		player.sendMessage(game.text.getText(95));
		for(String playername:game.playerLG.keySet()) {
			if(!game.playerLG.get(playername).getVotedPlayer().equals("")){
				player.sendMessage(String.format(game.text.getText(96),playername,game.playerLG.get(playername).getVotedPlayer()));
			}
		}
	}

	public String getResult(){
		int maxVote=0;
		String playerVote="";

		for(String playername:game.playerLG.keySet()) {

			if (game.playerLG.get(playername).getVote()>maxVote)  {
				maxVote = game.playerLG.get(playername).getVote();
				playerVote=playername;
			}
		}
		if(maxVote==0) return "";
		if(maxVote<=1) {
			Bukkit.broadcastMessage(game.getText(191));
			return "";
		}
		return playerVote;
	}

	public void showResultVote(String playerVote) {

		if(game.playerLG.containsKey(playerVote) && game.playerLG.get(playerVote).isState(State.LIVING)) {
			tempPlayer.add(playerVote);
			if (Bukkit.getPlayer(playerVote) != null) {
				Player player = Bukkit.getPlayer(playerVote);
				double life = player.getMaxHealth();
				player.setMaxHealth(life - 10);
				if (player.getHealth() > player.getMaxHealth()) {
					player.setHealth(life - 10);
				}
				Bukkit.broadcastMessage(String.format(game.text.getText(163), playerVote, game.playerLG.get(playerVote).getVote()));
				game.playerLG.get(playerVote).addKLostHeart(10);
			}
		}
		resetVote();
	}
}

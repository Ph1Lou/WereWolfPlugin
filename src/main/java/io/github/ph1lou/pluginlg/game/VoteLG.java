package io.github.ph1lou.pluginlg.game;


import io.github.ph1lou.pluginlg.events.VoteEvent;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class VoteLG {
	
	
	private final GameManager game;
	public final List<UUID> tempPlayer = new ArrayList<>();
	
	public VoteLG(GameManager game) {
		this.game=game;
	}
	
	public void setUnVote(Player elector,UUID vote){
		
		PlayerLG plg = game.playerLG.get(elector.getUniqueId());
		
		if (!plg.isState(State.ALIVE)) {
            elector.sendMessage(game.translate("werewolf.vote.death"));
        } else if (game.config.getTimerValues().get(TimerLG.VOTE_BEGIN) > 0) {
            elector.sendMessage(game.translate("werewolf.vote.vote_not_yet_activated"));
        } else if (!game.config.getConfigValues().get(ToolLG.VOTE)) {
            elector.sendMessage(game.translate("werewolf.vote.vote_disable"));
        } else if (game.score.getTimer() % (game.config.getTimerValues().get(TimerLG.DAY_DURATION) * 2) >= game.config.getTimerValues().get(TimerLG.VOTE_DURATION)) {
            elector.sendMessage(game.translate("werewolf.vote.not_vote_time"));
        } else if (plg.getVotedPlayer()!=null) {
            elector.sendMessage(game.translate("werewolf.vote.already_voted"));
        } else if (!game.playerLG.containsKey(vote)) {
            elector.sendMessage(game.translate("werewolf.check.player_not_found"));
        } else if (game.playerLG.get(vote).isState(State.DEATH)) {
            elector.sendMessage(game.translate("werewolf.check.player_not_found"));
		}
		else if (tempPlayer.contains(vote)){
			elector.sendMessage(game.translate("werewolf.vote.player_already_voted"));
		}
		else {
			plg.setVote(vote);
			game.playerLG.get(vote).incVote();
			Bukkit.getPluginManager().callEvent(new VoteEvent(elector.getUniqueId(),vote));
			elector.sendMessage(game.translate("werewolf.vote.perform_vote",game.playerLG.get(vote).getName()));
		}
				
	}
		
	public void resetVote() {
		for(UUID uuid:game.playerLG.keySet()) {
			game.playerLG.get(uuid).resetVote();
			game.playerLG.get(uuid).setVote(null);
		}
	}

	public void seeVote(Player player) {

		player.sendMessage(game.translate("werewolf.role.citizen.count_votes"));
		for(UUID uuid:game.playerLG.keySet()) {
			PlayerLG plg=game.playerLG.get(uuid);
			if(plg.getVotedPlayer()!=null){
				player.sendMessage(game.translate("werewolf.role.citizen.see_vote",plg.getName(),game.playerLG.get(plg.getVotedPlayer()).getName()));
			}
		}
	}

	public UUID getResult(){
		int maxVote=0;
		UUID playerVote=null;

		for(UUID uuid:game.playerLG.keySet()) {

			if (game.playerLG.get(uuid).getVote()>maxVote)  {
				maxVote = game.playerLG.get(uuid).getVote();
				playerVote=uuid;
			}
		}
		if(maxVote<=1) {
			Bukkit.broadcastMessage(game.translate("werewolf.vote.no_result"));
			return null;
		}
		return playerVote;
	}

	public void showResultVote(UUID playerVote) {

		if(game.playerLG.containsKey(playerVote)) {
			PlayerLG plg = game.playerLG.get(playerVote);
			if(plg.isState(State.ALIVE)){
				tempPlayer.add(playerVote);
				if (Bukkit.getPlayer(playerVote) != null) {
					Player player = Bukkit.getPlayer(playerVote);
					double life = player.getMaxHealth();
					player.setMaxHealth(life - 10);
					if (player.getHealth() > player.getMaxHealth()) {
						player.setHealth(life - 10);
					}
					Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_result", plg.getName(), plg.getVote()));
					plg.addKLostHeart(10);
				}
			}
		}
		resetVote();
	}
}

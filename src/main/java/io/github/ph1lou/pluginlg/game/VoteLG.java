package io.github.ph1lou.pluginlg.game;


import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.Vote;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import io.github.ph1lou.pluginlgapi.enumlg.VoteStatus;
import io.github.ph1lou.pluginlgapi.events.SeeVoteEvent;
import io.github.ph1lou.pluginlgapi.events.VoteEndEvent;
import io.github.ph1lou.pluginlgapi.events.VoteEvent;
import io.github.ph1lou.pluginlgapi.events.VoteResultEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;


public class VoteLG implements Listener, Vote {
	
	
	private final GameManager game;
	private final List<UUID> tempPlayer = new ArrayList<>();
	private final Map<UUID,Integer> votes = new HashMap<>();
	private final Map<UUID,UUID> voters = new HashMap<>();
	private VoteStatus currentStatus = VoteStatus.NOT_BEGIN;

	public VoteLG(GameManager game) {
		this.game=game;
	}

	@Override
	public void setUnVote(UUID voterUUID,UUID vote){

		PlayerWW plg = game.playerLG.get(voterUUID);

		if(Bukkit.getPlayer(voterUUID)==null){
			return;
		}

		Player voter = Bukkit.getPlayer(voterUUID);

		if (!plg.isState(State.ALIVE)) {
            voter.sendMessage(game.translate("werewolf.vote.death"));
        } else if (game.getConfig().getTimerValues().get(TimerLG.VOTE_BEGIN) > 0) {
            voter.sendMessage(game.translate("werewolf.vote.vote_not_yet_activated"));
        } else if (!game.getConfig().getConfigValues().get(ToolLG.VOTE)) {
            voter.sendMessage(game.translate("werewolf.vote.vote_disable"));
        } else if (!currentStatus.equals(VoteStatus.IN_PROGRESS)) {
            voter.sendMessage(game.translate("werewolf.vote.not_vote_time"));
        } else if (voters.containsKey(voterUUID)) {
            voter.sendMessage(game.translate("werewolf.vote.already_voted"));
        } else if (!game.playerLG.containsKey(vote)) {
            voter.sendMessage(game.translate("werewolf.check.player_not_found"));
        } else if (game.playerLG.get(vote).isState(State.DEATH)) {
            voter.sendMessage(game.translate("werewolf.check.player_not_found"));
		}
		else if (tempPlayer.contains(vote)){
			voter.sendMessage(game.translate("werewolf.vote.player_already_voted"));
		}
		else {
			this.voters.put(voterUUID,vote);
			this.votes.merge(vote, 1, Integer::sum);
			Bukkit.getPluginManager().callEvent(new VoteEvent(voterUUID,vote));
			voter.sendMessage(game.translate("werewolf.vote.perform_vote",game.playerLG.get(vote).getName()));
		}
				
	}

	@EventHandler
	public void onVoteEnd(VoteEndEvent event){

		this.currentStatus=VoteStatus.WAITING_CITIZEN;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVoteResult(VoteResultEvent event){

		event.setPlayerVotedUUID(getResult());
		showResultVote(event.getPlayerVoteUUID());
		this.currentStatus=VoteStatus.NOT_IN_PROGRESS;
	}

	@Override
	public void resetVote() {
		this.voters.clear();
		this.votes.clear();
	}

	@Override
	public void seeVote(Player player) {
		Bukkit.getPluginManager().callEvent(new SeeVoteEvent(player.getUniqueId(),votes));
		player.sendMessage(game.translate("werewolf.role.citizen.count_votes"));
		for(UUID uuid:voters.keySet()) {
			String voterName=game.playerLG.get(uuid).getName();
			String voteName=game.playerLG.get(this.voters.get(uuid)).getName();
			player.sendMessage(game.translate("werewolf.role.citizen.see_vote",voterName,voteName));
		}
	}

	@Override
	public Map<UUID,Integer> getVotes(){
		return this.votes;
	}

	@Override
	public UUID getResult(){
		int maxVote=0;
		UUID playerVote=null;

		for(UUID uuid:this.votes.keySet()) {

			if (this.votes.get(uuid)>maxVote)  {
				maxVote = this.votes.get(uuid);
				playerVote=uuid;
			}
		}
		if(maxVote<=1) {
			Bukkit.broadcastMessage(game.translate("werewolf.vote.no_result"));
			return null;
		}
		return playerVote;
	}

	@Override
	public void showResultVote(UUID playerVoteUUID) {

		if(playerVoteUUID != null){

			PlayerWW plg = game.playerLG.get(playerVoteUUID);

			if(plg.isState(State.ALIVE)){

				tempPlayer.add(playerVoteUUID);
				if (Bukkit.getPlayer(playerVoteUUID) != null) {
					Player player = Bukkit.getPlayer(playerVoteUUID);
					double life = player.getMaxHealth();
					player.setMaxHealth(life - 10);
					if (player.getHealth() > player.getMaxHealth()) {
						player.setHealth(life - 10);
					}
					Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_result", plg.getName(), this.votes.get(playerVoteUUID)));
					plg.addKLostHeart(10);
				}
			}
		}
		resetVote();
		currentStatus=VoteStatus.NOT_IN_PROGRESS;
	}

	@Override
	public boolean isStatus(VoteStatus status){
		return this.currentStatus==status;
	}

	@Override
	public void setStatus (VoteStatus status){
		this.currentStatus=status;
	}
}

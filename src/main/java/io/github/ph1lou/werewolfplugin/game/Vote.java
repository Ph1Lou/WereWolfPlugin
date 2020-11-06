package io.github.ph1lou.werewolfplugin.game;


import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.VoteAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.ConfigsBase;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.enumlg.TimersBase;
import io.github.ph1lou.werewolfapi.enumlg.VoteStatus;
import io.github.ph1lou.werewolfapi.events.SeeVoteEvent;
import io.github.ph1lou.werewolfapi.events.VoteEndEvent;
import io.github.ph1lou.werewolfapi.events.VoteEvent;
import io.github.ph1lou.werewolfapi.events.VoteResultEvent;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;


public class Vote implements Listener, VoteAPI {
	
	
	private final WereWolfAPI game;
	private final List<UUID> tempPlayer = new ArrayList<>();
	private final Map<UUID,Integer> votes = new HashMap<>();
	private final Map<UUID,UUID> voters = new HashMap<>();
	private VoteStatus currentStatus = VoteStatus.NOT_BEGIN;

	public Vote(WereWolfAPI game) {
		this.game=game;
	}

	@Override
	public void setUnVote(UUID voterUUID,UUID vote) {

        Player voter = Bukkit.getPlayer(voterUUID);

        if (voter == null) return;

        if (game.getConfig().getTimerValues().get(TimersBase.VOTE_BEGIN.getKey()) > 0) {
            voter.sendMessage(game.translate("werewolf.vote.vote_not_yet_activated"));
        } else if (!game.getConfig().getConfigValues().get(ConfigsBase.VOTE.getKey())) {
            voter.sendMessage(game.translate("werewolf.vote.vote_disable"));
        } else if (!currentStatus.equals(VoteStatus.IN_PROGRESS)) {
            voter.sendMessage(game.translate("werewolf.vote.not_vote_time"));
        } else if (voters.containsKey(voterUUID)) {
            voter.sendMessage(game.translate("werewolf.vote.already_voted"));
        } else if (tempPlayer.contains(vote)) {
            voter.sendMessage(game.translate("werewolf.vote.player_already_voted"));
        } else {
            VoteEvent voteEvent = new VoteEvent(voterUUID, vote);
            Bukkit.getPluginManager().callEvent(voteEvent);

            if (voteEvent.isCancelled()) {
                voter.sendMessage(game.translate("werewolf.check.cancel"));
                return;
            }
            this.voters.put(voteEvent.getPlayerUUID(), voteEvent.getTargetUUID());
            this.votes.merge(vote, 1, Integer::sum);

            voter.sendMessage(game.translate("werewolf.vote.perform_vote", game.getPlayersWW().get(vote).getName()));
        }

	}

	@EventHandler
	public void onVoteEnd(VoteEndEvent event) {
		this.currentStatus = VoteStatus.WAITING_CITIZEN;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onVoteResult(VoteResultEvent event) {
        if (!event.isCancelled()) {
            event.setPlayerVotedUUID(getResult());
            if (event.getPlayerVoteUUID() == null) {
                event.setCancelled(true);
            } else showResultVote(event.getPlayerVoteUUID());
        }
        this.currentStatus = VoteStatus.NOT_IN_PROGRESS;
        resetVote();
    }

	@Override
	public void resetVote() {
		this.voters.clear();
		this.votes.clear();
	}

	@Override
	public void seeVote(Player player) {

		SeeVoteEvent seeVoteEvent = new SeeVoteEvent(player.getUniqueId(), votes);
		Bukkit.getPluginManager().callEvent(seeVoteEvent);

		if (seeVoteEvent.isCancelled()) {
			player.sendMessage(game.translate("werewolf.check.cancel"));
			return;
		}
		player.sendMessage(game.translate("werewolf.role.citizen.count_votes"));
		for (UUID uuid : voters.keySet()) {
			String voterName = game.getPlayersWW().get(uuid).getName();
			String voteName = game.getPlayersWW().get(this.voters.get(uuid)).getName();
			player.sendMessage(game.translate("werewolf.role.citizen.see_vote", voterName, voteName));
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

		if(playerVoteUUID != null) {

            PlayerWW plg = game.getPlayersWW().get(playerVoteUUID);
            Player player = Bukkit.getPlayer(playerVoteUUID);

            if (plg.isState(StatePlayer.ALIVE)) {

                tempPlayer.add(playerVoteUUID);
                if (player != null) {
                    double life = VersionUtils.getVersionUtils().getPlayerMaxHealth(player);
                    VersionUtils.getVersionUtils().setPlayerMaxHealth(player, life - 10);
                    if (player.getHealth() > VersionUtils.getVersionUtils().getPlayerMaxHealth(player)) {
                        player.setHealth(life - 10);
                    }
                    Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_result", plg.getName(), this.votes.get(playerVoteUUID)));
                    plg.addKLostHeart(10);
                }
            }
		}
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

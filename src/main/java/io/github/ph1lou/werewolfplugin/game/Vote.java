package io.github.ph1lou.werewolfplugin.game;


import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.IVoteManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigBase;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.enums.VoteStatus;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteBeginEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteEndEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import io.github.ph1lou.werewolfapi.events.roles.seer.SeeVoteEvent;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Vote implements Listener, IVoteManager {


	private final WereWolfAPI game;
	private final List<IPlayerWW> tempPlayer = new ArrayList<>();
	private final Map<IPlayerWW, Integer> votes = new HashMap<>();
	private final Map<IPlayerWW, IPlayerWW> voters = new HashMap<>();
	private VoteStatus currentStatus = VoteStatus.NOT_BEGIN;

	public Vote(WereWolfAPI game) {
		this.game = game;
	}

	@Override
	public void setUnVote(IPlayerWW voterWW, IPlayerWW vote) {

		Player voter = Bukkit.getPlayer(voterWW.getUUID());

		if (voter == null) return;

		if (game.getConfig().getTimerValue(TimerBase.VOTE_BEGIN.getKey()) > 0) {
			voterWW.sendMessageWithKey("werewolf.vote.vote_not_yet_activated");
		} else if (!game.getConfig().isConfigActive(ConfigBase.VOTE.getKey())) {
			voterWW.sendMessageWithKey("werewolf.vote.vote_disable");
		} else if (!currentStatus.equals(VoteStatus.IN_PROGRESS)) {
			voterWW.sendMessageWithKey("werewolf.vote.not_vote_time");
		} else if (voters.containsKey(voterWW)) {
			voterWW.sendMessageWithKey("werewolf.vote.already_voted");
		} else if (tempPlayer.contains(vote)) {
			voterWW.sendMessageWithKey("werewolf.vote.player_already_voted");
		} else {
			VoteEvent voteEvent = new VoteEvent(voterWW, vote);
			Bukkit.getPluginManager().callEvent(voteEvent);

			if (voteEvent.isCancelled()) {
				voterWW.sendMessageWithKey("werewolf.check.cancel");
				return;
			}
			this.voters.put(voteEvent.getPlayerWW(), voteEvent.getTargetWW());
			this.votes.merge(vote, 1, Integer::sum);

			voterWW.sendMessageWithKey("werewolf.vote.perform_vote",
					Formatter.format("&player&",vote.getName()));
		}

	}

	@EventHandler
	public void onVoteBegin(VoteBeginEvent event) {
		this.currentStatus = VoteStatus.NOT_IN_PROGRESS;
	}

	@EventHandler
	public void onVoteEnd(VoteEndEvent event) {
		this.currentStatus = VoteStatus.WAITING_CITIZEN;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onVoteResult(VoteResultEvent event) {
		if (!event.isCancelled()) {
			event.setPlayerWW(getResult());
			if (event.getPlayerWW() == null) {
				if (currentStatus == VoteStatus.WAITING_CITIZEN) {
					Bukkit.broadcastMessage(game.translate("werewolf.vote.no_result"));
				}
				event.setCancelled(true);
			} else showResultVote(event.getPlayerWW());
		}
        resetVote();
    }

	@Override
	public void resetVote() {
        this.currentStatus = VoteStatus.NOT_IN_PROGRESS;
        this.voters.clear();
        this.votes.clear();
    }

	@Override
	public void seeVote(IPlayerWW playerWW) {

		Player player = Bukkit.getPlayer(playerWW.getUUID());
		SeeVoteEvent seeVoteEvent = new SeeVoteEvent(playerWW, votes);
		Bukkit.getPluginManager().callEvent(seeVoteEvent);

		if (player == null) return;

		if (seeVoteEvent.isCancelled()) {
			player.sendMessage(game.translate("werewolf.check.cancel"));
			return;
		}
		player.sendMessage(game.translate("werewolf.role.citizen.count_votes"));
		for (IPlayerWW playerWW1 : voters.keySet()) {

			IPlayerWW voteWW = this.voters.get(playerWW1);

			String voterName = playerWW1.getName();
			String voteName = voteWW.getName();
			player.sendMessage(game.translate("werewolf.role.citizen.see_vote",
					Formatter.format("&voter&",voterName),
					Formatter.format("&player&",voteName)));
		}
	}

	@Override
	public Map<IPlayerWW, Integer> getVotes() {
		return this.votes;
	}

	@Override
	public Map<IPlayerWW, IPlayerWW> getPlayerVotes() {
		return voters;
	}

	@Override
	public IPlayerWW getResult() {
		int maxVote = 0;
		IPlayerWW playerVote = null;

		for (IPlayerWW playerWW : this.votes.keySet()) {

			if (this.votes.get(playerWW) > maxVote) {
				maxVote = this.votes.get(playerWW);
				playerVote = playerWW;
			}
		}
		if (maxVote <= 1) {
			return null;
		}
		return playerVote;
	}

	@Override
	public void showResultVote(IPlayerWW playerWW) {

		if (playerWW != null) {

			tempPlayer.add(playerWW);

			int health = 5;
			if (playerWW.getMaxHealth() < 10) { //si le joueur a moins de coeurs ont réduit le temps de récupération de coeurs
				health = playerWW.getMaxHealth() / 2 - 1; //-1 car le joueur aura un coeur minimum quand il prend les votes
			}
			playerWW.removePlayerMaxHealth(10);

			Bukkit.broadcastMessage(game.translate("werewolf.vote.vote_result",
					Formatter.format("&player&",playerWW.getName()),
					Formatter.format("&number&",this.votes.get(playerWW))));

			int task = BukkitUtils.scheduleSyncRepeatingTask(() -> {
				if (game.isState(StateGame.GAME)) {
					playerWW.addPlayerMaxHealth(2);
				}
			}, 1200, 1200);

			BukkitUtils.scheduleSyncDelayedTask(() -> Bukkit.getScheduler().cancelTask(task), (long) health * 62 * 20);

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

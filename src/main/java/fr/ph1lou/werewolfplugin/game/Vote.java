package fr.ph1lou.werewolfplugin.game;


import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.events.game.vote.VoteBeginEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEndEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.vote.IVoteManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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
	public void setOneVote(IPlayerWW voterWW, IPlayerWW vote) {

		Player voter = Bukkit.getPlayer(voterWW.getUUID());

		if (voter == null) return;

		if (game.getConfig().getTimerValue(TimerBase.VOTE_BEGIN.getKey()) > 0) {
			voterWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.vote.vote_not_yet_activated");
		} else if (!game.getConfig().isConfigActive(ConfigBase.VOTE.getKey())) {
			voterWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.vote.vote_disable");
		} else if (!currentStatus.equals(VoteStatus.IN_PROGRESS)) {
			voterWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.vote.not_vote_time");
		} else if (this.voters.containsKey(voterWW)) {
			voterWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.vote.already_voted");
		} else if (this.tempPlayer.contains(vote)) {
			voterWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.vote.player_already_voted");
		} else {
			VoteEvent voteEvent = new VoteEvent(voterWW, vote);
			Bukkit.getPluginManager().callEvent(voteEvent);

			if (voteEvent.isCancelled()) {
				voterWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
				return;
			}
			this.voters.put(voteEvent.getPlayerWW(), voteEvent.getTargetWW());
			this.votes.merge(vote, 1, Integer::sum);

			voterWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.vote.perform_vote",
					Formatter.player(vote.getName()));
		}

	}

	@EventHandler
	public void onVoteBegin(VoteBeginEvent event) {
		this.currentStatus = VoteStatus.NOT_IN_PROGRESS;
	}


	@EventHandler(priority = EventPriority.LOW)
	public void onVoteResult(VoteResultEvent event) {
		if (!event.isCancelled()) {
			getResult().ifPresent(event::setPlayerWW);

			if (event.getPlayerWW() == null) {
				if (this.currentStatus == VoteStatus.WAITING) {
					Bukkit.broadcastMessage(game.translate(Prefix.ORANGE.getKey() , "werewolf.vote.no_result"));
				}
				event.setCancelled(true);
			} else {
				showResultVote();
			}
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
	public Map<IPlayerWW, Integer> getVotes() {
		return this.votes;
	}

	@Override
	public Map<IPlayerWW, IPlayerWW> getPlayerVotes() {
		return voters;
	}

	@Override
	public Optional<IPlayerWW> getResult() {
		int maxVote = 0;
		IPlayerWW playerVote = null;

		for (IPlayerWW playerWW : this.votes.keySet()) {

			if (this.votes.get(playerWW) > maxVote) {
				maxVote = this.votes.get(playerWW);
				playerVote = playerWW;
			}
		}
		if (maxVote <= 1) {
			return Optional.empty();
		}
		return Optional.of(playerVote);
	}

	@Override
	public void showResultVote() {

		this.getResult()
				.ifPresent(playerWW -> {
					tempPlayer.add(playerWW);

					int health = 5;
					if (playerWW.getMaxHealth() < 10) { //si le joueur a moins de coeurs ont réduit le temps de récupération de coeurs
						health = playerWW.getMaxHealth() / 2 - 1; //-1 car le joueur aura un coeur minimum quand il prend les votes
					}
					playerWW.removePlayerMaxHealth(10);

					Bukkit.broadcastMessage(game.translate(Prefix.YELLOW.getKey() , "werewolf.vote.vote_result",
							Formatter.player(playerWW.getName()),
							Formatter.number(this.votes.get(playerWW))));

					int task = BukkitUtils.scheduleSyncRepeatingTask(() -> {
						if (game.isState(StateGame.GAME)) {
							playerWW.addPlayerMaxHealth(2);
						}
					}, 1200, 1200);

					BukkitUtils.scheduleSyncDelayedTask(() -> Bukkit.getScheduler().cancelTask(task), (long) health * 62 * 20);
				});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVoteEnd(VoteEndEvent event) {

		this.currentStatus = VoteStatus.WAITING;
		long duration = game.getConfig().getTimerValue(TimerBase.VOTE_WAITING.getKey());
		BukkitUtils.scheduleSyncDelayedTask(() -> {
			if (!game.isState(StateGame.END)) {
				Bukkit.getPluginManager().callEvent(new VoteResultEvent(this.getResult().orElse(null)));
			}

		}, duration * 20);
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

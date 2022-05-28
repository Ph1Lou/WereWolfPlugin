// 
// Decompiled by Procyon v0.5.36
// 

package fr.ph1lou.werewolfplugin.game;

import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.vote.NewVoteResultEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteBeginEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEndEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.vote.IVoteManager;
import fr.ph1lou.werewolfplugin.configs.Vote;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VoteManager implements Listener, IVoteManager
{
	private final WereWolfAPI game;
	private final List<IPlayerWW> tempPlayers;
	@Nullable
	private IPlayerWW lastVote;
	private final Map<IPlayerWW, Integer> votes;
	private final Map<IPlayerWW, Integer> votesWerewolf;
	private final Map<IPlayerWW, Integer> votesVillager;
	private final Map<IPlayerWW, IPlayerWW> voters;
	private VoteStatus currentStatus;

	public VoteManager(WereWolfAPI game) {
		this.tempPlayers = new ArrayList<>();
		this.votes = new HashMap<>();
		this.votesWerewolf = new HashMap<>();
		this.votesVillager = new HashMap<>();
		this.voters = new HashMap<>();
		this.currentStatus = VoteStatus.NOT_BEGIN;
		this.game = game;
	}

	public void setOneVote(IPlayerWW voterWW, IPlayerWW vote) {
		Player voter = Bukkit.getPlayer(voterWW.getUUID());
		if (voter == null) {
			return;
		}
		if(voterWW.equals(this.lastVote)){
			voterWW.sendMessageWithKey(Prefix.RED,"werewolf.vote.new_voted");
			return;
		}
		if (this.game.getConfig().getTimerValue(TimerBase.VOTE_BEGIN) > 0) {
			voterWW.sendMessageWithKey(Prefix.RED, "werewolf.vote.vote_not_yet_activated");
		}
		else if (!this.game.getConfig().isConfigActive(ConfigBase.VOTE) || this.currentStatus == VoteStatus.ENDED) {
			voterWW.sendMessageWithKey(Prefix.RED, "werewolf.vote.vote_disable");
		}
		else if (!this.currentStatus.equals(VoteStatus.IN_PROGRESS)) {
			voterWW.sendMessageWithKey(Prefix.RED, "werewolf.vote.not_vote_time");
		}
		else if (this.voters.containsKey(voterWW)) {
			voterWW.sendMessageWithKey(Prefix.RED, "werewolf.vote.already_voted");
		}
		else if (this.tempPlayers.contains(vote)) {
			voterWW.sendMessageWithKey(Prefix.RED, "werewolf.vote.player_already_voted");
		}
		else if (this.game.getConfig().isConfigActive(ConfigBase.NEW_VOTE) && voterWW.getRole().isNeutral()) {
			voterWW.sendMessageWithKey(Prefix.RED, "werewolf.vote.neutral");
		}
		else {
			VoteEvent voteEvent = new VoteEvent(voterWW, vote);
			Bukkit.getPluginManager().callEvent(voteEvent);
			if (voteEvent.isCancelled()) {
				voterWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
				return;
			}
			this.voters.put(voterWW, vote);
			this.votes.merge(vote, 1, Integer::sum);
			if (voterWW.getRole().isWereWolf()) {
				this.votesWerewolf.merge(vote, 1, Integer::sum);
			}
			else {
				this.votesVillager.merge(vote, 1, Integer::sum);
			}
			voterWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.vote.perform_vote", Formatter.player(vote.getName()));
		}
	}

	@EventHandler
	public void onVoteBegin(VoteBeginEvent event) {
		this.currentStatus = VoteStatus.NOT_IN_PROGRESS;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onVoteResult(VoteResultEvent event) {
		if (!event.isCancelled()) {
			this.showResultVote(event.getPlayerWW());
			this.lastVote = event.getPlayerWW();
			if(this.lastVote!= null){
				this.tempPlayers.add(this.lastVote);
			}
			else{
				event.setCancelled(true);
			}
		}
		else{
			this.lastVote = null;
		}
		this.currentStatus = VoteStatus.NOT_IN_PROGRESS;
	}

	public void resetVote() {
		this.voters.clear();
		this.votesVillager.clear();
		this.votesWerewolf.clear();
		this.votes.clear();
	}

	public Map<IPlayerWW, Integer> getVotes() {
		if (this.game.getConfig().isConfigActive(ConfigBase.NEW_VOTE)) {
			return this.votesVillager;
		}
		return this.votes;
	}

	public Map<IPlayerWW, IPlayerWW> getPlayerVotes() {
		return this.voters;
	}

	public Optional<IPlayerWW> getResult() {
		if (this.game.getConfig().isConfigActive(ConfigBase.NEW_VOTE)) {
			return this.getResult(this.votesVillager);
		}
		return this.getResult(this.votes);
	}

	private Optional<IPlayerWW> getResult(Map<IPlayerWW, Integer> votes) {
		int maxVote = 0;
		IPlayerWW playerVote = null;
		for (IPlayerWW playerWW : votes.keySet()) {
			if (votes.get(playerWW) > maxVote) {
				maxVote = votes.get(playerWW);
				playerVote = playerWW;
			}
		}
		if (maxVote <= 0) {
			return Optional.empty();
		}
		return Optional.of(playerVote);
	}

	public void showResultVote(@Nullable IPlayerWW playerWW) {

		if (this.game.getConfig().isConfigActive(ConfigBase.NEW_VOTE)) {
			IPlayerWW werewolfWW = this.getResult(this.votesWerewolf).orElse(null);
			if (playerWW != null) {
				Bukkit.broadcastMessage(this.game.translate(Prefix.ORANGE, "werewolf.vote.new_vote_villager", Formatter.player(playerWW.getName())));
			}
			else {
				Bukkit.broadcastMessage(this.game.translate(Prefix.ORANGE, "werewolf.vote.no_result"));
			}
			if (werewolfWW != null) {

				this.game.getPlayersWW()
						.stream()
						.filter(playerWW2 -> playerWW2.getRole().isWereWolf() ||
								playerWW2.getRole().isNeutral())
						.forEach(playerWW2 -> {
							playerWW2.sendMessageWithKey(Prefix.ORANGE, "werewolf.vote.new_vote_werewolf",
									Formatter.player(werewolfWW.getName()));
							if (!playerWW2.getRole().isNeutral()) {
								Player player = Bukkit.getPlayer(playerWW2.getUUID());
								if (player != null) {
									Sound.CAT_MEOW.play(playerWW2, werewolfWW.getLocation());
									player.playEffect(werewolfWW.getLocation(), Effect.STEP_SOUND, (Object)Material.REDSTONE_BLOCK);
								}
							}
						});
			}
			if(playerWW != null && werewolfWW != null){
				Bukkit.getPluginManager().callEvent(new NewVoteResultEvent(playerWW, werewolfWW));
			}
			return;
		}
		if (playerWW == null) {
			Bukkit.broadcastMessage(this.game.translate(Prefix.ORANGE, "werewolf.vote.no_result"));
			return;
		}
		if (this.votes.getOrDefault(playerWW, 0) <= 1) {
			Bukkit.broadcastMessage(this.game.translate(Prefix.ORANGE, "werewolf.vote.no_result_more_one"));
			return;
		}
		double health = 5;
		if (playerWW.getMaxHealth() < 10) {
			health = playerWW.getMaxHealth() / 2 - 1;
		}
		playerWW.removePlayerMaxHealth(10);
		Bukkit.broadcastMessage(this.game.translate(Prefix.YELLOW, "werewolf.vote.vote_result",
				Formatter.player(playerWW.getName()),
				Formatter.number(this.votes.getOrDefault(playerWW, 0))));
		int task = BukkitUtils.scheduleSyncRepeatingTask(() -> {
			if (this.game.isState(StateGame.GAME)) {
				playerWW.addPlayerMaxHealth(2);
			}
		}, 1200L, 1200L);
		BukkitUtils.scheduleSyncDelayedTask(() -> Bukkit.getScheduler().cancelTask(task), (long) (health * 62L * 20L));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVoteEnd(VoteEndEvent event) {
		this.currentStatus = VoteStatus.WAITING;
		long duration = this.game.getConfig().getTimerValue(TimerBase.VOTE_WAITING);
		BukkitUtils.scheduleSyncDelayedTask(() -> {
			if (!this.game.isState(StateGame.END)) {
				Bukkit.getPluginManager().callEvent(new VoteResultEvent(this.getResult().orElse(null)));
			}
		}, duration * 20L);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDay(DayEvent event) {
		if (this.isStatus(VoteStatus.IN_PROGRESS) || this.isStatus(VoteStatus.WAITING)) {
			return;
		}
		if (this.game.getConfig().isConfigActive(ConfigBase.VOTE) &&
				this.game.getPlayersCount() < this.game.getConfig().getValue(Vote.CONFIG) &&
			!this.isStatus(VoteStatus.ENDED)) {
			Bukkit.broadcastMessage(this.game.translate(Prefix.ORANGE, "werewolf.vote.vote_deactivate"));
			this.setStatus(VoteStatus.ENDED);
			return;
		}
		int duration = this.game.getConfig().getTimerValue(TimerBase.VOTE_DURATION);

		if (this.game.getConfig().isConfigActive(ConfigBase.VOTE) &&
				!this.isStatus(VoteStatus.NOT_BEGIN) && !this.isStatus(VoteStatus.ENDED)) {

			this.resetVote();
			Bukkit.getOnlinePlayers().forEach(Sound.CHICKEN_HURT::play);
			Bukkit.broadcastMessage(this.game.translate(Prefix.ORANGE, "werewolf.vote.vote_time",
					Formatter.timer(Utils.conversion(duration))));
			this.setStatus(VoteStatus.IN_PROGRESS);
			BukkitUtils.scheduleSyncDelayedTask(() -> {
				if (!this.game.isState(StateGame.END)) {
					Bukkit.getPluginManager().callEvent(new VoteEndEvent());
				}
			}, duration * 20L);
		}
	}

	public boolean isStatus(VoteStatus status) {
		return this.currentStatus == status;
	}

	public void setStatus(VoteStatus status) {
		this.currentStatus = status;
	}
}

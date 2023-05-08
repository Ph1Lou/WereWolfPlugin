// 
// Decompiled by Procyon v0.5.36
// 

package fr.ph1lou.werewolfplugin.game;

import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.vote.IVoteManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class VoteManager implements Listener, IVoteManager {
    private static final Integer MIN_VOTE = 3;
    private static final int POISON_SECONDS = 13;
    private final WereWolfAPI game;
    private final Set<IPlayerWW> tempPlayers = new HashSet<>();
    private final Map<IPlayerWW, Integer> votes = new HashMap<>();
    private final Map<IPlayerWW, IPlayerWW> voters = new HashMap<>();
    private VoteStatus currentStatus = VoteStatus.NOT_BEGIN;

    public VoteManager(WereWolfAPI game) {
        this.game = game;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVoteResult(VoteResultEvent event) {
        if (!event.isCancelled()) {
            this.showResultVote(event.getPlayerWW());
            IPlayerWW playerWW = event.getPlayerWW();
            if (playerWW != null) {
                this.tempPlayers.add(playerWW);
            } else {
                event.setCancelled(true);
            }
        }
        this.currentStatus = VoteStatus.NOT_IN_PROGRESS;
    }

    public void setOneVote(IPlayerWW voterWW, IPlayerWW vote) {

        if (!voterWW.getRole().getPlayersMet().contains(vote)) {
            voterWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.vote.not_met",
                    Formatter.number(game.getConfig().getValue(IntValueBase.VOTE_DISTANCE)));
            return;

        }

        if (this.tempPlayers.contains(vote)) {
            voterWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.vote.player_already_voted");
            return;
        }
        VoteEvent voteEvent = new VoteEvent(voterWW, vote);
        Bukkit.getPluginManager().callEvent(voteEvent);

        if (voteEvent.isCancelled()) {
            voterWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }
        this.voters.put(voterWW, vote);
        this.votes.merge(vote, 1, Integer::sum);
        voterWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.configurations.vote.perform_vote", Formatter.player(vote.getName()));
    }


    public void resetVote() {
        this.voters.clear();
        this.votes.clear();
    }

    @Override
    public int getVotes(IPlayerWW iPlayerWW) {
        return this.votes.getOrDefault(iPlayerWW, 0);
    }

    @Override
    public void setVotes(IPlayerWW iPlayerWW, int i) {
        this.votes.put(iPlayerWW, i);
    }

    @Override
    public Optional<IPlayerWW> getPlayerVote(IPlayerWW iPlayerWW) {
        return Optional.ofNullable(this.voters.get(iPlayerWW));
    }

    @Override

    public void setPlayerVote(IPlayerWW voterWW, IPlayerWW iPlayerWW1) {
        this.voters.put(voterWW, iPlayerWW1);
    }

    public Optional<IPlayerWW> getResult() {
        return this.getResult(this.votes);
    }


    @Override
    public Set<? extends IPlayerWW> getVotedPlayers() {
        return new HashSet<>(this.voters.values());
    }

    @Override
    public Set<? extends IPlayerWW> getVoters() {
        return this.voters.keySet();
    }


    @Override
    public Set<? extends IPlayerWW> getAlreadyVotedPlayers() {
        return this.tempPlayers;
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
        if (playerVote == null || maxVote < MIN_VOTE) {
            return Optional.empty();
        }
        return Optional.of(playerVote);
    }

    public boolean isStatus(VoteStatus status) {
        return this.currentStatus == status;
    }

    public void setStatus(VoteStatus status) {
        this.currentStatus = status;
    }



    public void showResultVote(@Nullable IPlayerWW playerWW) {


        if (playerWW == null) {
            Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.configurations.vote.no_result"));
            return;
        }

        if (getVotes(playerWW) < MIN_VOTE) {
            Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.configurations.vote.no_result_more_one"));
            return;
        }

        playerWW.addPotionModifier(PotionModifier.add(PotionEffectType.POISON, POISON_SECONDS * 20, 1, "werewolf.vote"));
        Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, "werewolf.configurations.vote.vote_result",
                Formatter.player(playerWW.getName()),
                Formatter.number(getVotes(playerWW))));

        game.getPlayersWW()
                .stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .forEach(playerWW1 -> {
                    if(playerWW1.getLocation().getWorld() == playerWW.getLocation().getWorld()){
                        playerWW1.sendMessageWithKey(Prefix.YELLOW,
                                "werewolf.configurations.vote.distance_voted",
                                Formatter.number(((int) playerWW1.getLocation().distance(playerWW.getLocation())) / 100 * 100 + 100));
                    }});
    }
}

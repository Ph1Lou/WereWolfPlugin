package fr.ph1lou.werewolfplugin.buildings;

import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.buildings.IBuildingsManager;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import fr.ph1lou.werewolfapi.events.buildings.UrnVoteCastEvent;
import fr.ph1lou.werewolfapi.events.buildings.UrnVoteResultEvent;
import fr.ph1lou.werewolfapi.events.game.vote.AbsentionistListVoteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.vote.IUrnVoteManager;
import fr.ph1lou.werewolfplugin.utils.ExposedHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UrnVoteManager implements IUrnVoteManager {

    private final IBuildingsManager buildingsManager;
    private final WereWolfAPI game;

    private boolean active = false;
    private int session = 0;

    private static final int MIN_VOTES = 3;

    private final Set<IPlayerWW> votedAgainst = new HashSet<>();
    private final Set<IPlayerWW> votedBlankOnce = new HashSet<>();
    private final Map<IPlayerWW, Set<Location>> blankVotedUrns = new HashMap<>();
    private final Set<IPlayerWW> electedPlayers = new HashSet<>();
    private final Map<IPlayerWW, Integer> remoteVotesAgainst = new HashMap<>();
    private final Map<IPlayerWW, Integer> ballotDiscardedForSession = new HashMap<>();

    public UrnVoteManager(IBuildingsManager buildingsManager, WereWolfAPI game) {
        this.buildingsManager = buildingsManager;
        this.game = game;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public int getSession() {
        return session;
    }

    public boolean hasVotedAgainst(IPlayerWW player) {
        return votedAgainst.contains(player);
    }

    public boolean hasVotedBlankOnce(IPlayerWW player) {
        return votedBlankOnce.contains(player);
    }

    public boolean isElected(IPlayerWW player) {
        return electedPlayers.contains(player);
    }

    public boolean hasBlankVotedUrn(IPlayerWW player, Location urnLocation) {
        Set<Location> urns = blankVotedUrns.get(player);
        return urns != null && urns.contains(urnLocation);
    }

    public void markVotedAgainst(IPlayerWW player) {
        votedAgainst.add(player);
    }

    public void markVotedBlankOnce(IPlayerWW player) {
        votedBlankOnce.add(player);
    }

    public void markBlankVotedUrn(IPlayerWW player, Location urnLocation) {
        blankVotedUrns.computeIfAbsent(player, k -> new HashSet<>()).add(urnLocation);
    }

    /**
     * Result of an attempt to cast a vote-against ballot (office or remote).
     * REJECTED: voter/target not eligible (inactive session, already voted,
     *           target already elected, or office full).
     * COUNTED:  the ballot was recorded and will be tallied.
     * DISCARDED: the ballot was consumed (voter is penalized) but not recorded,
     *            so it will NOT be counted at dépouillement.
     */
    public enum CastResult {
        REJECTED, COUNTED, DISCARDED
    }

    public CastResult castVoteAgainst(IPlayerWW voter, IPlayerWW target, IVoteOffice office) {

        if (!active) return CastResult.REJECTED;
        if (hasVotedAgainst(voter)) return CastResult.REJECTED;
        if (isElected(target)) return CastResult.REJECTED;
        if (office != null && office.isFull()) return CastResult.REJECTED;

        boolean discarded = isBallotDiscardedFor(voter);

        if (!discarded) {
            if (office != null) {
                office.recordVoteAgainst(target);
            } else {
                remoteVotesAgainst.merge(target, 1, Integer::sum);
            }
        } else {
            ballotDiscardedForSession.remove(voter);
        }

        markVotedAgainst(voter);
        fireCastEvent(voter, target, false, office);

        return discarded ? CastResult.DISCARDED : CastResult.COUNTED;
    }

    public void markBallotDiscarded(IPlayerWW player, int session) {
        ballotDiscardedForSession.put(player, session);
    }

    private boolean isBallotDiscardedFor(IPlayerWW voter) {
        Integer s = ballotDiscardedForSession.get(voter);
        return s != null && s == this.session;
    }

    public void startSession(int n) {
        if (active) {
            endSession(this.session);
        }
        this.session = n;
        this.active = true;
        votedAgainst.clear();
        votedBlankOnce.clear();
        blankVotedUrns.clear();
        remoteVotesAgainst.clear();

        for (IVoteOffice office : buildingsManager.getVoteOffices()) {
            office.resetSession();
            if (n >= 2) {
                office.reduceCapacity(1);
            }
        }
    }

    public void endSession(int sessionNumber) {
        if (!active || this.session != sessionNumber) {
            return;
        }
        active = false;

        // Penalize abstention, mirroring the classic vote: villagers who did not take part
        // (neither voted against someone nor cast a blank ballot) lose 1 honor. The actual
        // honor change is applied by Honor#onAbstentionistList (villagers only).
        Set<IPlayerWW> participants = new HashSet<>(votedAgainst);
        participants.addAll(votedBlankOnce);
        Bukkit.getPluginManager().callEvent(new AbsentionistListVoteEvent(game.getAlivePlayersWW()
                .stream()
                .filter(playerWW -> !participants.contains(playerWW))
                .collect(Collectors.toList())));

        Map<IPlayerWW, Integer> totals = new HashMap<>();
        for (IVoteOffice office : buildingsManager.getVoteOffices()) {
            for (Map.Entry<IPlayerWW, Integer> e : office.getVotesAgainst().entrySet()) {
                IPlayerWW target = e.getKey();
                if (target == null) continue;
                totals.merge(target, e.getValue(), Integer::sum);
            }
        }
        for (Map.Entry<IPlayerWW, Integer> e : remoteVotesAgainst.entrySet()) {
            if (e.getKey() == null) continue;
            totals.merge(e.getKey(), e.getValue(), Integer::sum);
        }

        IPlayerWW winner = null;
        int max = 0;
        for (Map.Entry<IPlayerWW, Integer> e : totals.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                winner = e.getKey();
            }
        }

        if (winner == null || max < MIN_VOTES) {
            Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.configurations.buildings.urn_vote_no_result",
                    Formatter.number(sessionNumber)));
            return;
        }

        UrnVoteResultEvent resultEvent = new UrnVoteResultEvent(winner, max, sessionNumber, totals);
        Bukkit.getPluginManager().callEvent(resultEvent);

        if (resultEvent.isCancelled()) {
            return;
        }

        electedPlayers.add(resultEvent.getPlayerWW());
        applySanctions(resultEvent.getPlayerWW(), sessionNumber, resultEvent.getVotes());
    }

    private void applySanctions(IPlayerWW target, int session, int votes) {

        target.modifyHonor(-2);

        if (session >= 2) {
            target.removePlayerMaxHealth(2);
        }

        if (session < 3) {
            ExposedHelper.expose(game, target);
        }

        if (session >= 3) {
            target.getRole().disableAbilities();
            Bukkit.broadcastMessage(game.translate(Prefix.RED, "werewolf.configurations.buildings.urn_vote_result_role",
                    Formatter.player(target.getName()),
                    Formatter.number(votes),
                    Formatter.role(game.translate(target.getRole().getKey()))));
        } else {
            Bukkit.broadcastMessage(game.translate(Prefix.RED, "werewolf.configurations.buildings.urn_vote_result",
                    Formatter.player(target.getName()),
                    Formatter.number(votes)));
        }
    }

    public void fireCastEvent(IPlayerWW voter, IPlayerWW target, boolean blank, IVoteOffice office) {
        UrnVoteCastEvent event = new UrnVoteCastEvent(voter, target, blank, session, office);
        Bukkit.getPluginManager().callEvent(event);
    }
}

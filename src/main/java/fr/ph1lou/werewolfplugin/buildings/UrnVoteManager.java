package fr.ph1lou.werewolfplugin.buildings;

import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import fr.ph1lou.werewolfapi.events.buildings.UrnVoteCastEvent;
import fr.ph1lou.werewolfapi.events.buildings.UrnVoteResultEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.utils.ExposedHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

public class UrnVoteManager {

    private final BuildingsManager buildingsManager;
    private final WereWolfAPI game;

    private boolean active = false;
    private int session = 0;

    private final Set<UUID> votedAgainst = new HashSet<>();
    private final Set<UUID> votedBlankOnce = new HashSet<>();
    private final Map<UUID, Set<Location>> blankVotedUrns = new HashMap<>();

    public UrnVoteManager(BuildingsManager buildingsManager, WereWolfAPI game) {
        this.buildingsManager = buildingsManager;
        this.game = game;
    }

    public boolean isActive() {
        return active;
    }

    public int getSession() {
        return session;
    }

    public boolean hasVotedAgainst(UUID player) {
        return votedAgainst.contains(player);
    }

    public boolean hasVotedBlankOnce(UUID player) {
        return votedBlankOnce.contains(player);
    }

    public boolean hasBlankVotedUrn(UUID player, Location urnLocation) {
        Set<Location> urns = blankVotedUrns.get(player);
        return urns != null && urns.contains(urnLocation);
    }

    public void markVotedAgainst(UUID player) {
        votedAgainst.add(player);
    }

    public void markVotedBlankOnce(UUID player) {
        votedBlankOnce.add(player);
    }

    public void markBlankVotedUrn(UUID player, Location urnLocation) {
        blankVotedUrns.computeIfAbsent(player, k -> new HashSet<>()).add(urnLocation);
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
        for (VoteOffice office : buildingsManager.getVoteOffices()) {
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

        Map<IPlayerWW, Integer> totals = new HashMap<>();
        for (VoteOffice office : buildingsManager.getVoteOffices()) {
            for (Map.Entry<UUID, Integer> e : office.getVotesAgainst().entrySet()) {
                IPlayerWW target = game.getPlayerWW(e.getKey()).orElse(null);
                if (target == null) continue;
                totals.merge(target, e.getValue(), Integer::sum);
            }
        }

        IPlayerWW winner = null;
        int max = 0;
        for (Map.Entry<IPlayerWW, Integer> e : totals.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                winner = e.getKey();
            }
        }

        if (winner == null) {
            Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.configurations.buildings.urn_vote_no_result",
                    Formatter.number(sessionNumber)));
            return;
        }

        UrnVoteResultEvent resultEvent = new UrnVoteResultEvent(winner, max, sessionNumber, totals);
        Bukkit.getPluginManager().callEvent(resultEvent);

        if (resultEvent.isCancelled()) {
            return;
        }

        applySanctions(resultEvent.getPlayerWW(), sessionNumber, resultEvent.getVotes());
    }

    private void applySanctions(IPlayerWW target, int session, int votes) {
        boolean honorActive = game.getConfig().isConfigActive(ConfigBase.HONOR);

        if (honorActive) {
            target.modifyHonor(-2);
        }

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

        game.getAlivePlayersWW().forEach(p -> {
            if (p.equals(target)) {
                return;
            }
            try {
                if (p.getLocation().getWorld() == target.getLocation().getWorld()) {
                    int dist = (int) p.getLocation().distance(target.getLocation());
                    p.sendMessageWithKey(Prefix.YELLOW, "werewolf.configurations.buildings.urn_vote_distance",
                            Formatter.player(target.getName()),
                            Formatter.number((dist / 100) * 100 + 100));
                }
            } catch (Throwable ignored) {
            }
        });
    }

    public void fireCastEvent(IPlayerWW voter, IPlayerWW target, boolean blank, IVoteOffice office) {
        UrnVoteCastEvent event = new UrnVoteCastEvent(voter, target, blank, session, office);
        Bukkit.getPluginManager().callEvent(event);
    }
}

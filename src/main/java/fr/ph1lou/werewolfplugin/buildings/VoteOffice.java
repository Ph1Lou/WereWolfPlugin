package fr.ph1lou.werewolfplugin.buildings;

import fr.ph1lou.werewolfapi.buildings.BuildingType;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class VoteOffice extends Building implements IVoteOffice {

    private final Location urnLocation;
    private final int baseCapacity;
    private int capacity;
    private int votesCast;
    private final Map<UUID, Integer> votesAgainst = new HashMap<>();
    private int blankVotes;
    private final Set<UUID> blankVoters = new HashSet<>();

    public VoteOffice(World world, int baseX, int baseY, int baseZ, Location bannerLocation, Location urnLocation, int capacity) {
        super(BuildingType.VOTE_OFFICE, world, baseX, baseY, baseZ, bannerLocation);
        this.urnLocation = urnLocation;
        this.baseCapacity = capacity;
        this.capacity = capacity;
    }

    public Location getUrnLocation() {
        return urnLocation;
    }

    public int getBaseCapacity() {
        return baseCapacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getVotesCast() {
        return votesCast;
    }

    public int getRemaining() {
        return Math.max(0, capacity - votesCast);
    }

    public boolean isFull() {
        return votesCast >= capacity;
    }

    public void reduceCapacity(int amount) {
        this.capacity = Math.max(1, this.capacity - amount);
        if (this.votesCast > this.capacity) {
            this.votesCast = this.capacity;
        }
    }

    public void resetSession() {
        this.votesCast = 0;
        this.votesAgainst.clear();
        this.blankVoters.clear();
    }

    public boolean hasBlankVoted(UUID player) {
        return blankVoters.contains(player);
    }

    public void recordVoteAgainst(UUID target) {
        votesAgainst.merge(target, 1, Integer::sum);
        votesCast++;
    }

    public void recordBlank(UUID player) {
        blankVotes++;
        blankVoters.add(player);
        votesCast++;
    }

    public Map<UUID, Integer> getVotesAgainst() {
        return votesAgainst;
    }

    public int getBlankVotes() {
        return blankVotes;
    }

    public Set<UUID> getBlankVoters() {
        return blankVoters;
    }
}

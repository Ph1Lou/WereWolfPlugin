package fr.ph1lou.werewolfplugin.buildings;

import fr.ph1lou.werewolfapi.buildings.BuildingType;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VoteOffice extends Building implements IVoteOffice {

    private final Location urnLocation;
    private final Location chestLocation;
    private final int baseCapacity;
    private int capacity;
    private int votesCast;
    private final Map<IPlayerWW, Integer> votesAgainst = new HashMap<>();
    private int blankVotes;
    private final Set<IPlayerWW> blankVoters = new HashSet<>();

    public VoteOffice(World world, int baseX, int baseY, int baseZ, Location bannerLocation, Location urnLocation, Location chestLocation, int capacity) {
        super(BuildingType.VOTE_OFFICE, world, baseX, baseY, baseZ, bannerLocation);
        this.urnLocation = urnLocation;
        this.chestLocation = chestLocation;
        this.baseCapacity = capacity;
        this.capacity = capacity;
    }

    @Override
    public Location getUrnLocation() {
        return urnLocation;
    }

    @Override
    public Location getChestLocation() {
        return chestLocation;
    }

    @Override
    public int getBaseCapacity() {
        return baseCapacity;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public int getVotesCast() {
        return votesCast;
    }

    @Override
    public int getRemaining() {
        return Math.max(0, capacity - votesCast);
    }

    @Override
    public boolean isFull() {
        return votesCast >= capacity;
    }

    @Override
    public void reduceCapacity(int amount) {
        this.capacity = Math.max(1, this.capacity - amount);
        if (this.votesCast > this.capacity) {
            this.votesCast = this.capacity;
        }
    }

    @Override
    public void resetSession() {
        this.votesCast = 0;
        this.votesAgainst.clear();
        this.blankVoters.clear();
    }

    @Override
    public boolean hasBlankVoted(IPlayerWW player) {
        return blankVoters.contains(player);
    }

    @Override
    public void recordVoteAgainst(IPlayerWW target) {
        votesAgainst.merge(target, 1, Integer::sum);
        votesCast++;
    }

    @Override
    public void recordBlank(IPlayerWW player) {
        blankVotes++;
        blankVoters.add(player);
        votesCast++;
    }

    @Override
    public Map<IPlayerWW, Integer> getVotesAgainst() {
        return votesAgainst;
    }

    @Override
    public int getBlankVotes() {
        return blankVotes;
    }
}

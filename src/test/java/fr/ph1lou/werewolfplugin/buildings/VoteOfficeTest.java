package fr.ph1lou.werewolfplugin.buildings;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VoteOfficeTest {

    @Test
    public void resetsBlankBallotsBetweenSessions() {
        VoteOffice office = new VoteOffice(null, 0, 0, 0, null, null, null, 5);

        office.recordBlank(null);
        assertEquals(1, office.getBlankVotes());
        assertEquals(1, office.getVotesCast());

        office.resetSession();
        assertEquals(0, office.getBlankVotes());
        assertEquals(0, office.getVotesCast());

        office.recordBlank(null);
        assertEquals(1, office.getBlankVotes());
        assertEquals(1, office.getVotesCast());
    }
}

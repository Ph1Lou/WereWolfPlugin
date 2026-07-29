package fr.ph1lou.werewolfplugin.guis;

import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.buildings.UrnVoteManager;
import fr.ph1lou.werewolfplugin.buildings.VoteOffice;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;

public final class UrnVoteGuiHelper {

    private UrnVoteGuiHelper() {
    }

    static void recordVoteAgainst(GameManager game, Player player, VoteOffice office, IPlayerWW target) {
        UrnVoteManager urn = game.getBuildingsManager().getUrnVoteManager();

        if (!urn.isActive()) {
            return;
        }
        if (office.isFull()) {
            return;
        }
        if (urn.hasVotedAgainst(player.getUniqueId())) {
            return;
        }

        office.recordVoteAgainst(target.getUUID());
        urn.markVotedAgainst(player.getUniqueId());

        IPlayerWW voter = game.getPlayerWW(player.getUniqueId()).orElse(null);
        if (voter == null) {
            return;
        }

        if (game.getConfig().isConfigActive(ConfigBase.HONOR)) {
            voter.modifyHonor(1);
        }

        urn.fireCastEvent(voter, target, false);

        voter.sendMessageWithKey(Prefix.YELLOW, "werewolf.configurations.buildings.urn_voted_against",
                Formatter.player(target.getName()));
    }

    static void recordBlank(GameManager game, Player player, VoteOffice office, boolean additional) {
        UrnVoteManager urn = game.getBuildingsManager().getUrnVoteManager();

        if (!urn.isActive()) {
            return;
        }
        if (office.isFull()) {
            return;
        }
        if (additional && urn.hasBlankVotedUrn(player.getUniqueId(), office.getUrnLocation())) {
            return;
        }

        office.recordBlank(player.getUniqueId());
        if (additional) {
            urn.markBlankVotedUrn(player.getUniqueId(), office.getUrnLocation());
        } else {
            urn.markVotedBlankOnce(player.getUniqueId());
        }

        IPlayerWW voter = game.getPlayerWW(player.getUniqueId()).orElse(null);
        if (voter == null) {
            return;
        }

        if (game.getConfig().isConfigActive(ConfigBase.HONOR)) {
            voter.modifyHonor(-1);
        }

        urn.fireCastEvent(voter, null, true);

        voter.sendMessageWithKey(Prefix.YELLOW, "werewolf.configurations.buildings.urn_voted_blank");
    }
}

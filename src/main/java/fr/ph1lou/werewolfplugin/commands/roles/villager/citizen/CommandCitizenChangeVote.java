package fr.ph1lou.werewolfplugin.commands.roles.villager.citizen;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.events.roles.citizen.CitizenChangeVoteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Citizen;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.citizen.command_change",
        roleKeys = RoleBase.CITIZEN,
        requiredPower = true,
        argNumbers = 0,
        autoCompletion = false)
public class CommandCitizenChangeVote implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {


        if (!game.getVoteManager().isStatus(VoteStatus.WAITING)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }

        Citizen citizen = (Citizen) playerWW.getRole();

        if (!game.getVoteManager().isStatus(VoteStatus.WAITING)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }


        citizen.setPower(false);
        IPlayerWW voteWW = game.getVoteManager().getPlayerVote(playerWW).orElse(null);

        if(voteWW == null){
            return;
        }

        CitizenChangeVoteEvent cancelVoteEvent = new CitizenChangeVoteEvent(playerWW, voteWW);
        Bukkit.getPluginManager().callEvent(cancelVoteEvent);

        if (cancelVoteEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        game.getVoteManager().getVotedPlayers()
                .forEach(playerWW1 -> game.getVoteManager().setVotes(playerWW1,
                        Math.min(3, game.getVoteManager().getVotes(playerWW1))));

        game.getVoteManager().setVotes(voteWW, 4);

        playerWW.sendMessageWithKey(
                Prefix.YELLOW, "werewolf.roles.citizen.change_vote_perform",
                Formatter.player(voteWW.getName()));
        citizen.addAffectedPlayer(voteWW);
    }
}

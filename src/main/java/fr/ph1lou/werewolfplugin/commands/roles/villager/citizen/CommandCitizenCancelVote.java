package fr.ph1lou.werewolfplugin.commands.roles.villager.citizen;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.events.roles.citizen.CitizenCancelVoteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Citizen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.citizen.command_2",
        roleKeys = RoleBase.CITIZEN,
        requiredPower = true,
        argNumbers = 0,
        autoCompletion = false)
public class CommandCitizenCancelVote implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if(game.getConfig().isConfigActive(ConfigBase.NEW_VOTE)){
            return;
        }

        if (playerWW == null) return;

        if (!game.getVoteManager().isStatus(VoteStatus.WAITING)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }

        Citizen citizen = (Citizen) playerWW.getRole();

        if (!game.getVoteManager().isStatus(VoteStatus.WAITING)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.power");
            return;
        }

        citizen.setPower(false);
        IPlayerWW voteWW = game.getVoteManager().getResult().orElse(null);

        CitizenCancelVoteEvent cancelVoteEvent = new CitizenCancelVoteEvent(playerWW, voteWW);
        Bukkit.getPluginManager().callEvent(cancelVoteEvent);

        if (cancelVoteEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        game.getVoteManager().resetVote();
        Bukkit.broadcastMessage(game.translate(
                Prefix.GREEN , "werewolf.role.citizen.cancelling_broadcast"));

        if (voteWW == null) return;

        playerWW.sendMessageWithKey(
                Prefix.YELLOW , "werewolf.role.citizen.cancelling_vote_perform",
                Formatter.player(voteWW.getName()));
        citizen.addAffectedPlayer(voteWW);


    }
}

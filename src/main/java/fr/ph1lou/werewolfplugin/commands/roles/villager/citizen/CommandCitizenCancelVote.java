package fr.ph1lou.werewolfplugin.commands.roles.villager.citizen;

import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.events.roles.citizen.CitizenCancelVoteEvent;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfplugin.roles.villagers.Citizen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCitizenCancelVote implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if(game.getConfig().isConfigActive(ConfigBase.NEW_VOTE.getKey())){
            return;
        }

        if (playerWW == null) return;

        Citizen citizen = (Citizen) playerWW.getRole();


        if (!game.getVoteManager().isStatus(VoteStatus.WAITING)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.power");
            return;
        }

        citizen.setPower(false);
        IPlayerWW voteWW = game.getVoteManager().getResult().orElse(null);

        CitizenCancelVoteEvent cancelVoteEvent = new  CitizenCancelVoteEvent(playerWW, voteWW);
        Bukkit.getPluginManager().callEvent(cancelVoteEvent);

        if (cancelVoteEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        game.getVoteManager().resetVote();
        Bukkit.broadcastMessage(game.translate(
                Prefix.GREEN.getKey() , "werewolf.role.citizen.cancelling_broadcast"));

        if (voteWW == null) return;

        playerWW.sendMessageWithKey(
                Prefix.YELLOW.getKey() , "werewolf.role.citizen.cancelling_vote_perform",
                Formatter.player(voteWW.getName()));
        citizen.addAffectedPlayer(voteWW);


    }
}

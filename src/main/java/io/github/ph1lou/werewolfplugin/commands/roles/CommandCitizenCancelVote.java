package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.VoteStatus;
import io.github.ph1lou.werewolfapi.events.game.vote.CancelVoteEvent;
import io.github.ph1lou.werewolfplugin.roles.villagers.Citizen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCitizenCancelVote implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        Citizen citizen = (Citizen) playerWW.getRole();

        if (!game.getVote().isStatus(VoteStatus.WAITING_CITIZEN)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.power");
            return;
        }

        citizen.setPower(false);
        IPlayerWW voteWW = game.getVote().getResult();

        CancelVoteEvent cancelVoteEvent = new CancelVoteEvent(playerWW, voteWW);
        Bukkit.getPluginManager().callEvent(cancelVoteEvent);

        if (cancelVoteEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        game.getVote().resetVote();
        Bukkit.broadcastMessage(game.translate(
                Prefix.GREEN.getKey() , "werewolf.role.citizen.cancelling_broadcast"));

        if (voteWW == null) return;

        playerWW.sendMessageWithKey(
                Prefix.YELLOW.getKey() , "werewolf.role.citizen.cancelling_vote_perform",
                Formatter.player(voteWW.getName()));
        citizen.addAffectedPlayer(voteWW);


    }
}

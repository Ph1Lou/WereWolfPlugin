package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.VoteStatus;
import io.github.ph1lou.werewolfapi.events.CancelVoteEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.roles.villagers.Citizen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCitizenCancelVote implements Commands {


    private final Main main;

    public CommandCitizenCancelVote(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Citizen citizen = (Citizen) playerWW.getRole();

        if (!game.getVote().isStatus(VoteStatus.WAITING_CITIZEN)) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        citizen.setPower(false);
        PlayerWW voteWW = game.getVote().getResult();

        CancelVoteEvent cancelVoteEvent = new CancelVoteEvent(playerWW, voteWW);
        Bukkit.getPluginManager().callEvent(cancelVoteEvent);

        if (cancelVoteEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        game.getVote().resetVote();
        Bukkit.broadcastMessage(game.translate(
                "werewolf.role.citizen.cancelling_broadcast"));

        if (voteWW == null) return;

        player.sendMessage(game.translate(
                "werewolf.role.citizen.cancelling_vote_perform",
                voteWW.getName()));
        citizen.addAffectedPlayer(voteWW);


    }
}

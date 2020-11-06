package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.VoteStatus;
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
        PlayerWW plg = game.getPlayersWW().get(uuid);
        Citizen citizen = (Citizen) plg.getRole();

        if (!citizen.hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if (!game.getVote().isStatus(VoteStatus.WAITING_CITIZEN)) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        citizen.setPower(false);
        UUID vote = game.getVote().getResult();
        CancelVoteEvent cancelVoteEvent = new CancelVoteEvent(uuid, vote);
        Bukkit.getPluginManager().callEvent(cancelVoteEvent);
        game.getVote().resetVote();

        if (cancelVoteEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        player.sendMessage(game.translate(
                "werewolf.role.citizen.cancelling_vote_perform",
                game.getPlayersWW().get(vote).getName()));
        citizen.addAffectedPlayer(vote);
        Bukkit.broadcastMessage(game.translate(
                "werewolf.role.citizen.cancelling_broadcast"));
    }
}

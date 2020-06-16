package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Citizen;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.enumlg.VoteStatus;
import io.github.ph1lou.pluginlgapi.events.CancelVoteEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCitizenCancelVote implements Commands {


    private final MainLG main;

    public CommandCitizenCancelVote(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if(!game.playerLG.containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerWW plg = game.playerLG.get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!(plg.getRole().isDisplay("werewolf.role.citizen.display"))){
            player.sendMessage(game.translate("werewolf.check.role",game.translate("werewolf.role.citizen.display")));
            return;
        }

        Citizen citizen = (Citizen) plg.getRole();


        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!citizen.hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if (!game.getVote().isStatus(VoteStatus.WAITING_CITIZEN)) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        citizen.setPower(false);
        UUID vote=game.getVote().getResult();
        Bukkit.getPluginManager().callEvent(new CancelVoteEvent(uuid,vote));
        game.getVote().resetVote();
        sender.sendMessage(game.translate("werewolf.role.citizen.cancelling_vote_perform",game.playerLG.get(vote).getName()));
        citizen.addAffectedPlayer(vote);
        Bukkit.broadcastMessage(game.translate("werewolf.role.citizen.cancelling_broadcast"));
    }
}

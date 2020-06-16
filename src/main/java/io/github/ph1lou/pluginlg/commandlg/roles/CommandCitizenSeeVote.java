package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Citizen;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.enumlg.VoteStatus;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCitizenSeeVote implements Commands {

    private final MainLG main;

    public CommandCitizenSeeVote(MainLG main) {
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

        if(citizen.getUse()>=2) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if (!game.getVote().isStatus(VoteStatus.WAITING_CITIZEN)) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        citizen.setUse(citizen.getUse()+1);
        game.getVote().seeVote((Player) sender);
    }
}

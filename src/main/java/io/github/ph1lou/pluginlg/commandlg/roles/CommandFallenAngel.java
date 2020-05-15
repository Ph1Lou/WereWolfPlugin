package io.github.ph1lou.pluginlg.commandlg.roles;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.Angel;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandFallenAngel extends Commands {


    public CommandFallenAngel(MainLG main) {
        super(main);
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

        PlayerLG plg = game.playerLG.get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!(plg.getRole() instanceof Angel)){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.angel.display")));
            return;
        }

        Angel angel = (Angel) plg.getRole();

        if(!angel.hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        angel.setChoice(RoleLG.FALLEN_ANGEL);
        angel.setPower(false);
        sender.sendMessage(game.translate("werewolf.role.angel.angel_choice_perform",game.translate("werewolf.role.fallen_angel.display")));
    }
}

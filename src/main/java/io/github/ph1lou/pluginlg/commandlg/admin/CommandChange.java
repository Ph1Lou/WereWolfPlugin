package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChange implements Commands {


    private final MainLG main;

    public CommandChange(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.change.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if(!game.isState(StateLG.LOBBY)) {
            game.translate("werewolf.check.game_in_progress");
            return;
        }

        sender.sendMessage(game.translate("werewolf.commands.admin.change.in_progress"));
        if(game.wft!=null){
            game.wft.stop();
            game.wft=null;
        }
        game.deleteMap();
        game.createMap();
        sender.sendMessage(game.translate("werewolf.commands.admin.change.finished"));
    }
}

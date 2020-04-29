package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAdminHelp extends Commands {


    public CommandAdminHelp(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

     GameManager game = main.currentGame;

        TextLG text = game.text;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.help.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }
        sender.sendMessage(text.getText(153));
    }
}

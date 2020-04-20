package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.command.CommandSender;

public class CommandAdminHelp extends Commands {

    final MainLG main;

    public CommandAdminHelp(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.h.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }
        sender.sendMessage(main.text.getText(153));
    }
}

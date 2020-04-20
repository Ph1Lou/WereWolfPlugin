package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.command.CommandSender;

public class CommandHelp extends Commands {

    final MainLG main;

    public CommandHelp(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(main.text.getText(102));
    }
}

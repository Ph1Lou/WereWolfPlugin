package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandConfig extends Commands {

    final MainLG main;

    public CommandConfig(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.config.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }
        if (sender instanceof Player) {
            main.optionlg.toolBar((Player) sender);
        } else sender.sendMessage(main.text.getText(140));
    }
}

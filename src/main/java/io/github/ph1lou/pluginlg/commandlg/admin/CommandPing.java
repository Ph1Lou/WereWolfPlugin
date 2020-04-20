package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.command.CommandSender;

public class CommandPing extends Commands {

    final MainLG main;

    public CommandPing(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.ping.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }

        main.spark.pingBot();
    }
}

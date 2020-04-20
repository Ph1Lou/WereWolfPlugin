package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSend extends Commands {

    final MainLG main;

    public CommandSend(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.send.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(main.text.getText(140));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(String.format(main.text.getText(190), 1));
            return;
        }
        if (main.score.getHost().equals("")) {
            sender.sendMessage(main.text.getText(268));
            return;
        }
        main.spark.setHost(sender.getName(), args[0]);
    }
}

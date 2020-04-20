package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandInfo extends Commands {

    final MainLG main;

    public CommandInfo(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.info.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }

        if (args.length == 0) return;

        StringBuilder sb2 = new StringBuilder();

        for (String w : args) {
            sb2.append(w).append(" ");
        }
        Bukkit.broadcastMessage(String.format(main.text.getText(136), sb2.toString()));
    }
}

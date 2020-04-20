package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.command.CommandSender;

public class CommandHost extends Commands {

    final MainLG main;

    public CommandHost(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("host.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(String.format(main.text.getText(190), 1));
            return;
        }
        sender.sendMessage(main.text.getText(118));
        StringBuilder sb = new StringBuilder();
        for (String w : args) {
            sb.append(w).append(" ");
        }
        main.score.setHost(sb.toString());
    }
}

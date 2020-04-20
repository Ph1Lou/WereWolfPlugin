package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandDisconnected extends Commands {

    final MainLG main;

    public CommandDisconnected(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.disc.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }

        for (String p : main.playerLG.keySet()) {
            PlayerLG plg = main.playerLG.get(p);
            if (plg.isState(State.LIVING) && Bukkit.getPlayer(p) == null) {
                sender.sendMessage(String.format(main.text.getText(167), p, main.score.conversion(main.score.getTimer() - plg.getDeathTime())));
            }
        }
    }
}

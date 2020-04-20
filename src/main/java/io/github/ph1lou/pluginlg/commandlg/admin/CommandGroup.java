package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.Title;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGroup extends Commands {

    final MainLG main;

    public CommandGroup(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.group.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Title.sendTitle(player, 20, 60, 20, main.text.getText(138), String.format(main.text.getText(139), main.score.getGroup()));
        }
        Bukkit.broadcastMessage(String.format(main.text.getText(137), main.score.getGroup()));
    }
}

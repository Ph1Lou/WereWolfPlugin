package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFinalHeal extends Commands {

    final MainLG main;

    public CommandFinalHeal(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.fh.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }
        Bukkit.broadcastMessage(main.text.getText(150));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setHealth(p.getMaxHealth());
            p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1, 20);
        }
    }
}

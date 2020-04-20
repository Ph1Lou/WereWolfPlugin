package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.BorderLG;
import io.github.ph1lou.pluginlg.worldloader.WorldFillTask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandGeneration extends Commands {

    final MainLG main;

    public CommandGeneration(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.generation.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }
        int chunksPerRun = 20;
        if (main.wft == null) {
            main.wft = new WorldFillTask("world", chunksPerRun, main.config.borderValues.get(BorderLG.BORDER_MAX) / 2);
            main.wft.setTaskID(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, main.wft, 1, 1));
            sender.sendMessage(main.text.getText(269));
        } else sender.sendMessage(main.text.getText(11));
    }
}

package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandChat extends Commands {

    final MainLG main;

    public CommandChat(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.chat.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }

        main.config.configValues.put(ToolLG.CHAT, !main.config.configValues.get(ToolLG.CHAT));

        if (main.config.configValues.get(ToolLG.CHAT)) {
            Bukkit.broadcastMessage(main.text.getText(122));
        } else Bukkit.broadcastMessage(main.text.getText(123));
    }
}

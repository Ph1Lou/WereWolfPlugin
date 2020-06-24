package io.github.ph1lou.werewolfplugin.commandlg.admin.ingame;

import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInfo implements Commands {


    private final Main main;

    public CommandInfo(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.info.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (args.length == 0) return;

        StringBuilder sb2 = new StringBuilder();

        for (String w : args) {
            sb2.append(w).append(" ");
        }
        Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.info", sb2.toString()));
    }
}

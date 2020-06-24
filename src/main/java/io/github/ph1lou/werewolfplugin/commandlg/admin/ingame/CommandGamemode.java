package io.github.ph1lou.werewolfplugin.commandlg.admin.ingame;

import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGamemode implements Commands {


    private final Main main;

    public CommandGamemode(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        Player player = (Player) sender;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.gamemode.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (args.length != 1) return;

        try {
            int i = Integer.parseInt(args[0]);
            if (i == 0) {
                i = 1;
            } else if (i == 1) {
                i = 0;
            }
            player.setGameMode(GameMode.values()[i]);
            Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.gamemode", sender.getName(), i));
        }
        catch (NumberFormatException ignored){
        }
    }
}

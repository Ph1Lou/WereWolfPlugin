package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTP implements Commands {


    private final Main main;

    public CommandTP(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        Player player = (Player) sender;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.gamemode.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        Player playerArg1 = Bukkit.getPlayer(args[0]);

        if (args.length == 1) {

            if (playerArg1 == null) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }

            player.teleport(playerArg1);
            Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.teleportation", sender.getName(), args[0]));
        }


        if (args.length != 2) return;

        Player playerArg2 = Bukkit.getPlayer(args[1]);

        if (playerArg2 == null || playerArg1 == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        playerArg1.teleport(playerArg2);
        Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.teleportation", args[0], args[1]));

    }
}

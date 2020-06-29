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

        if (args.length == 1) {
            try {
                if (player.getWorld().equals(Bukkit.getPlayer(args[0]).getWorld())) {
                    player.teleport(Bukkit.getPlayer(args[0]));
                    Bukkit.getConsoleSender().sendMessage(game.translate(game.translate("werewolf.commands.admin.teleportation", sender.getName(), args[0])));
                }
            } catch (Exception ignored) {
            }
        }



        if(args.length!=2) return;

        try{
            if(Bukkit.getPlayer(args[1]).getWorld().equals(Bukkit.getPlayer(args[0]).getWorld())){
                Bukkit.getPlayer(args[0]).teleport(Bukkit.getPlayer(args[1]));
                Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.teleportation",args[0],args[1]));
            }
        }
        catch (Exception ignored) {
        }

    }
}

package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IModerationManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandTP implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        Player playerArg1 = Bukkit.getPlayer(args[0]);
        IModerationManager moderationManager = game.getModerationManager();


        if (args.length == 1) {

            if (args[0].equals("@a")) {

                Bukkit.getOnlinePlayers()
                        .forEach(player1 ->
                                Bukkit.dispatchCommand(player, "a tp " + player1.getName()));
                return;
            }

            if (playerArg1 == null) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }

            player.teleport(playerArg1);
            String message = game.translate("werewolf.commands.admin.teleportation.send",
                    Formatter.format("&player1&",player.getName()),
                    Formatter.format("&player2&",playerArg1.getName()));
            moderationManager.alertHostsAndModerators(message);
            if (!moderationManager.isStaff(uuid)) {
                player.sendMessage(message);
            }
            return;
        }

        if (args[0].equals("@a")) {

            Bukkit.getOnlinePlayers()
                    .forEach(player1 -> Bukkit.dispatchCommand(player, "a tp " + player1.getName() + " " + args[1]));
            return;
        }

        if (args[1].equals("@a")) {

            Bukkit.getOnlinePlayers()
                    .forEach(player1 -> Bukkit.dispatchCommand(player, "a tp " + args[0] + " " + player1.getName()));
            return;
        }

        Player playerArg2 = Bukkit.getPlayer(args[1]);

        if (playerArg2 == null || playerArg1 == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        playerArg1.teleport(playerArg1);
        String message = game.translate("werewolf.commands.admin.teleportation.send",
                Formatter.format("&player1&",playerArg1.getName()),
                Formatter.format("&player2&",playerArg2.getName()));
        moderationManager.alertHostsAndModerators(message);
        if (!moderationManager.isStaff(uuid)) {
            player.sendMessage(message);
        }

    }

}

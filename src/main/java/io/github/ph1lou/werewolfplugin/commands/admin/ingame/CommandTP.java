package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.ModerationManagerAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandTP implements Commands {


    private final Main main;

    public CommandTP(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        Player playerArg1 = Bukkit.getPlayer(args[0]);
        ModerationManagerAPI moderationManager = game.getModerationManager();


        if (args.length == 1) {

            if (args[0].equals("@a")) {

                Bukkit.getOnlinePlayers()
                        .forEach(player1 ->
                                Bukkit.dispatchCommand(player, "a tp " + player1.getName()));
                return;
            }

            if (playerArg1 == null) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                Bukkit.broadcastMessage(args[0]);
                return;
            }

            player.teleport(playerArg1);

            moderationManager.alertHostsAndModerators(game.translate("werewolf.commands.admin.teleportation.send", player.getName(), playerArg1.getName()));

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


        moderationManager.alertHostsAndModerators(game.translate("werewolf.commands.admin.teleportation.send", playerArg1.getName(), playerArg2.getName()));


    }

}

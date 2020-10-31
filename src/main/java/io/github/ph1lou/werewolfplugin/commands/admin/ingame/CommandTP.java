package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandTP implements Commands {


    private final Main main;

    public CommandTP(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (args[0].equals("@a")) {

            Bukkit.getOnlinePlayers()
                    .forEach(player1 -> {
                        args[0] = player1.getName();
                        Bukkit.dispatchCommand(player, "a tp " + Arrays.stream(args).reduce(String::concat));
                    });
            return;
        }

        Player playerArg1 = Bukkit.getPlayer(args[0]);

        if (args.length == 1) {


            if (playerArg1 == null) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                Bukkit.broadcastMessage(args[0]);
                return;
            }

            player.teleport(playerArg1);
            Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.teleportation.send", player.getName(), playerArg1.getName()));

            return;
        }

        if (args[1].equals("@a")) {

            Bukkit.getOnlinePlayers()
                    .forEach(player1 -> {
                        args[1] = player1.getName();
                        Bukkit.dispatchCommand(player, "a tp " + Arrays.stream(args).reduce(String::concat));
                    });
            return;
        }

        Player playerArg2 = Bukkit.getPlayer(args[1]);

        if (playerArg2 == null || playerArg1 == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        playerArg1.teleport(playerArg2);
        Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.teleportation.send", playerArg1.getName(), playerArg2.getName()));

    }
}

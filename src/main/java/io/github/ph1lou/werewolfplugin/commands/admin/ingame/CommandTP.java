package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandTP implements Commands {


    private final Main main;

    public CommandTP(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        Player playerArg1 = Bukkit.getPlayer(args[0]);

        if (args.length == 1) {

            if (playerArg1 == null) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }

            player.teleport(playerArg1);
            Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.teleportation", player.getName(), playerArg1.getName()));
        }


        if (args.length != 2) return;

        Player playerArg2 = Bukkit.getPlayer(args[1]);

        if (playerArg2 == null || playerArg1 == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        playerArg1.teleport(playerArg2);
        Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.teleportation", playerArg1.getName(), playerArg2.getName()));

    }
}

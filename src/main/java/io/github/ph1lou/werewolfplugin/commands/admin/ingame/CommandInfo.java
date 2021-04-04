package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandInfo implements ICommands {


    private final Main main;

    public CommandInfo(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (args.length == 0) {
            player.sendMessage(game.translate("werewolf.check.parameters", 1));
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (String w : args) {
            sb.append(w).append(" ");
        }
        Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.info.send", ChatColor.translateAlternateColorCodes('&', sb.toString())));
    }
}

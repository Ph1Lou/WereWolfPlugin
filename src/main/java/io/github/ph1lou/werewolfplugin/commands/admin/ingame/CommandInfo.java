package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandInfo implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

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

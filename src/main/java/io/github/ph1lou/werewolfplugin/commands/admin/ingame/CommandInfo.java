package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandInfo implements Commands {


    private final Main main;

    public CommandInfo(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        if (args.length == 0) return;

        StringBuilder sb = new StringBuilder();

        for (String w : args) {
            sb.append(w).append(" ");
        }
        Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.info", ChatColor.translateAlternateColorCodes('&', sb.toString())));
    }
}

package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class CommandGamemode implements Commands {


    private final Main main;

    public CommandGamemode(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        if (args.length != 1) return;

        try {
            int i = Integer.parseInt(args[0]);
            if (i == 0) {
                i = 1;
            } else if (i == 1) {
                i = 0;
            }
            player.setGameMode(GameMode.values()[i]);
            Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.gamemode", player.getName(), i));
        }
        catch (NumberFormatException ignored){
        }
    }
}

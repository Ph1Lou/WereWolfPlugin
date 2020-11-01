package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class CommandGamemode implements Commands {


    private final Main main;

    public CommandGamemode(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        try {
            int i = Integer.parseInt(args[0]);
            if (i == 0) {
                i = 1;
            } else if (i == 1) {
                i = 0;
            }
            player.setGameMode(GameMode.values()[i]);
            game.getModerationManager().alertHostsAndModerators(game.translate("werewolf.commands.admin.gamemode.send", player.getName(), i));
        }
        catch (NumberFormatException ignored){
        }
    }
}

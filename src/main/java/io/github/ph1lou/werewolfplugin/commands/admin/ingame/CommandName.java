package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;

public class CommandName implements Commands {


    private final Main main;

    public CommandName(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {


        GameManager game = main.getCurrentGame();

        if (args.length == 0) {
            player.sendMessage(game.translate("werewolf.check.parameters", 1));
            return;
        }
        player.sendMessage(game.translate("werewolf.commands.admin.set_game_name"));
        StringBuilder sb = new StringBuilder();
        for (String w : args) {
            sb.append(w).append(" ");
        }
        game.setGameName(sb.toString());
    }
}

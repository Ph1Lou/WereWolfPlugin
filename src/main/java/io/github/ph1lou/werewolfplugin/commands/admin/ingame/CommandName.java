package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.entity.Player;

public class CommandName implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (args.length == 0) {
            player.sendMessage(game.translate("werewolf.check.parameters", 1));
            return;
        }

        player.sendMessage(game.translate("werewolf.commands.admin.set_game_name.send"));
        StringBuilder sb = new StringBuilder();
        for (String w : args) {
            sb.append(w).append(" ");
        }
        game.setGameName(sb.toString());
    }
}

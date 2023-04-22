package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.set_game_name.command",
        descriptionKey = "werewolf.commands.admin.set_game_name.description"
)
public class CommandName implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (args.length == 0) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.parameters",
                    Formatter.number(1)));
            return;
        }

        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.set_game_name.send"));
        StringBuilder sb = new StringBuilder();
        for (String w : args) {
            sb.append(w).append(" ");
        }
        game.setGameName(sb.toString());
    }
}

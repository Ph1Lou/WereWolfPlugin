package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandVote extends Commands {


    public CommandVote(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)){
            return;
        }

     GameManager game = main.currentGame;

        TextLG text = game.text;

        if (args.length != 1) {
            sender.sendMessage(text.getText(54));
            return;
        }
        if(!game.playerLG.containsKey(sender.getName())) {
            sender.sendMessage(text.getText(67));
            return;
        }
        game.vote.setUnVote((Player) sender, args[0]);
    }
}

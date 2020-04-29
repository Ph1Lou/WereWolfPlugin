package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTimer extends Commands {


    public CommandTimer(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)){
            return;
        }

     GameManager game = main.currentGame;

        TextLG text = game.text;

        for (TimerLG timer : TimerLG.values()) {
            String time = game.score.conversion(game.config.timerValues.get(timer));
            if (time.charAt(0) != '-') {
                sender.sendMessage(String.format(text.translateTimer.get(timer), time));
            }
        }
    }
}

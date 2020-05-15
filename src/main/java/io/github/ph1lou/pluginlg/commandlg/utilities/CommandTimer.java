package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.command.CommandSender;

public class CommandTimer extends Commands {


    public CommandTimer(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.currentGame;

        for (TimerLG timer : TimerLG.values()) {
            String time = game.score.conversion(game.config.getTimerValues().get(timer));
            if (time.charAt(0) != '-') {
                sender.sendMessage(game.translate(timer.getKey(), time));
            }
        }
    }
}

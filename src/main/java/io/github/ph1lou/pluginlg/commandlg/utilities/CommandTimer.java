package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.command.CommandSender;

public class CommandTimer implements Commands {


    private final MainLG main;

    public CommandTimer(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.currentGame;

        for (TimerLG timer : TimerLG.values()) {
            String time = game.score.conversion(game.getConfig().getTimerValues().get(timer));
            if (time.charAt(0) != '-') {
                if(timer.equals(TimerLG.ROLE_DURATION)){
                    if(game.getConfig().isTrollSV()){
                        sender.sendMessage(game.translate(timer.getKey(), game.score.conversion(game.getConfig().getTimerValues().get(timer)-120)));
                    }
                    else sender.sendMessage(game.translate(timer.getKey(), time));
                }
                else sender.sendMessage(game.translate(timer.getKey(), time));
            }
        }
    }
}

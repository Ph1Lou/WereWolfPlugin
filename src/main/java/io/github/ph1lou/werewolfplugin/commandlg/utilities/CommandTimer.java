package io.github.ph1lou.werewolfplugin.commandlg.utilities;

import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.command.CommandSender;

public class CommandTimer implements Commands {


    private final Main main;

    public CommandTimer(Main main) {
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

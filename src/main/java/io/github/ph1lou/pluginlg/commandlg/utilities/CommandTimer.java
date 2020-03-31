package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import org.bukkit.command.CommandSender;

public class CommandTimer extends Commands {

    final MainLG main;

    public CommandTimer(MainLG main, String name) {
        super(name);
        this.main=main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (TimerLG timer : TimerLG.values()) {
            String time = main.score.conversion(main.config.value.get(timer));
            if (time.charAt(0) != '-') {
                sender.sendMessage(String.format(main.text.translateTimer.get(timer), time));
            }
        }
    }
}

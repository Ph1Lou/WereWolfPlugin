package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.TimerRegister;
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


        GameManager game = main.getCurrentGame();

        for (TimerRegister timer:main.getRegisterTimers()) {
            String time = game.getScore().conversion(game.getConfig().getTimerValues().get(timer.getKey()));
            if (time.charAt(0) != '-') {
                sender.sendMessage(game.translate(timer.getKey(), time));
            }
        }
    }
}

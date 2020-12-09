package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.registers.TimerRegister;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;

public class CommandTimer implements Commands {


    private final Main main;

    public CommandTimer(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {


        WereWolfAPI game = main.getWereWolfAPI();

        for (TimerRegister timer : main.getRegisterManager().getTimersRegister()) {
            String time = game.getScore().conversion(game.getConfig().getTimerValues().get(timer.getKey()));
            if (time.charAt(0) != '-') {
                player.sendMessage(game.translate(timer.getKey(), time));
            }
        }
    }
}

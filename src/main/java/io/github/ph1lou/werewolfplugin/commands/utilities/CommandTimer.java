package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.registers.TimerRegister;
import io.github.ph1lou.werewolfapi.utils.Utils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.RegisterManager;
import org.bukkit.entity.Player;

public class CommandTimer implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        for (TimerRegister timer : RegisterManager.get().getTimersRegister()) {
            String time = Utils.conversion(game.getConfig().getTimerValue(timer.getKey()));
            if (time.charAt(0) != '-') {
                player.sendMessage(game.translate(timer.getKey(), time));
            }
        }
    }
}

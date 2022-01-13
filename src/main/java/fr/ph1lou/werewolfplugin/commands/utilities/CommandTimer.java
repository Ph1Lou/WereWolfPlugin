package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.registers.impl.TimerRegister;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.RegisterManager;
import org.bukkit.entity.Player;

public class CommandTimer implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        for (TimerRegister timer : RegisterManager.get().getTimersRegister()) {
            String time = Utils.conversion(game.getConfig().getTimerValue(timer.getKey()));
            if (time.charAt(0) != '-') {
                player.sendMessage(game.translate(timer.getKey(),
                        Formatter.timer(time)));
            }
        }
    }
}

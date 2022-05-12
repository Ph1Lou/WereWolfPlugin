package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.menu.timers.command",
        descriptionKey = "werewolf.menu.timers.description",
        argNumbers = 0)
public class CommandTimer implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        for (Wrapper<?, Timer> timer : Register.get().getTimersRegister()) {
            String time = Utils.conversion(game.getConfig().getTimerValue(timer.getMetaDatas().key()));
            if (time.charAt(0) != '-') {
                player.sendMessage(game.translate(timer.getMetaDatas().key(),
                        Formatter.timer(time)));
            }
        }
    }
}

package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.registers.impl.RandomEventRegister;
import fr.ph1lou.werewolfplugin.RegisterManager;
import org.bukkit.entity.Player;

public class CommandRandomEvents implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getConfig().isConfigActive(ConfigBase.HIDE_EVENTS.getKey())) {

            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.menu.random_events.disable"));

            return;
        }

        StringBuilder sb = new StringBuilder(game.translate(Prefix.GREEN.getKey() , "werewolf.menu.random_events.list"));

        for (RandomEventRegister randomEventRegister : RegisterManager.get().getRandomEventsRegister()) {

            if (game.getConfig().getProbability(randomEventRegister.getKey()) > 0) {
                sb.append(game.translate("werewolf.menu.random_events.command_message",
                        Formatter.format("&event&",game.translate(randomEventRegister.getKey())),
                                Formatter.number(game.getConfig().getProbability(randomEventRegister.getKey()))))
                        .append(", ");
            }
        }

        player.sendMessage(sb.toString());
    }
}

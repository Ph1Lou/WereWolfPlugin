package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigBase;
import io.github.ph1lou.werewolfapi.registers.RandomEventRegister;
import io.github.ph1lou.werewolfplugin.RegisterManager;
import org.bukkit.entity.Player;

public class CommandRandomEvents implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getConfig().isConfigActive(ConfigBase.HIDE_EVENTS.getKey())) {

            player.sendMessage(game.translate("werewolf.menu.random_events.disable"));

            return;
        }

        StringBuilder sb = new StringBuilder(game.translate("werewolf.menu.random_events.list"));

        for (RandomEventRegister randomEventRegister : RegisterManager.get().getRandomEventsRegister()) {

            if (game.getConfig().getProbability(randomEventRegister.getKey()) > 0) {
                sb.append(game.translate("werewolf.menu.random_events.command_message",
                        game.translate(randomEventRegister.getKey()),
                        game.getConfig().getProbability(randomEventRegister.getKey()))).append(", ");
            }
        }

        player.sendMessage(sb.toString());
    }
}

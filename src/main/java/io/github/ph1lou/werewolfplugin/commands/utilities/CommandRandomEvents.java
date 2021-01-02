package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.registers.RandomEventRegister;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;

public class CommandRandomEvents implements Commands {


    private final Main main;

    public CommandRandomEvents(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        StringBuilder sb = new StringBuilder(game.translate("werewolf.menu.random_events.list"));

        for (RandomEventRegister randomEventRegister : main.getRegisterManager().getRandomEventsRegister()) {

            if (game.getConfig().getProbability(randomEventRegister.getKey()) > 0) {
                sb.append(game.translate("werewolf.menu.random_events.command_message", game.translate(randomEventRegister.getKey()), game.getConfig().getProbability(randomEventRegister.getKey()))).append(", ");

            }
        }

        player.sendMessage(sb.toString());
    }
}

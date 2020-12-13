package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.registers.ConfigRegister;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;

public class CommandRules implements Commands {


    private final Main main;

    public CommandRules(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        for (ConfigRegister configRegister : main.getRegisterManager().getConfigsRegister()) {

            if (configRegister.isAppearInMenu()) {
                if (game.getConfig().isConfigActive(configRegister.getKey())) {
                    player.sendMessage(game.translate("werewolf.utils.enable", game.translate(configRegister.getKey())));
                } else {
                    player.sendMessage(game.translate("werewolf.utils.disable", game.translate(configRegister.getKey())));
                }
            }

        }
    }
}

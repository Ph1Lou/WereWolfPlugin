package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.ConfigRegister;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.command.CommandSender;

public class CommandRules implements Commands {


    private final Main main;

    public CommandRules(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();

        for (ConfigRegister ConfigRegister:main.getRegisterConfigs()) {
            if (game.getConfig().getConfigValues().get(ConfigRegister.getKey())) {
                sender.sendMessage(game.translate("werewolf.utils.enable", game.translate(ConfigRegister.getKey())));
            } else sender.sendMessage(game.translate("werewolf.utils.disable", game.translate(ConfigRegister.getKey())));
        }
    }
}

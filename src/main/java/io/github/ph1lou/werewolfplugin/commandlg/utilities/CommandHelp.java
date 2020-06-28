package io.github.ph1lou.werewolfplugin.commandlg.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.command.CommandSender;

public class CommandHelp implements Commands {


    private final Main main;

    public CommandHelp(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        sender.sendMessage(game.translate("werewolf.commands.help"));
    }
}

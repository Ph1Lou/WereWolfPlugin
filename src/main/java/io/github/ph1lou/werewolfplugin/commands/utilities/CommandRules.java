package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.ToolLG;
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
        
        for (ToolLG tool : ToolLG.values()) {

            if (game.getConfig().getConfigValues().get(tool)) {
                sender.sendMessage(game.translate("werewolf.utils.enable", game.translate(tool.getKey())));
            } else sender.sendMessage(game.translate("werewolf.utils.disable", game.translate(tool.getKey())));
        }
    }
}

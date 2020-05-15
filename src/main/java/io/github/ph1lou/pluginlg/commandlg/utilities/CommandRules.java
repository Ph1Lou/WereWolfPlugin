package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import org.bukkit.command.CommandSender;

public class CommandRules extends Commands {


    public CommandRules(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;
        
        for (ToolLG tool : ToolLG.values()) {

            if (game.config.getConfigValues().get(tool)) {
                sender.sendMessage(game.translate("werewolf.utils.enable", game.translate(tool.getKey())));
            } else sender.sendMessage(game.translate("werewolf.utils.disable", game.translate(tool.getKey())));
        }
    }
}

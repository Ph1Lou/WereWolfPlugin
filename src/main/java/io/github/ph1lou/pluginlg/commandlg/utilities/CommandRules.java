package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.command.CommandSender;

public class CommandRules extends Commands {

    final MainLG main;

    public CommandRules(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (ToolLG tool : ToolLG.values()) {

            if (main.config.configValues.get(tool)) {
                sender.sendMessage(String.format(main.text.getText(169), main.text.translateTool.get(tool)));
            } else sender.sendMessage(String.format(main.text.getText(168), main.text.translateTool.get(tool)));
        }
    }
}

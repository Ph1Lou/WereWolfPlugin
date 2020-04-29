package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandScenarios extends Commands {


    public CommandScenarios(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)){
            return;
        }

     GameManager game = main.currentGame;

        TextLG text = game.text;

        for (ScenarioLG scenario : ScenarioLG.values()) {
            if (game.config.scenarioValues.get(scenario)) {
                sender.sendMessage(String.format(text.getText(169), text.translateScenario.get(scenario)));
            } else sender.sendMessage(String.format(text.getText(168), text.translateScenario.get(scenario)));
        }
    }
}

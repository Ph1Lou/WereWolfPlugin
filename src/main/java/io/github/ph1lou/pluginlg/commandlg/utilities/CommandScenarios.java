package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import org.bukkit.command.CommandSender;

public class CommandScenarios extends Commands {

    MainLG main;

    public CommandScenarios(MainLG main, String name) {
        super(name);
        this.main=main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        for (ScenarioLG scenario : ScenarioLG.values()) {
            if (main.config.scenario.get(scenario)) {
                sender.sendMessage(String.format(main.text.getText(169), main.text.translateScenario.get(scenario)));
            }
            else sender.sendMessage(String.format(main.text.getText(168), main.text.translateScenario.get(scenario)));
        }
    }
}

package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import org.bukkit.command.CommandSender;

public class CommandScenarios implements Commands {


    private final MainLG main;

    public CommandScenarios(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        for (ScenarioLG scenario : ScenarioLG.values()) {
            if (game.getConfig().getScenarioValues().get(scenario)) {
                sender.sendMessage(game.translate("werewolf.utils.enable", game.translate(scenario.getKey())));
            } else sender.sendMessage(game.translate("werewolf.utils.disable", game.translate(scenario.getKey())));
        }
    }
}

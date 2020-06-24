package io.github.ph1lou.werewolfplugin.commandlg.utilities;

import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.command.CommandSender;

public class CommandScenarios implements Commands {


    private final Main main;

    public CommandScenarios(Main main) {
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

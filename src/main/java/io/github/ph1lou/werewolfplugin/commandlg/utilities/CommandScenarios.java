package io.github.ph1lou.werewolfplugin.commandlg.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.ScenarioRegister;
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

        for (ScenarioRegister scenarioRegister:main.getRegisterScenarios()) {
            if (game.getConfig().getScenarioValues().get(scenarioRegister.getKey())) {
                sender.sendMessage(game.translate("werewolf.utils.enable", game.translate(scenarioRegister.getKey())));
            } else sender.sendMessage(game.translate("werewolf.utils.disable", game.translate(scenarioRegister.getKey())));
        }
    }
}

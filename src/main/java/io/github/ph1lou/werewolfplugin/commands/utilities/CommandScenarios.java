package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.ScenarioRegister;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;

public class CommandScenarios implements Commands {


    private final Main main;

    public CommandScenarios(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        for (ScenarioRegister scenarioRegister : main.getRegisterScenarios()) {
            if (game.getConfig().getScenarioValues().get(scenarioRegister.getKey())) {
                player.sendMessage(game.translate("werewolf.utils.enable", game.translate(scenarioRegister.getKey())));
            } else
                player.sendMessage(game.translate("werewolf.utils.disable", game.translate(scenarioRegister.getKey())));
        }
    }
}

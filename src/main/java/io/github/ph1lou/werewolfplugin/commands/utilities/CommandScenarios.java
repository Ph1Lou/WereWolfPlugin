package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.registers.ScenarioRegister;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;

public class CommandScenarios implements ICommands {


    private final Main main;

    public CommandScenarios(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (game.getConfig().isConfigActive(ConfigsBase.HIDE_SCENARIOS.getKey())) {

            player.sendMessage(game.translate("werewolf.menu.scenarios.disable"));

            return;
        }

        StringBuilder sb = new StringBuilder(game.translate("werewolf.menu.scenarios.list"));
        int i = 0;
        for (ScenarioRegister scenarioRegister : main.getRegisterManager().getScenariosRegister()) {
            if (game.getConfig().isScenarioActive(scenarioRegister.getKey())) {
                sb.append(i % 2 == 0 ? "§b" : "").append(game.translate(scenarioRegister.getKey())).append("§f, ");
                i++;
            }
        }

        player.sendMessage(sb.toString());
    }
}

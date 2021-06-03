package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigBase;
import io.github.ph1lou.werewolfapi.registers.ScenarioRegister;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.RegisterManager;
import org.bukkit.entity.Player;

public class CommandScenarios implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getConfig().isConfigActive(ConfigBase.HIDE_SCENARIOS.getKey())) {

            player.sendMessage(game.translate("werewolf.menu.scenarios.disable"));

            return;
        }

        StringBuilder sb = new StringBuilder(game.translate("werewolf.menu.scenarios.list"));
        int i = 0;
        for (ScenarioRegister scenarioRegister : RegisterManager.get().getScenariosRegister()) {
            if (game.getConfig().isScenarioActive(scenarioRegister.getKey())) {
                sb.append(i % 2 == 0 ? "§b" : "").append(game.translate(scenarioRegister.getKey())).append("§f, ");
                i++;
            }
        }

        player.sendMessage(sb.toString());
    }
}

package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.registers.impl.ScenarioRegister;
import fr.ph1lou.werewolfplugin.RegisterManager;
import org.bukkit.entity.Player;

public class CommandScenarios implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getConfig().isConfigActive(ConfigBase.HIDE_SCENARIOS.getKey())) {

            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.menu.scenarios.disable"));

            return;
        }

        StringBuilder sb = new StringBuilder(game.translate(Prefix.GREEN.getKey() , "werewolf.menu.scenarios.list"));
        int i = 0;
        for (ScenarioRegister scenarioRegister : RegisterManager.get().getScenariosRegister()) {
            if (game.getConfig().isScenarioActive(scenarioRegister.getKey())) {
                sb.append(i % 2 == 0 ? "§b" : "")
                        .append(game.translate(scenarioRegister.getKey()))
                        .append("§f, ");
                i++;
            }
        }

        player.sendMessage(sb.toString());
    }
}

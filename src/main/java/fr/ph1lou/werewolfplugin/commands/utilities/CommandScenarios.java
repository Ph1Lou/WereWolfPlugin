package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.Command;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.registers.impl.ScenarioRegister;
import fr.ph1lou.werewolfplugin.RegisterManager;
import org.bukkit.entity.Player;

@Command(key = "werewolf.menu.scenarios.command",
        descriptionKey = "werewolf.menu.scenarios.description",
        argNumbers = 0)
public class CommandScenarios implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getConfig().isConfigActive(ConfigBase.HIDE_SCENARIOS)) {

            player.sendMessage(game.translate(Prefix.RED , "werewolf.menu.scenarios.disable"));

            return;
        }

        StringBuilder sb = new StringBuilder(game.translate(Prefix.GREEN , "werewolf.menu.scenarios.list"));
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

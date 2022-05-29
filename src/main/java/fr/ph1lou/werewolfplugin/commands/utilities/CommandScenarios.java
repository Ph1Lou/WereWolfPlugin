package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.scenarios.command",
        descriptionKey = "werewolf.commands.player.scenarios.description",
        argNumbers = 0)
public class CommandScenarios implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getConfig().isConfigActive(ConfigBase.HIDE_SCENARIOS)) {

            player.sendMessage(game.translate(Prefix.RED , "werewolf.commands.player.scenarios.disable"));

            return;
        }

        player.sendMessage(game.translate(Prefix.GREEN , "werewolf.commands.player.scenarios.list"));
        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (Wrapper<ListenerManager, Scenario> scenarioRegister : Register.get().getScenariosRegister()) {
            if (game.getConfig().isScenarioActive(scenarioRegister.getMetaDatas().key())) {
                sb.append(i % 2 == 0 ? "§b" : "")
                        .append(game.translate(scenarioRegister.getMetaDatas().key()))
                        .append("§f, ");
                i++;
            }
        }

        player.sendMessage(sb.toString());
    }
}

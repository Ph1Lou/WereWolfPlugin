package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@PlayerCommand(key = "werewolf.commands.player.configurations.command",
        descriptionKey = "werewolf.commands.player.configurations.description",
        argNumbers = 0)
public class CommandConfigurations implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(game.translate(Prefix.ORANGE, "werewolf.commands.player.configurations.list"));

        String message = Register.get().getConfigsRegister()
                .stream()
                .map(Wrapper::getMetaDatas)
                .map(Configuration::config)
                .filter(ConfigurationBasic::appearInMenu)
                .filter(ConfigurationBasic::appearInConfigurationList)
                .filter(configurationWrapper -> game.getConfig().isConfigActive(configurationWrapper.key()) &&
                                                this.hideCompositionCondition(game, configurationWrapper.key()))
                .map(configurationWrapper -> "§a-§f " + game.translate(configurationWrapper.key()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.joining("\n"));

        player.sendMessage(message);
    }

    private boolean hideCompositionCondition(WereWolfAPI game, String key) {
        return !key.equals(ConfigBase.LONE_WOLF)
               || !game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION);
    }
}

package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@PlayerCommand(key = "werewolf.commands.player.configurations_roles.command",
        descriptionKey = "werewolf.commands.player.configurations_roles.description",
        argNumbers = 0)
public class CommandConfigurationsRoles implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION)) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.player.compo.composition_hide"));
            return;
        }

        player.sendMessage(game.translate(Prefix.ORANGE, "werewolf.commands.player.configurations_roles.list"));

        String message = Stream.concat(Register.get().getRolesRegister().stream().map(Wrapper::getMetaDatas)
                                .filter(role -> game.getConfig().getRoleCount(role.key()) > 0)
                                .flatMap(role -> Stream.of(role.configurations()))
                                .map(Configuration::config)
                                .filter(ConfigurationBasic::appearInMenu)
                                .filter(ConfigurationBasic::appearInConfigurationList)
                                .filter(configurationWrapper -> game.getConfig().isConfigActive(configurationWrapper.key()))
                                .map(configurationWrapper -> "§a-§f " + game.translate(configurationWrapper.key())),
                        Register.get().getRolesRegister()
                                .stream()
                                .map(Wrapper::getMetaDatas)
                                .filter(role -> game.getConfig().getRoleCount(role.key()) > 0)
                                .flatMap(role -> Stream.of(role.configValues()))
                                .map(intValue -> "§a-§f " + game.translate(intValue.key(),
                                        Formatter.number(game.getConfig().getValue(intValue.key())))))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.joining("\n"));

        player.sendMessage(message);
    }
}

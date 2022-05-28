package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@PlayerCommand(key = "werewolf.menu.global.command",
        descriptionKey = "werewolf.menu.global.description",
        argNumbers = 0)
public class CommandRules implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(game.translate(Prefix.ORANGE,"werewolf.menu.global.list"));

        String message = Stream.concat(Register.get().getConfigsRegister()
                                .stream()
                                .map(Wrapper::getMetaDatas),
                        Register.get().getRolesRegister().stream().map(Wrapper::getMetaDatas)
                                .filter(role -> !game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION))
                                .filter(role -> game.getConfig().getRoleCount(role.key()) > 0)
                                .flatMap(role -> Stream.of(role.configurations())))
                .sorted((o1, o2) -> game.translate(o1.key())
                        .compareToIgnoreCase(game.translate(o2.key())))
                .sorted((o1, o2) -> {
                    boolean active1 = game.getConfig().isConfigActive(o1.key());
                    boolean active2 = game.getConfig().isConfigActive(o2.key());

                    if(active1 && active2){
                        return 0;
                    }
                    if(active1){
                        return -1;
                    }
                    if(active2){
                        return 1;
                    }
                    return 0;
                })
                .filter(Configuration::appearInMenu)
                .filter(Configuration::appearInConfigurationList)
                .filter(configurationWrapper -> game.getConfig().isConfigActive(configurationWrapper.key()) &&
                        this.hideCompositionCondition(game, configurationWrapper.key()))
                .map(configurationWrapper -> "§a-§f " + game.translate(configurationWrapper.key()))
                .collect(Collectors.joining("\n"));

        player.sendMessage(message);
    }

    private boolean hideCompositionCondition(WereWolfAPI game, String key) {
        return !key.equals(ConfigBase.LONE_WOLF)
                || !game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION);
    }
}

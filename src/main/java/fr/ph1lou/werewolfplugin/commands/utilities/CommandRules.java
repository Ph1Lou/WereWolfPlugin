package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@PlayerCommand(key = "werewolf.menu.global.command",
        descriptionKey = "werewolf.menu.global.description",
        argNumbers = 0)
public class CommandRules implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(game.translate(Prefix.ORANGE,"werewolf.menu.global.list"));

        String message = Register.get().getConfigsRegister()
                .stream()
                .sorted((o1, o2) -> game.translate(o1.getMetaDatas().key())
                        .compareToIgnoreCase(game.translate(o2.getMetaDatas().key())))
                .sorted((o1, o2) -> {
                    boolean active1 = game.getConfig().isConfigActive(o1.getMetaDatas().key());
                    boolean active2 = game.getConfig().isConfigActive(o2.getMetaDatas().key());

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
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().appearInMenu())
                .map(configurationWrapper -> {
                    if (game.getConfig().isConfigActive(configurationWrapper.getMetaDatas().key())) {
                        return "- " + game.translate(configurationWrapper.getMetaDatas().key()) +
                                " " +
                                game.translate("werewolf.utils.check");
                    } else {
                        return "- " + game.translate(configurationWrapper.getMetaDatas().key()) +
                                " " +
                                game.translate("werewolf.utils.cross");
                    }
                })
                .collect(Collectors.joining("\n"));

        player.sendMessage(message);
    }
}

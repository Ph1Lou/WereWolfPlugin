package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.Command;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.registers.impl.ConfigRegister;
import fr.ph1lou.werewolfplugin.RegisterManager;
import org.bukkit.entity.Player;

@Command(key = "werewolf.menu.global.command",
        descriptionKey = "werewolf.menu.global.description",
        argNumbers = 0)
public class CommandRules implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        for (ConfigRegister configRegister : RegisterManager.get().getConfigsRegister()) {

            if (configRegister.isAppearInMenu()) {
                if (game.getConfig().isConfigActive(configRegister.getKey())) {
                    player.sendMessage(game.translate("werewolf.utils.enable") +
                            game.translate(configRegister.getKey()));
                } else {
                    player.sendMessage(game.translate("werewolf.utils.disable") +
                            game.translate(configRegister.getKey()));
                }
            }

        }
    }
}

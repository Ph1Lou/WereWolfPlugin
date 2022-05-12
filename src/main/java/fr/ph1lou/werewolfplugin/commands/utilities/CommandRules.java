package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.menu.global.command",
        descriptionKey = "werewolf.menu.global.description",
        argNumbers = 0)
public class CommandRules implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        for (Wrapper<?, Configuration> configRegister : Register.get().getConfigsRegister()) {

            if (configRegister.getMetaDatas().appearInMenu()) {
                if (game.getConfig().isConfigActive(configRegister.getMetaDatas().key())) {
                    player.sendMessage(game.translate("werewolf.utils.enable") +
                            game.translate(configRegister.getMetaDatas().key()));
                } else {
                    player.sendMessage(game.translate("werewolf.utils.disable") +
                            game.translate(configRegister.getMetaDatas().key()));
                }
            }

        }
    }
}

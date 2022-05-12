package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.chat.command",
        descriptionKey = "werewolf.commands.admin.chat.description",
        moderatorAccess = true,
        argNumbers = 0)
public class CommandChat implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        game.getConfig().switchConfigValue(ConfigBase.CHAT);

        Bukkit.broadcastMessage(game.getConfig().isConfigActive(ConfigBase.CHAT) ?
                game.translate(Prefix.GREEN , "werewolf.commands.admin.chat.on") :
                game.translate(Prefix.RED , "werewolf.commands.admin.chat.off"));
    }
}

package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.enums.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandChat implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        game.getConfig().switchConfigValue(ConfigBase.CHAT.getKey());

        Bukkit.broadcastMessage(game.getConfig().isConfigActive(ConfigBase.CHAT.getKey()) ? game.translate(Prefix.GREEN.getKey() , "werewolf.commands.admin.chat.on") : game.translate(Prefix.RED.getKey() , "werewolf.commands.admin.chat.off"));
    }
}

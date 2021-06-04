package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandChat implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        game.getConfig().switchConfigValue(ConfigBase.CHAT.getKey());

        Bukkit.broadcastMessage(game.getConfig().isConfigActive(ConfigBase.CHAT.getKey()) ? game.translate("werewolf.commands.admin.chat.on") : game.translate("werewolf.commands.admin.chat.off"));
    }
}

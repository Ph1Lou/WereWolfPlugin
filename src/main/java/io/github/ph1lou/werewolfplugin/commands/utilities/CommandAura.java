package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import org.bukkit.entity.Player;

public class CommandAura implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(game.translate(Prefix.BLUE.getKey(),"werewolf.commands.aura.prefix"));
        game.translateArray("werewolf.commands.aura.messages").forEach(player::sendMessage);
    }
}

package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.guis.Config;
import org.bukkit.entity.Player;

public class CommandConfig implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {
        Config.INVENTORY.open(player);
    }
}

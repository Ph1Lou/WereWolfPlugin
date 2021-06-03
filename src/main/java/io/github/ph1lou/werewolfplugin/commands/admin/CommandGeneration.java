package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;

public class CommandGeneration implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        game.getMapManager().generateMap(player, game.getConfig().getBorderMax() / 2);
    }
}

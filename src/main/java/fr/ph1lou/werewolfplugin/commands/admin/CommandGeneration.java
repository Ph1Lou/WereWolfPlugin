package fr.ph1lou.werewolfplugin.commands.admin;

import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

public class CommandGeneration implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        game.getMapManager().generateMap(player, game.getConfig().getBorderMax() / 2);
    }
}

package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfplugin.guis.Config;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

public class CommandConfig implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {
        Config.INVENTORY.open(player);
    }
}

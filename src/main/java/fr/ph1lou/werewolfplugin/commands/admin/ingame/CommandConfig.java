package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfplugin.guis.Config;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.menu.command",
        descriptionKey = "werewolf.menu.description",
        moderatorAccess = true,
        hostAccess = true,
        argNumbers = 0)
public class CommandConfig implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {
        Config.INVENTORY.open(player);
    }
}

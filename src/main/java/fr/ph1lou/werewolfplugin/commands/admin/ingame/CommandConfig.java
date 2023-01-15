package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfplugin.guis.MainGUI;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.config.command",
        descriptionKey = "werewolf.commands.admin.config.description",
        moderatorAccess = true,
        argNumbers = 0)
public class CommandConfig implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {
        MainGUI.INVENTORY.open(player);
    }
}

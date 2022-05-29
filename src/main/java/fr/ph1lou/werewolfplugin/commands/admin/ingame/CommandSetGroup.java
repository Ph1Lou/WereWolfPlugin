package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.set_group.command",
        descriptionKey = "werewolf.commands.admin.set_group.description",
        moderatorAccess = true,
        argNumbers = 1)
public class CommandSetGroup implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        try {
            game.setGroup(Integer.parseInt(args[0]));
            player.performCommand(String.format("ww %s", game.translate("werewolf.commands.admin.group.command")));

        } catch (NumberFormatException ignored) {
        }
    }
}

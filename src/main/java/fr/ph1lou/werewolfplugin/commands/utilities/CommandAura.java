package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.Command;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import org.bukkit.entity.Player;

@Command(key = "werewolf.commands.aura.command",
        descriptionKey = "werewolf.menu.aura.description",
        argNumbers = 0)
public class CommandAura implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(game.translate(Prefix.BLUE,"werewolf.commands.aura.prefix"));
        game.translateArray("werewolf.commands.aura.messages").forEach(player::sendMessage);
    }
}

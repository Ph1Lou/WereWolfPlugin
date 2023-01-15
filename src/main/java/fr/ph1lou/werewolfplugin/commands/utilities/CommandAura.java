package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.aura.command",
        descriptionKey = "werewolf.commands.player.aura.description",
        argNumbers = 0)
public class CommandAura implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(game.translate(Prefix.BLUE, "werewolf.commands.player.aura.prefix"));
        game.translateArray("werewolf.commands.player.aura.messages").forEach(player::sendMessage);
    }
}

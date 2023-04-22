package fr.ph1lou.werewolfplugin.commands.admin;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

import java.io.IOException;

@AdminCommand(key = "werewolf.commands.admin.change.command",
        descriptionKey = "werewolf.commands.admin.change.description",
        statesGame = StateGame.LOBBY,
        argNumbers = 0)
public class CommandChange implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {


        player.sendMessage(game.translate(Prefix.YELLOW, "werewolf.commands.admin.change.in_progress"));
        try {
            game.getMapManager().loadMap();
        } catch (IOException ignored) {
        }

        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.change.finished"));

    }
}

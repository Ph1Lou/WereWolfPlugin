package fr.ph1lou.werewolfplugin.commands.admin;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.generation.command",
        descriptionKey = "werewolf.commands.admin.generation.description",
        stateGame = StateGame.LOBBY,
        argNumbers = 0,
        moderatorAccess = true)
public class CommandGeneration implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        game.getMapManager().generateMap(player, game.getConfig().getBorderMax() / 2);
    }
}

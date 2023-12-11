package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfplugin.guis.ChoiceGui;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.color.command",
        descriptionKey = "werewolf.commands.player.color.description",
        argNumbers = 0,
        statesPlayer = StatePlayer.ALIVE,
        statesGame = StateGame.GAME)
public class CommandColor implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        ChoiceGui.getInventory(player).open(player);
    }
}

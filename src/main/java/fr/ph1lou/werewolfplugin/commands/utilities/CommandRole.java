package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.entity.Player;

import java.util.UUID;

@PlayerCommand(key = "werewolf.commands.player.role.command",
        descriptionKey = "werewolf.commands.player.role.description",
        statesGame = { StateGame.GAME, StateGame.END },
        argNumbers = 0)
public class CommandRole implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        player.sendMessage(playerWW.getRole().getDescription());

    }
}

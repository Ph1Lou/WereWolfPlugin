package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfplugin.guis.ChoiceGui;
import fr.ph1lou.werewolfplugin.guis.ColorsGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@PlayerCommand(key = "werewolf.commands.player.color.command",
        descriptionKey = "werewolf.commands.player.color.description",
        statesPlayer = StatePlayer.ALIVE,
        statesGame = StateGame.GAME)
public class CommandColor implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (args.length > 0) {
            ColorsGUI.getInventory(Arrays.stream(args).map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .map(Entity::getUniqueId)
                    .map(game::getPlayerWW)
                    .filter(Optional::isPresent)
                    .map(Optional::get).collect(Collectors.toList())).open(player);
            return;
        }
        ChoiceGui.getInventory(player).open(player);
    }
}

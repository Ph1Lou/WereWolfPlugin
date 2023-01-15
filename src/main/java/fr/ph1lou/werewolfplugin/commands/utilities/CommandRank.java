package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@PlayerCommand(key = "werewolf.commands.player.rank.command",
        descriptionKey = "werewolf.commands.player.rank.description",
        statesGame = StateGame.LOBBY,
        argNumbers = 0)
public class CommandRank implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();

        List<? extends UUID> queue = game.getModerationManager().getQueue();

        if (!game.isState(StateGame.LOBBY)) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.already_begin"));
            return;
        }

        if (queue.contains(uuid)) {
            player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.player.rank.perform",
                    Formatter.number(queue.indexOf(uuid) + 1)));
        } else {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.player.rank.not_in_queue"));
        }
    }
}

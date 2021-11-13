package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CommandRank implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();

        List<? extends UUID> queue = game.getModerationManager().getQueue();

        if (!game.isState(StateGame.LOBBY)) {
            player.sendMessage(game.translate("werewolf.check.already_begin"));
            return;
        }

        if (queue.contains(uuid)) {
            player.sendMessage(game.translate("werewolf.menu.rank.perform",
                    Formatter.format("&position&",queue.indexOf(uuid) + 1)));
        } else {
            player.sendMessage(game.translate("werewolf.menu.rank.not_in_queue"));
        }
    }
}

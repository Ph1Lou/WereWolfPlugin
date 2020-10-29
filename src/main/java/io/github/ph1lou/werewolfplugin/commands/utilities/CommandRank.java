package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CommandRank implements Commands {


    private final Main main;

    public CommandRank(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();
        UUID uuid = player.getUniqueId();

        List<UUID> queue = game.getModerationManager().getQueue();

        if (!game.isState(StateLG.LOBBY)) {
            player.sendMessage(game.translate("werewolf.check.already_begin"));
            return;
        }

        if (queue.contains(uuid)) {
            player.sendMessage(game.translate("werewolf.menu.rank.perform", queue.indexOf(uuid) + 1));
        } else {
            player.sendMessage(game.translate("werewolf.menu.rank.not_in_queue"));
        }
    }
}

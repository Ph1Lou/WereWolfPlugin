package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IModerationManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWhitelist implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IModerationManager moderationManager = game.getModerationManager();
        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID uuid = playerArg.getUniqueId();

        if (moderationManager.getWhiteListedPlayers().contains(uuid)) {
            player.sendMessage(game.translate("werewolf.commands.admin.whitelist.remove",
                    Formatter.format("&player&",playerArg.getName())));
            moderationManager.removePlayerOnWhiteList(uuid);
        } else {
            player.sendMessage(game.translate("werewolf.commands.admin.whitelist.add",
                    Formatter.format("&player&",playerArg.getName())));
            moderationManager.addPlayerOnWhiteList(uuid);
            ((GameManager) game).finalJoin(playerArg);
        }
    }
}

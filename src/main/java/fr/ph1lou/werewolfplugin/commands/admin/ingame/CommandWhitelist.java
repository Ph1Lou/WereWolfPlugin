package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWhitelist implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IModerationManager moderationManager = game.getModerationManager();
        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.offline_player"));
            return;
        }

        UUID uuid = playerArg.getUniqueId();

        if (moderationManager.getWhiteListedPlayers().contains(uuid)) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.commands.admin.whitelist.remove",
                    Formatter.player(playerArg.getName())));
            moderationManager.removePlayerOnWhiteList(uuid);
        } else {
            player.sendMessage(game.translate(Prefix.GREEN.getKey() , "werewolf.commands.admin.whitelist.add",
                    Formatter.player(playerArg.getName())));
            moderationManager.addPlayerOnWhiteList(uuid);
            ((GameManager) game).finalJoin(playerArg);
        }
    }
}

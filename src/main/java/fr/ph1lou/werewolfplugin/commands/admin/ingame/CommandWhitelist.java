package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerBukkitArg;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.whitelist.command",
        descriptionKey = "werewolf.commands.admin.whitelist.description",
        moderatorAccess = true,
        statesGame = StateGame.LOBBY,
        argNumbers = 1,
        args = @Params(bukkitPlayers = @PlayerBukkitArg(index = 0)))
public class CommandWhitelist implements IParameterizedCommand {

    @Override
    public void execute(WereWolfAPI game, Player player, CommandContext ctx) {

        IModerationManager moderationManager = game.getModerationManager();
        Player playerArg = ctx.bukkitPlayer(0);

        UUID uuid = playerArg.getUniqueId();

        if (moderationManager.getWhiteListedPlayers().contains(uuid)) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.whitelist.remove",
                    Formatter.player(playerArg.getName())));
            moderationManager.removePlayerOnWhiteList(uuid);
        } else {
            player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.whitelist.add",
                    Formatter.player(playerArg.getName())));
            moderationManager.addPlayerOnWhiteList(uuid);
            ((GameManager) game).finalJoin(playerArg);
        }
    }
}

package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.permissions.ModeratorEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandModerator implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {



        IModerationManager moderationManager = game.getModerationManager();
        Player moderator = Bukkit.getPlayer(args[0]);

        if (moderator == null) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.offline_player"));
            return;
        }

        UUID argUUID = moderator.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (moderationManager.getModerators().contains(argUUID)) {
            Bukkit.broadcastMessage(game.translate(Prefix.RED.getKey() , "werewolf.commands.admin.moderator.remove",
                    Formatter.player(moderator.getName())));
            moderationManager.getModerators().remove(argUUID);

            if (game.isState(StateGame.LOBBY)) {
                ((GameManager)game).finalJoin(moderator);
            }
            Bukkit.getPluginManager().callEvent(new ModeratorEvent(argUUID, false));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(moderator));
            return;
        }

        if (!game.isState(StateGame.LOBBY)) {
            if (playerWW1 != null && !playerWW1.isState(StatePlayer.DEATH)) {
                player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.commands.admin.moderator.player_living"));
                return;
            }
        } else {
            if (playerWW1 != null) {
                ((GameManager)game).remove(argUUID);
            } else {
                moderationManager.getQueue().remove(argUUID);
            }
            game.getModerationManager().checkQueue();
        }
        moderator.setGameMode(GameMode.SPECTATOR);
        moderationManager.addModerator(argUUID);
        Bukkit.broadcastMessage(game.translate(Prefix.GREEN.getKey() , "werewolf.commands.admin.moderator.add",
                Formatter.player(moderator.getName())));
        Bukkit.getPluginManager().callEvent(new ModeratorEvent(argUUID, true));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(moderator));
    }
}

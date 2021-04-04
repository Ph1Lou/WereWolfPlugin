package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IModerationManager;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.permissions.ModeratorEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandModerator implements ICommands {


    private final Main main;

    public CommandModerator(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {


        GameManager game = (GameManager) main.getWereWolfAPI();
        IModerationManager moderationManager = game.getModerationManager();
        Player moderator = Bukkit.getPlayer(args[0]);

        if (moderator == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID argUUID = moderator.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (moderationManager.getModerators().contains(argUUID)) {
            Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.moderator.remove", moderator.getName()));
            moderationManager.getModerators().remove(argUUID);

            if (game.isState(StateGame.LOBBY)) {
                game.finalJoin(moderator);
            }
            Bukkit.getPluginManager().callEvent(new ModeratorEvent(argUUID, false));
            return;
        }

        if (!game.isState(StateGame.LOBBY)) {
            if (playerWW1 != null && !playerWW1.isState(StatePlayer.DEATH)) {
                player.sendMessage(game.translate("werewolf.commands.admin.moderator.player_living"));
                return;
            }
        } else {
            if (playerWW1 != null) {
                game.getScore().removePlayerSize();
                game.remove(argUUID);
            } else {
                moderationManager.getQueue().remove(argUUID);
            }
            game.getModerationManager().checkQueue();
        }
        moderator.setGameMode(GameMode.SPECTATOR);
        moderationManager.addModerator(argUUID);
        Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.moderator.add", moderator.getName()));
        Bukkit.getPluginManager().callEvent(new ModeratorEvent(argUUID, true));
    }
}

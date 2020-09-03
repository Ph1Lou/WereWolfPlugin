package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.game.ModerationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandModerator implements Commands {


    private final Main main;

    public CommandModerator(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.getCurrentGame();
        ModerationManager moderationManager = game.getModerationManager();

        if (!sender.hasPermission("a.moderator.use") && !moderationManager.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        Player moderator = Bukkit.getPlayer(args[0]);

        if (moderator == null) {
            sender.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID argUUID = moderator.getUniqueId();

        if (moderationManager.getModerators().contains(argUUID)) {
            Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.moderator.remove", args[0]));
            moderationManager.getModerators().remove(argUUID);

            if (game.isState(StateLG.LOBBY)) {
                game.join(moderator);
            }
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent());
            return;
        }

        if (!game.isState(StateLG.LOBBY)) {
            if (game.getPlayersWW().containsKey(argUUID) && !game.getPlayersWW().get(argUUID).isState(State.DEATH)) {
                sender.sendMessage(game.translate("werewolf.commands.admin.moderator.player_living"));
                return;
            }
        } else {
            if (game.getPlayersWW().containsKey(argUUID)) {
                game.getScore().removePlayerSize();
                game.getPlayersWW().remove(argUUID);
            } else moderationManager.getQueue().remove(argUUID);
        }
        moderator.setGameMode(GameMode.SPECTATOR);
        moderationManager.getModerators().add(argUUID);
        Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.moderator.add", args[0]));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent());
        moderationManager.checkQueue();
    }
}

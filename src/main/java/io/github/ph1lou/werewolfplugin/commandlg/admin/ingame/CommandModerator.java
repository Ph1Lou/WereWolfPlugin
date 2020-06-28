package io.github.ph1lou.werewolfplugin.commandlg.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
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


        GameManager game = main.currentGame;


        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.moderator.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if (Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        Player moderator = Bukkit.getPlayer(args[0]);
        UUID argUUID = moderator.getUniqueId();

        if (game.getModerators().contains(argUUID)) {
            Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.moderator.remove", args[0]));
            game.getModerators().remove(argUUID);

            if (game.isState(StateLG.LOBBY)) {
                game.join(moderator);
            }
            game.updateNameTag();
            return;
        }

        if (!game.isState(StateLG.LOBBY)) {
            if (game.getPlayersWW().containsKey(argUUID) && !game.getPlayersWW().get(argUUID).isState(State.DEATH)) {
                sender.sendMessage(game.translate("werewolf.commands.admin.moderator.player_living"));
                return;
            }
        }
        else{
            if(game.getPlayersWW().containsKey(argUUID)){
                game.score.removePlayerSize();
                game.getPlayersWW().remove(argUUID);
            }
            else game.getQueue().remove(argUUID);
        }
        moderator.setGameMode(GameMode.SPECTATOR);
        game.getModerators().add(argUUID);
        Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.moderator.add", args[0]));
        game.updateNameTag();
        game.checkQueue();
    }
}

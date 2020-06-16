package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandModerator implements Commands {


    private final MainLG main;

    public CommandModerator(MainLG main) {
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
            if(game.playerLG.containsKey(argUUID)){
                moderator.setScoreboard(game.playerLG.get(argUUID).getScoreBoard());
            }
            else moderator.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            if (game.isState(StateLG.LOBBY)) {
                game.join(moderator);
                game.updateNameTag();
            }
            return;
        }

        if (!game.isState(StateLG.LOBBY)) {
            if (game.playerLG.containsKey(argUUID) && !game.playerLG.get(argUUID).isState(State.DEATH)) {
                sender.sendMessage(game.translate("werewolf.commands.admin.moderator.player_living"));
                return;
            }
        }
        else{
            if(game.playerLG.containsKey(argUUID)){
                game.score.removePlayerSize();
                game.playerLG.remove(argUUID);
            }
            else game.getQueue().remove(argUUID);
        }
        moderator.setGameMode(GameMode.SPECTATOR);
        game.getModerators().add(argUUID);
        moderator.setScoreboard(game.board);
        Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.moderator.add", args[0]));
        game.updateNameTag();
        game.checkQueue();
    }
}

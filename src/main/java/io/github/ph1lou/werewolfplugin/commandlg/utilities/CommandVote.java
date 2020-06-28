package io.github.ph1lou.werewolfplugin.commandlg.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandVote implements Commands {


    private final Main main;

    public CommandVote(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }
        if(!game.getPlayersWW().containsKey(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }
        if(Bukkit.getPlayer(args[0])==null){
            sender.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = Bukkit.getPlayer(args[0]).getUniqueId();
        game.getVote().setUnVote(((Player) sender).getUniqueId(), argUUID);
    }
}

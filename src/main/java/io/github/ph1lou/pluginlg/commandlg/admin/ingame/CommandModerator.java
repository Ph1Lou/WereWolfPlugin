package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandModerator extends Commands {


    public CommandModerator(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game = main.currentGame;

        TextLG text = game.text;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.moderator.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        if (args.length == 0) return;

        if (Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(game.text.getText(132));
            return;
        }

        Player moderator = Bukkit.getPlayer(args[0]);

        if (game.getModerators().contains(moderator.getUniqueId())) {
            Bukkit.broadcastMessage(String.format(game.text.getText(300), args[0]));
            game.getModerators().remove(moderator.getUniqueId());
            if (game.isState(StateLG.LOBBY)) {
                game.join(moderator);
                game.optionlg.updateNameTag();
            }
            return;
        }

        if (!game.isState(StateLG.LOBBY)) {
            if (game.playerLG.containsKey(args[0]) && !game.playerLG.get(args[0]).isState(State.MORT)) {
                sender.sendMessage(game.text.getText(298));
                return;
            }
        }
        else{
            game.score.removePlayerSize();
            game.board.getTeam(args[0]).unregister();
            game.playerLG.remove(args[0]);
        }
        moderator.setGameMode(GameMode.SPECTATOR);
        game.getModerators().add(moderator.getUniqueId());
        moderator.setScoreboard(game.board);
        Bukkit.broadcastMessage(String.format(game.text.getText(297), args[0]));
        game.optionlg.updateNameTag();
        game.checkQueue();
    }
}

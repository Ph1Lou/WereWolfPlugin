package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKill extends Commands {


    public CommandKill(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

     GameManager game = main.currentGame;

        TextLG text = game.text;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.kill.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }
        
        if (args.length != 1) {
            sender.sendMessage(text.getText(54));
            return;
        }
        if (!game.playerLG.containsKey(args[0])) {
            sender.sendMessage(text.getText(132));
            return;
        }
        if (!game.playerLG.get(args[0]).isState(State.LIVING)) {
            sender.sendMessage(text.getText(141));
            return;
        }
        if (game.isState(StateLG.DEBUT)) {
            game.score.removePlayerSize();
            game.playerLG.remove(args[0]);
            sender.sendMessage(text.getText(143));
            return;
        }
        if (Bukkit.getPlayer(args[0]) != null) {
            sender.sendMessage(text.getText(142));
            return;
        }
        if (game.isState(StateLG.LG)) {
            game.death_manage.death(args[0]);
        } else sender.sendMessage(text.getText(68));
    }
}

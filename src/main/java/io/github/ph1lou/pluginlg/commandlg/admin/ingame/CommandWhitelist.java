package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWhitelist extends Commands {

    public CommandWhitelist(MainLG main) {
        super(main);
    }


    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game = main.currentGame;


        TextLG text = game.text;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.whitelist.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        if (args.length != 1) {
            return;
        }

        if (Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(game.text.getText(132));
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        UUID uuid = player.getUniqueId();

        if (game.getWhiteListedPlayers().contains(uuid)) {
            sender.sendMessage(game.text.getText(282));
            game.removeWhiteListedPlayer(uuid);
        } else {
            sender.sendMessage(game.text.getText(283));
            game.addWhiteListedPlayer(uuid);
            if (game.isState(StateLG.LOBBY)) {
                game.join(player);
            }
        }
    }
}

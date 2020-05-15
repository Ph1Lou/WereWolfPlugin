package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
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


        GameManager game = main.currentGame;


        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.whitelist.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (args.length != 1) {
            return;
        }

        if (Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        UUID uuid = player.getUniqueId();

        if (game.getWhiteListedPlayers().contains(uuid)) {
            sender.sendMessage(game.translate("werewolf.commands.admin.whitelist.remove"));
            game.removePlayerOnWhiteList(uuid);
        } else {
            sender.sendMessage(game.translate("werewolf.commands.admin.whitelist.add"));
            game.addPlayerOnWhiteList(uuid);
            if (game.isState(StateLG.LOBBY)) {
                game.join(player);
            }
        }
    }
}

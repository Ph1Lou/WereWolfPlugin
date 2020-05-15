package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandDisconnected extends Commands {


    public CommandDisconnected(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.currentGame;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.disc.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        for (UUID uuid : game.playerLG.keySet()) {
            PlayerLG plg = game.playerLG.get(uuid);
            if (plg.isState(State.ALIVE) && Bukkit.getPlayer(uuid) == null) {
                sender.sendMessage(game.translate("werewolf.commands.admin.disconnected", plg.getName(), game.score.conversion(game.score.getTimer() - plg.getDeathTime())));
            }
        }
    }
}

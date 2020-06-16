package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommandDisconnected implements Commands {


    private final MainLG main;

    public CommandDisconnected(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.currentGame;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.disc.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        for (UUID uuid : game.playerLG.keySet()) {
            PlayerWW plg = game.playerLG.get(uuid);
            if (plg.isState(State.ALIVE) && Bukkit.getPlayer(uuid) == null) {
                sender.sendMessage(game.translate("werewolf.commands.admin.disconnected", plg.getName(), game.score.conversion((int) (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - plg.getDeathTime()))));
            }
        }
    }
}

package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommandDisconnected implements Commands {


    private final Main main;

    public CommandDisconnected(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.getCurrentGame();

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.disc.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        for (UUID uuid : game.getPlayersWW().keySet()) {
            PlayerWW plg = game.getPlayersWW().get(uuid);
            if (plg.isState(State.ALIVE) && Bukkit.getPlayer(uuid) == null) {
                sender.sendMessage(game.translate("werewolf.commands.admin.disconnected", plg.getName(), game.score.conversion((int) (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - plg.getDeathTime()))));
            }
        }
    }
}

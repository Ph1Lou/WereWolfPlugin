package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInfo extends Commands {


    public CommandInfo(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game = null;
        Player player = (Player) sender;

        for (GameManager gameManager : main.listGames.values()) {
            if (gameManager.getWorld().equals(player.getWorld())) {
                game = gameManager;
            }
        }

        if (game == null) {
            return;
        }

        TextLG text = game.text;

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.info.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        if (args.length == 0) return;

        StringBuilder sb2 = new StringBuilder();

        for (String w : args) {
            sb2.append(w).append(" ");
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (game.getWorld().equals(p.getWorld())) {
                p.sendMessage(String.format(text.getText(136), sb2.toString()));
            }
        }
    }
}

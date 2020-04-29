package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import io.github.ph1lou.pluginlg.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGroup extends Commands {


    public CommandGroup(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

     GameManager game = main.currentGame;

        TextLG text = game.text;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.Group.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            Title.sendTitle(p, 20, 60, 20, text.getText(138), String.format(text.getText(139), game.score.getGroup()));
            p.sendMessage(String.format(text.getText(137), game.score.getGroup()));
        }

    }
}

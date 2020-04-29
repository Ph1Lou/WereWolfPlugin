package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGamemode extends Commands {


    public CommandGamemode(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game = main.currentGame;
        Player player = (Player) sender;
        TextLG text = game.text;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.gamemode.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        if (args.length != 1) return;

        try {
            int i = Integer.parseInt(args[0]);
            if (i == 0) {
                i = 1;
            } else if (i == 1) {
                i = 0;
            }
            player.setGameMode(GameMode.values()[i]);
            Bukkit.getConsoleSender().sendMessage(String.format(game.text.getText(307), sender.getName(), i));
        }
        catch (NumberFormatException ignored){
        }
    }
}

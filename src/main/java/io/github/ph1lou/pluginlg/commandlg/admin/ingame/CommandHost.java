package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHost extends Commands {


    public CommandHost(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game = main.currentGame;

        TextLG text = game.text;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.host.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        if(Bukkit.getPlayer(args[0])==null){
            sender.sendMessage(game.text.getText(132));
            return;
        }

        Player host = Bukkit.getPlayer(args[0]);

        if(game.getHosts().size()==1){
            sender.sendMessage(game.text.getText(301));
            return;
        }

        if(game.getHosts().contains(host.getUniqueId())){
            Bukkit.broadcastMessage(String.format(game.text.getText(302), args[0]));
            game.getHosts().remove(host.getUniqueId());
            return;
        }

        game.getHosts().add(host.getUniqueId());
        Bukkit.broadcastMessage(String.format(game.text.getText(304), args[0]));
    }
}

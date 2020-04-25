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

        GameManager game=null;
        Player player =(Player) sender;

        for(GameManager gameManager:main.listGames.values()){
            if(gameManager.getWorld().equals(player.getWorld())){
                game=gameManager;
                break;
            }
        }

        if(game==null){
            return;
        }

        TextLG text = game.text;

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.host.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
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
            sender.sendMessage(String.format(game.text.getText(302),args[0]));
            game.getHosts().remove(host.getUniqueId());
            return;
        }

        if(!host.getWorld().equals(game.getWorld())){
            sender.sendMessage(game.text.getText(303));
            return;
        }

        game.getHosts().add(host.getUniqueId());
        sender.sendMessage(String.format(game.text.getText(304),args[0]));
        host.sendMessage(game.text.getText(305));
    }
}

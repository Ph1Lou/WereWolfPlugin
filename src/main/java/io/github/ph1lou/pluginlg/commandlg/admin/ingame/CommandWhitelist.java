package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWhitelist extends Commands {

    public CommandWhitelist(MainLG main) {
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

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.whitelist.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        if(args.length!=1){
            return;
        }
        if(game.getWhiteListedPlayers().contains(args[0])){
            sender.sendMessage(game.text.getText(282));
            game.removeWhiteListedPlayer(args[0]);
            return;
        }
        game.addWhiteListedPlayer(args[0]);


        if(Bukkit.getPlayer(args[0])!=null){
            game.sendMessage(Bukkit.getPlayer(args[0]));
            sender.sendMessage(game.text.getText(283));
        }
        else sender.sendMessage(game.text.getText(284));
    }
}

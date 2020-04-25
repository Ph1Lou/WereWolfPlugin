package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTP extends Commands {


    public CommandTP(MainLG main) {
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

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.gamemode.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        if(args.length==1) {
            try{
                if(player.getWorld().equals(Bukkit.getPlayer(args[0]).getWorld())){
                    player.teleport(Bukkit.getPlayer(args[0]));
                    Bukkit.getConsoleSender().sendMessage(String.format(game.text.getText(306),sender.getName(),args[0]));
                }
            }
            catch (Exception ignored){
            }
        }



        if(args.length!=2) return;

        try{
            if(Bukkit.getPlayer(args[1]).getWorld().equals(Bukkit.getPlayer(args[0]).getWorld())){
                Bukkit.getPlayer(args[0]).teleport(Bukkit.getPlayer(args[1]));
                Bukkit.getConsoleSender().sendMessage(String.format(game.text.getText(306),args[0],args[1]));
            }
        }
        catch (Exception ignored) {
        }

    }
}

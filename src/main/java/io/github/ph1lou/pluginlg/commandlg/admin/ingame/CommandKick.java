package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandKick extends Commands {


    public CommandKick(MainLG main) {
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

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.kick.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        if(args.length==0){
            sender.sendMessage(game.text.getText(54));
            return;
        }

        if(!game.isState(StateLG.LOBBY) && game.playerLG.containsKey(args[0]) && game.playerLG.get(args[0]).isState(State.LIVING)){
            sender.sendMessage(game.text.getText(285));
        }

        if(Bukkit.getPlayer(args[0])==null){
            sender.sendMessage(game.text.getText(132));
            return;
        }
        Player kickedPlayer= Bukkit.getPlayer(args[0]);

        if(!kickedPlayer.getWorld().equals(game.getWorld())){
            sender.sendMessage(game.text.getText(286));
            return;
        }

        StringBuilder sb =new StringBuilder();
        for(String arg: Arrays.copyOfRange(args,1,args.length)){
            sb.append(arg).append(" ");
        }
        kickedPlayer.sendMessage(String.format(game.text.getText(287),sender.getName(),sb.toString()));
        kickedPlayer.performCommand("lg leave");

    }
}

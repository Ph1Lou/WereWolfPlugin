package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCupid extends Commands {


    public CommandCupid(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)){
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
        String playername = player.getName();

        if(!game.playerLG.containsKey(playername)) {
            player.sendMessage(text.getText(67));
            return;
        }

        PlayerLG plg = game.playerLG.get(playername);

        if(!game.isState(StateLG.LG)) {
            player.sendMessage(text.getText(68));
            return;
        }

        if (!plg.isRole(RoleLG.CUPIDON)){
            player.sendMessage(String.format(text.getText(189),text.translateRole.get(RoleLG.CUPIDON)));
            return;
        }

        if (args.length!=2) {
            player.sendMessage(String.format(text.getText(190),2));
            return;
        }

        if(!plg.isState(State.LIVING)){
            player.sendMessage(text.getText(97));
            return;
        }

        if(!plg.hasPower()) {
            player.sendMessage(text.getText(103));
            return;
        }

        if(args[0].equals(args[1])) {
            player.sendMessage(text.getText(104));
            return;
        }

        for(String p:args) {
            if(p.equals(playername)) {
                player.sendMessage(text.getText(105));
                return;
            }
        }

        for(String p:args) {
            if(Bukkit.getPlayer(p)==null || !game.playerLG.containsKey(p) || game.playerLG.get(p).isState(State.MORT)) {
                player.sendMessage(text.getText(106));
                return;
            }
        }

        for(String p:args) {
            plg.addAffectedPlayer(p);
        }
        plg.setPower(false);

        sender.sendMessage(String.format(text.powerHasBeenUse.get(RoleLG.CUPIDON),args[0],args[1]));
    }
}

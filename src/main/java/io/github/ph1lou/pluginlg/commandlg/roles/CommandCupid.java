package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCupid extends Commands {
    final MainLG main;

    public CommandCupid(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)){
            return;
        }

        Player player =(Player) sender;
        String playername = player.getName();

        if(!main.playerLG.containsKey(playername)) {
            player.sendMessage(main.text.getText(67));
            return;
        }

        PlayerLG plg = main.playerLG.get(playername);

        if(!main.isState(StateLG.LG)) {
            player.sendMessage(main.text.getText(68));
            return;
        }

        if (!plg.isRole(RoleLG.CUPIDON)){
            player.sendMessage(String.format(main.text.getText(189),main.text.translateRole.get(RoleLG.CUPIDON)));
            return;
        }

        if (args.length!=2) {
            player.sendMessage(String.format(main.text.getText(190),2));
            return;
        }

        if(!plg.isState(State.LIVING)){
            player.sendMessage(main.text.getText(97));
            return;
        }

        if(!plg.hasPower()) {
            player.sendMessage(main.text.getText(103));
            return;
        }

        if(args[0].equals(args[1])) {
            player.sendMessage(main.text.getText(104));
            return;
        }

        for(String p:args) {
            if(p.equals(playername)) {
                player.sendMessage(main.text.getText(105));
                return;
            }
        }

        for(String p:args) {
            if(Bukkit.getPlayer(p)==null || !main.playerLG.containsKey(p) || main.playerLG.get(p).isState(State.MORT)) {
                player.sendMessage(main.text.getText(106));
                return;
            }
        }

        for(String p:args) {
            plg.addAffectedPlayer(p);
        }
        plg.setPower(false);

        sender.sendMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.CUPIDON),args[0],args[1]));
    }
}

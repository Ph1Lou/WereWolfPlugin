package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.Camp;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDetective extends Commands {

    final MainLG main;

    public CommandDetective(MainLG main, String name) {
        super(name);
        this.main=main;
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

        if (!plg.isRole(RoleLG.DETECTIVE)){
            player.sendMessage(String.format(main.text.getText(189),main.text.translateRole.get(RoleLG.DETECTIVE)));
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

        for(String p:args) {
            if(p.equals(playername)) {
                player.sendMessage(main.text.getText(105));
                return;
            }
        }

        for(String p:args) {

            if(Bukkit.getPlayer(p)==null || !main.playerLG.containsKey(p) || main.playerLG.get(p).isState(State.MORT)){
                player.sendMessage(main.text.getText(106));
                return;
            }
        }



        if(plg.getAffectedPlayer().contains(args[0]) || plg.getAffectedPlayer().contains(args[1])){
            player.sendMessage(main.text.getText(114));
            return;
        }

        PlayerLG plg1 = main.playerLG.get(args[0]);
        PlayerLG plg2 = main.playerLG.get(args[1]);

        plg.addAffectedPlayer(args[0]);
        plg.addAffectedPlayer(args[1]);
        plg.setPower(false);
        Camp isLG1=plg2.getCamp();
        Camp isLG2=plg1.getCamp();

        if(plg1.isRole(RoleLG.LOUP_FEUTRE)) {
            isLG2=plg1.getPosterCamp();
        }
        if(plg2.isRole(RoleLG.LOUP_FEUTRE)) {
            isLG1=plg2.getPosterCamp();
        }

        if(isLG1!=isLG2) {
            player.sendMessage(String.format(main.text.getText(72),args[0],args[1]));
        }
        else player.sendMessage(String.format(main.text.getText(71),args[0],args[1]));
    }
}

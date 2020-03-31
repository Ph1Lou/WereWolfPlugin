package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFox extends Commands {

    final MainLG main;

    public CommandFox(MainLG main, String name) {
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

        if (!plg.isRole(RoleLG.RENARD)){
            player.sendMessage(String.format(main.text.getText(189),main.text.translateRole.get(RoleLG.RENARD)));
            return;
        }

        if (args.length!=1) {
            player.sendMessage(main.text.getText(54));
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

        if(args[0].equals(playername)) {
            player.sendMessage(main.text.getText(105));
            return;
        }

        if(Bukkit.getPlayer(args[0])==null || !main.playerLG.containsKey(args[0]) || main.playerLG.get(args[0]).isState(State.MORT)){
            player.sendMessage(main.text.getText(106));
            return;
        }

        Location location = player.getLocation();
        Location locationTarget = Bukkit.getPlayer(args[0]).getLocation();

        if(location.distance(locationTarget)>20) {
            player.sendMessage(main.text.getText(111));
            return;
        }
        else if (plg.getUse()>=main.config.getUseOfFlair()){
            player.sendMessage(main.text.getText(103));
            return;
        }

        plg.clearAffectedPlayer();
        plg.addAffectedPlayer(args[0]);
        plg.setUse(plg.getUse()+1);
        plg.setPower(false);
        player.sendMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.RENARD),args[0]));
        main.playerLG.get(sender.getName()).setFlair(0f);
    }
}

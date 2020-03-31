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

public class CommandSeer extends Commands {

    final MainLG main;

    public CommandSeer(MainLG main, String name) {
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

        if (!plg.isRole(RoleLG.VOYANTE) && !plg.isRole(RoleLG.VOYANTE_BAVARDE)){
            player.sendMessage(String.format(main.text.getText(189),main.text.translateRole.get(RoleLG.VOYANTE)));
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

        if(Bukkit.getPlayer(args[0])==null || !main.playerLG.containsKey(args[0]) || main.playerLG.get(args[0]).isState(State.MORT)){
            player.sendMessage(main.text.getText(106));
            return;
        }

        double life =player.getMaxHealth();

        if (life<7) {
            player.sendMessage(main.text.getText(112));
        }
        else {
            PlayerLG plg1 = main.playerLG.get(args[0]);
            plg.setPower(false);
            plg.addAffectedPlayer(args[0]);

            if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isPosterCamp(Camp.VILLAGE)) || plg1.isCamp(Camp.VILLAGE)) {
                player.setMaxHealth(life-6);
                if(player.getHealth()>life-6) {
                    player.setHealth(life-6);
                }
                player.sendMessage(main.text.getText(113));
                if(plg.isRole(RoleLG.VOYANTE_BAVARDE)){
                    Bukkit.broadcastMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.VOYANTE_BAVARDE),main.text.translateRole.get(RoleLG.VILLAGEOIS)));
                }
                plg.addKLostHeart(6);
            }
            else if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isPosterCamp(Camp.LG)) || (!plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isCamp(Camp.LG))) {
                player.sendMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.VOYANTE),main.text.translateRole.get(RoleLG.LOUP_GAROU)));
                if(plg.isRole(RoleLG.VOYANTE_BAVARDE)){
                    Bukkit.broadcastMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.VOYANTE_BAVARDE),main.text.translateRole.get(RoleLG.LOUP_GAROU)));
                }
            }
            else if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isPosterCamp(Camp.NEUTRAL)) || plg1.isCamp(Camp.NEUTRAL)) {
                player.sendMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.VOYANTE),main.text.getText(201)));
                if(plg.isRole(RoleLG.VOYANTE_BAVARDE)){
                    Bukkit.broadcastMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.VOYANTE_BAVARDE),main.text.getText(201)));
                }
            }
        }
    }
}

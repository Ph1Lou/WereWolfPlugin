package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.Camp;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSeer extends Commands {


    public CommandSeer(MainLG main) {
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

        if (!plg.isRole(RoleLG.VOYANTE) && !plg.isRole(RoleLG.VOYANTE_BAVARDE)){
            player.sendMessage(String.format(text.getText(189),text.translateRole.get(RoleLG.VOYANTE)));
            return;
        }

        if (args.length!=1) {
            player.sendMessage(text.getText(54));
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

        if(Bukkit.getPlayer(args[0])==null || !game.playerLG.containsKey(args[0]) || game.playerLG.get(args[0]).isState(State.MORT)){
            player.sendMessage(text.getText(106));
            return;
        }

        double life =player.getMaxHealth();

        if (life<7) {
            player.sendMessage(text.getText(112));
        }
        else {
            PlayerLG plg1 = game.playerLG.get(args[0]);
            plg.setPower(false);
            plg.addAffectedPlayer(args[0]);

            if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isPosterCamp(Camp.VILLAGE)) || plg1.isCamp(Camp.VILLAGE)) {
                player.setMaxHealth(life-6);
                if(player.getHealth()>life-6) {
                    player.setHealth(life-6);
                }
                player.sendMessage(text.getText(113));
                if(plg.isRole(RoleLG.VOYANTE_BAVARDE)){
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (game.getWorld().equals(p.getWorld())) {
                            p.sendMessage(String.format(text.powerHasBeenUse.get(RoleLG.VOYANTE_BAVARDE), text.translateRole.get(RoleLG.VILLAGEOIS)));
                        }
                    }
                }
                plg.addKLostHeart(6);
            }
            else if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isPosterCamp(Camp.LG)) || (!plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isCamp(Camp.LG))) {
                player.sendMessage(String.format(text.powerHasBeenUse.get(RoleLG.VOYANTE),text.translateRole.get(RoleLG.LOUP_GAROU)));
                if(plg.isRole(RoleLG.VOYANTE_BAVARDE)){
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (game.getWorld().equals(p.getWorld())) {
                            p.sendMessage(String.format(text.powerHasBeenUse.get(RoleLG.VOYANTE_BAVARDE), text.translateRole.get(RoleLG.LOUP_GAROU)));
                        }
                    }
                }
            }
            else if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isPosterCamp(Camp.NEUTRAL)) || plg1.isCamp(Camp.NEUTRAL)) {
                player.sendMessage(String.format(text.powerHasBeenUse.get(RoleLG.VOYANTE),text.getText(201)));
                if(plg.isRole(RoleLG.VOYANTE_BAVARDE)){
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (game.getWorld().equals(p.getWorld())) {
                            p.sendMessage(String.format(text.powerHasBeenUse.get(RoleLG.VOYANTE_BAVARDE), text.getText(201)));
                        }
                    }
                }
            }
        }
    }
}

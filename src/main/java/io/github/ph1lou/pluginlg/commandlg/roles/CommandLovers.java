package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLovers extends Commands {

    final MainLG main;

    public CommandLovers(MainLG main, String name) {
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
        if(!plg.isState(State.LIVING)){
            player.sendMessage(main.text.getText(97));
            return;
        }
        if(!main.config.tool_switch.get(ToolLG.DON_LOVERS)){
            player.sendMessage(main.text.getText(259));
            return;
        }
        if(plg.getCouple().isEmpty()){
            player.sendMessage("§4§L[LG UHC]§r Vous n'ëtes pas en couple");
            return;
        }
        if (args.length!=1 && args.length!=2) {
            player.sendMessage(String.format(main.text.getText(190),1));
            return;
        }
        int heart;
        double life =player.getHealth();
        try {
            heart= Integer.parseInt(args[0]);
        } catch (NumberFormatException ignored) {
            player.sendMessage(main.text.getText(254));
            return;
        }
        if (life<=heart) {
            player.sendMessage(main.text.getText(255));
            return;
        }

        if(args.length==1){

            if(plg.getCouple().size()>heart){
                player.sendMessage(main.text.getText(256));
                return;
            }

            player.setHealth(life-heart);
            for(String p:plg.getCouple()){
                if(Bukkit.getPlayer(p)!=null){
                    Player playerCouple=Bukkit.getPlayer(p);
                    int don=heart/plg.getCouple().size();
                    if(playerCouple.getMaxHealth()-playerCouple.getHealth()>=don){
                        playerCouple.setHealth(playerCouple.getHealth()+don);
                        playerCouple.sendMessage(String.format(main.text.getText(260),don,playername));
                        playerCouple.playSound(playerCouple.getLocation(), Sound.PORTAL, 1, 20);
                        heart-=don;
                    }
                }
            }
            player.setHealth(player.getHealth()+heart);
        }
        else {
            if(args[0].equals(playername)) {
                player.sendMessage(main.text.getText(105));
                return;
            }
            if(!plg.getCouple().contains(args[1])){
                player.sendMessage(main.text.getText(257));
                return;
            }
            player.setHealth(life-heart);

            if(Bukkit.getPlayer(args[1])==null){
                player.sendMessage(main.text.getText(106));
                return;
            }
            Player playerCouple=Bukkit.getPlayer(args[1]);
            if(playerCouple.getMaxHealth()-playerCouple.getHealth()>=heart){
                playerCouple.setHealth(playerCouple.getHealth()+heart);
            }
        }
    }
}

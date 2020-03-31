package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRole extends Commands {

    final MainLG main;

    public CommandRole(MainLG main, String name) {
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

        if (args.length!=0) {
            player.sendMessage(String.format(main.text.getText(190),0));
            return;
        }

        if(!plg.isState(State.LIVING)){
            player.sendMessage(main.text.getText(97));
            return;
        }

        player.sendMessage(main.text.description.get(plg.getRole()));

        if(plg.isRole(RoleLG.SOEUR)) {
            StringBuilder list =new StringBuilder();
            for(String sister:main.playerLG.keySet()) {
                if(main.playerLG.get(sister).isState(State.LIVING) && main.playerLG.get(sister).isRole(RoleLG.SOEUR)) {
                    list.append(sister).append(" ");
                }
            }
            player.sendMessage(String.format(main.text.getText(22),list.toString()));
        }
        else if(plg.isRole(RoleLG.FRERE_SIAMOIS)) {
            StringBuilder list =new StringBuilder();
            for(String brother:main.playerLG.keySet()) {
                if(main.playerLG.get(brother).isState(State.LIVING) && main.playerLG.get(brother).isRole(RoleLG.FRERE_SIAMOIS)) {
                    list.append(brother).append(" ");
                }
            }
            player.sendMessage(String.format(main.text.getText(23),list.toString()));
        }
    }
}

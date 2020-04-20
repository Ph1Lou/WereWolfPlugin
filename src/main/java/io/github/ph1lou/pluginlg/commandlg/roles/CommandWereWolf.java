package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWereWolf extends Commands {

    final MainLG main;

    public CommandWereWolf(MainLG main) {
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

        if (args.length!=0) {
            player.sendMessage(String.format(main.text.getText(190),0));
            return;
        }

        if(!plg.isState(State.LIVING)){
            player.sendMessage(main.text.getText(97));
            return;
        }

        if (!plg.isCamp(Camp.LG) && !plg.isRole(RoleLG.LOUP_GAROU_BLANC)) {
            sender.sendMessage(main.text.getText(98));
            return;
        }
        if (!main.config.configValues.get(ToolLG.LG_LIST)) {
            sender.sendMessage(main.text.getText(99));
            return;
        }
        if (main.config.timerValues.get(TimerLG.LG_LIST) > 0) {
            sender.sendMessage(main.text.getText(100));
            return;
        }

        StringBuilder list =new StringBuilder();

        for(String p:main.playerLG.keySet()) {
            if(main.playerLG.get(p).isState(State.LIVING) && (main.playerLG.get(p).isCamp(Camp.LG) || main.playerLG.get(p).isRole(RoleLG.LOUP_GAROU_BLANC))) {
                list.append(p).append(" ");
            }
        }
        player.sendMessage(String.format(main.text.getText(101),list.toString()));
    }
}

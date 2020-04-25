package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.*;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWereWolf extends Commands {


    public CommandWereWolf(MainLG main) {
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

        if (args.length!=0) {
            player.sendMessage(String.format(text.getText(190),0));
            return;
        }

        if(!plg.isState(State.LIVING)){
            player.sendMessage(text.getText(97));
            return;
        }

        if (!plg.isCamp(Camp.LG) && !plg.isRole(RoleLG.LOUP_GAROU_BLANC)) {
            sender.sendMessage(text.getText(98));
            return;
        }
        if (!game.config.configValues.get(ToolLG.LG_LIST)) {
            sender.sendMessage(text.getText(99));
            return;
        }
        if (game.config.timerValues.get(TimerLG.LG_LIST) > 0) {
            sender.sendMessage(text.getText(100));
            return;
        }

        StringBuilder list =new StringBuilder();

        for(String p:game.playerLG.keySet()) {
            if(game.playerLG.get(p).isState(State.LIVING) && (game.playerLG.get(p).isCamp(Camp.LG) || game.playerLG.get(p).isRole(RoleLG.LOUP_GAROU_BLANC))) {
                list.append(p).append(" ");
            }
        }
        player.sendMessage(String.format(text.getText(101),list.toString()));
    }
}

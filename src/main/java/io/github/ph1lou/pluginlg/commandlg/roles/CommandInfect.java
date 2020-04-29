package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.*;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInfect extends Commands {


    public CommandInfect(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        if (!(sender instanceof Player)){
            return;
        }

        GameManager game = main.currentGame;

        TextLG text = game.text;
        Player player = (Player) sender;
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

        if (!plg.isRole(RoleLG.INFECT)){
            player.sendMessage(String.format(text.getText(189),text.translateRole.get(RoleLG.INFECT)));
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

        if (!game.config.configValues.get(ToolLG.AUTO_REZ_INFECT) && args[0].equals(playername)) {
            player.sendMessage(text.getText(105));
            return;
        }

        if(Bukkit.getPlayer(args[0])==null || !game.playerLG.containsKey(args[0]) || game.playerLG.get(args[0]).isState(State.MORT)) {
            player.sendMessage(text.getText(106));
            return;
        }
        PlayerLG plg1 = game.playerLG.get(args[0]);

        if (!plg1.isState(State.JUDGEMENT)) {
            player.sendMessage(text.getText(108));
            return;
        }

        if (!plg1.canBeInfect()) {
            player.sendMessage(text.getText(109));
            return;
        }

        plg.addAffectedPlayer(args[0]);
        plg.setPower(false);

        if(!plg1.isCamp(Camp.LG)) {
            game.roleManage.newLG(args[0]);
        }
        plg1.setCanBeInfect(false);
        player.sendMessage(String.format(text.powerHasBeenUse.get(RoleLG.INFECT),args[0]));
        game.death_manage.resurrection(args[0]);
    }
}

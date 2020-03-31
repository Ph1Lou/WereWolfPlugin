package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCitizenCancelVote extends Commands {

    final MainLG main;

    public CommandCitizenCancelVote(MainLG main, String name) {
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

        if (!plg.isRole(RoleLG.CITOYEN)){
            player.sendMessage(String.format(main.text.getText(189),main.text.translateRole.get(RoleLG.CITOYEN)));
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

        if(!plg.hasPower()) {
            player.sendMessage(main.text.getText(103));
            return;
        }

        if(main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2) <main.config.value.get(TimerLG.VOTE_DURATION) ){
            main.text.getText(103);
            return;
        }
        if(!main.config.tool_switch.get(ToolLG.VOTE) || main.config.value.get(TimerLG.VOTE_DURATION)+main.config.value.get(TimerLG.VOTE_BEGIN)>0) {
            main.text.getText(103);
            return;
        }
        if(main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2) > main.config.value.get(TimerLG.VOTE_DURATION)+main.config.value.get(TimerLG.CITIZEN_DURATION) ){
            main.text.getText(103);
            return;
        }

        plg.setPower(false);
        String vote=main.vote.getResult();
        sender.sendMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.CITOYEN),vote));
        plg.addAffectedPlayer(vote);
        Bukkit.broadcastMessage(main.text.getText(94));
        main.vote.resetVote();
    }
}

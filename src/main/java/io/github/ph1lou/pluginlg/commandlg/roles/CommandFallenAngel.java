package io.github.ph1lou.pluginlg.commandlg.roles;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFallenAngel extends Commands {

    final MainLG main;

    public CommandFallenAngel(MainLG main, String name) {
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

        if (!plg.isRole(RoleLG.ANGE)){
            player.sendMessage(String.format(main.text.getText(189),main.text.translateRole.get(RoleLG.ANGE)));
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

        plg.setRole(RoleLG.ANGE_DECHU);
        plg.setPower(false);
        sender.sendMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.ANGE),main.text.translateRole.get(RoleLG.ANGE_DECHU)));
    }
}

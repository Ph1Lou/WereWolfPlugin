package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandKill extends Commands {

    final MainLG main;

    public CommandKill(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.kill.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(main.text.getText(54));
            return;
        }
        if (!main.playerLG.containsKey(args[0])) {
            sender.sendMessage(main.text.getText(132));
            return;
        }
        if (!main.playerLG.get(args[0]).isState(State.LIVING)) {
            sender.sendMessage(main.text.getText(141));
            return;
        }
        if (main.isState(StateLG.DEBUT)) {
            main.score.removePlayerSize();
            main.playerLG.remove(args[0]);
            sender.sendMessage(main.text.getText(143));
            return;
        }
        if (Bukkit.getPlayer(args[0]) != null) {
            sender.sendMessage(main.text.getText(142));
            return;
        }
        if (main.isState(StateLG.LG)) {
            main.death_manage.death(args[0]);
        } else sender.sendMessage(main.text.getText(68));
    }
}

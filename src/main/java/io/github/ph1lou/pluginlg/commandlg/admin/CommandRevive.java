package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRevive extends Commands {

    final MainLG main;

    public CommandRevive(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.revive.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(main.text.getText(54));
            return;
        }

        if (!main.isState(StateLG.LG)) {
            sender.sendMessage(main.text.getText(68));
            return;
        }

        if (!main.playerLG.containsKey(args[0])) {
            sender.sendMessage(main.text.getText(132));
            return;
        }

        if (!main.playerLG.get(args[0]).isState(State.MORT)) {
            sender.sendMessage(main.text.getText(149));
            return;
        }

        RoleLG role = main.playerLG.get(args[0]).getRole();
        main.config.roleCount.put(role, main.config.roleCount.get(role) + 1);
        main.death_manage.resurrection(args[0]);
        main.score.addPlayerSize();
        if (role.equals(RoleLG.PETITE_FILLE) || role.equals(RoleLG.LOUP_PERFIDE)) {
            main.playerLG.get(args[0]).setPower(true);
        }
        Bukkit.broadcastMessage(String.format(main.text.getText(154), args[0]));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 20);
        }
    }
}

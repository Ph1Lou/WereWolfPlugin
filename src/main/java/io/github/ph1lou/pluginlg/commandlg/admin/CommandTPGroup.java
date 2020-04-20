package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTPGroup extends Commands {

    final MainLG main;

    public CommandTPGroup(MainLG main) {
        this.main = main;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("tpGroup.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }

        if (args.length != 1 && args.length != 2) {
            sender.sendMessage(main.text.getText(54));
            return;
        }

        if (!main.playerLG.containsKey(args[0]) || Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(main.text.getText(132));
            return;
        }

        if (!main.isState(StateLG.LG)) {
            sender.sendMessage(main.text.getText(144));
            return;
        }

        if (!main.playerLG.get(args[0]).isState(State.LIVING)) {
            return;
        }
        int d = 20;
        int size = main.score.getGroup();
        double r = Math.random() * Bukkit.getOnlinePlayers().size();
        Player player = Bukkit.getPlayer(args[0]);
        Location location = player.getLocation();

        try {
            if (args.length == 2) {
                d = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException ignored) {
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (size > 0 && main.playerLG.containsKey(p.getName()) && main.playerLG.get(p.getName()).isState(State.LIVING)) {
                if (p.getLocation().distance(location) <= d) {
                    size--;
                    main.death_manage.transportation(p.getName(), r, main.text.getText(93));
                }
            }
        }
    }
}

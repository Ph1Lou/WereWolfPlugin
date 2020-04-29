package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTPGroup extends Commands {


    public CommandTPGroup(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

     GameManager game = main.currentGame;

        TextLG text = game.text;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.tpGroup.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }
        

        if (args.length != 1 && args.length != 2) {
            sender.sendMessage(text.getText(54));
            return;
        }

        if (!game.playerLG.containsKey(args[0]) || Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(text.getText(132));
            return;
        }

        if (!game.isState(StateLG.LG)) {
            sender.sendMessage(text.getText(144));
            return;
        }

        if (!game.playerLG.get(args[0]).isState(State.LIVING)) {
            return;
        }
        int d = 20;
        int size = game.score.getGroup();
        double r = Math.random() * Bukkit.getOnlinePlayers().size();
        Player target = Bukkit.getPlayer(args[0]);
        Location location = target.getLocation();

        try {
            if (args.length == 2) {
                d = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException ignored) {
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (size > 0 && game.playerLG.containsKey(p.getName()) && game.playerLG.get(p.getName()).isState(State.LIVING)) {
                if (p.getLocation().distance(location) <= d) {
                    size--;
                    game.death_manage.transportation(p.getName(), r, text.getText(93));
                }
            }
        }
    }
}

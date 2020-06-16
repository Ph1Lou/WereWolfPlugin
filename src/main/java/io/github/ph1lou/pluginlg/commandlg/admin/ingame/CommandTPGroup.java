package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandTPGroup implements Commands {


    private final MainLG main;

    public CommandTPGroup(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.currentGame;


        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.tpGroup.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }
        

        if (args.length != 1 && args.length != 2) {
            sender.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }
        if(Bukkit.getPlayer(args[0])==null){
            sender.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = Bukkit.getPlayer(args[0]).getUniqueId();

        if (!game.playerLG.containsKey(argUUID)) {
            sender.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (!game.isState(StateLG.GAME)) {
            sender.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!game.playerLG.get(argUUID).isState(State.ALIVE)) {
            return;
        }
        int d = 20;
        int size = game.score.getGroup();
        double r = Math.random() * Bukkit.getOnlinePlayers().size();
        Player target = Bukkit.getPlayer(args[0]);
        Location location = target.getLocation();
        StringBuilder sb = new StringBuilder();
        try {
            if (args.length == 2) {
                d = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException ignored) {
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            if (size > 0 && game.playerLG.containsKey(uuid) && game.playerLG.get(uuid).isState(State.ALIVE)) {
                if (p.getLocation().distance(location) <= d) {
                    size--;
                    sb.append(p.getName()).append(" ");
                    game.transportation(uuid, r, game.translate("werewolf.commands.admin.tp_group.perform"));
                }
            }
        }
        Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.tp_group.broadcast",sb.toString()));
    }
}

package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandTPGroup implements Commands {


    private final Main main;

    public CommandTPGroup(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.getCurrentGame();


        if (!sender.hasPermission("a.tpGroup.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }


        if (args.length != 1 && args.length != 2) {
            sender.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            sender.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();

        if (!game.getPlayersWW().containsKey(argUUID)) {
            sender.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (!game.isState(StateLG.GAME)) {
            sender.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!game.getPlayersWW().get(argUUID).isState(State.ALIVE)) {
            return;
        }
        int d = 20;
        int size = game.getScore().getGroup();
        double r = Math.random() * 2 * Math.PI;

        Location location = playerArg.getLocation();
        StringBuilder sb = new StringBuilder();
        try {
            if (args.length == 2) {
                d = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException ignored) {
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            if (size > 0 && game.getPlayersWW().containsKey(uuid) && game.getPlayersWW().get(uuid).isState(State.ALIVE)) {
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

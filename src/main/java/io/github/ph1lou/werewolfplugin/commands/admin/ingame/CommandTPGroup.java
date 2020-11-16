package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandTPGroup implements Commands {


    private final Main main;

    public CommandTPGroup(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {


        WereWolfAPI game = main.getWereWolfAPI();
        Player playerArg = Bukkit.getPlayer(args[0]);
        String playerName = player.getName();

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();

        if (!game.getPlayersWW().containsKey(argUUID)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (!game.getPlayersWW().get(argUUID).isState(StatePlayer.ALIVE)) {
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
            if (size > 0 && game.getPlayersWW().containsKey(uuid) && game.getPlayersWW().get(uuid).isState(StatePlayer.ALIVE)) {

                try {
                    if (p.getLocation().distance(location) <= d) {
                        size--;
                        sb.append(p.getName()).append(" ");
                        game.getMapManager().transportation(uuid, r, game.translate("werewolf.commands.admin.tp_group.perform", playerName));
                    }
                } catch (Exception ignored) {

                }
            }
        }
        Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.tp_group.broadcast", sb.toString(), playerName));
    }
}

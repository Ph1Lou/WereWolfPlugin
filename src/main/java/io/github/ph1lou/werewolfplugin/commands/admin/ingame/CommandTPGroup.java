package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandTPGroup implements ICommands {


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
        IPlayerWW playerWW = game.getPlayerWW(argUUID);

        if (playerWW == null) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (!playerWW.isState(StatePlayer.ALIVE)) {
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
            IPlayerWW playerWW1 = game.getPlayerWW(uuid);

            if (size > 0 && playerWW1 != null && playerWW1.isState(StatePlayer.ALIVE)) {

                try {
                    if (p.getLocation().distance(location) <= d) {
                        size--;
                        sb.append(p.getName()).append(" ");
                        playerWW1.sendMessageWithKey("werewolf.commands.admin.tp_group.perform", playerName);
                        game.getMapManager().transportation(playerWW1, r);
                    }
                } catch (Exception ignored) {

                }
            }
        }
        Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.commands.admin.tp_group.broadcast", sb.toString(), playerName));
    }
}

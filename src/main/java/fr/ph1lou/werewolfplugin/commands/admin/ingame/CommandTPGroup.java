package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.annotations.IntArg;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.tp_group.command",
        descriptionKey = "werewolf.commands.admin.tp_group.description",
        argNumbers = {1, 2},
        moderatorAccess = true,
        statesGame = StateGame.GAME,
        args = @Params(
                players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }),
                integers = @IntArg(index = 1, required = false)))
public class CommandTPGroup implements IParameterizedCommand {

    @Override
    public void execute(WereWolfAPI game, Player player, CommandContext ctx) {

        IPlayerWW playerWW = ctx.player(0);
        Player playerArg = Bukkit.getPlayer(playerWW.getUUID());
        String playerName = player.getName();

        int d = ctx.has(1) ? ctx.intValue(1) : 20;
        int size = game.getGroup();
        double r = Math.random() * 2 * Math.PI;

        Location location = playerArg.getLocation();
        StringBuilder sb = new StringBuilder();

        for (Player p : Bukkit.getOnlinePlayers()) {
            IPlayerWW playerWW1 = game.getPlayerWW(p.getUniqueId()).orElse(null);

            if (size > 0 && playerWW1 != null && playerWW1.isState(StatePlayer.ALIVE)) {
                if (p.getWorld().equals(playerArg.getWorld())) {
                    if (p.getLocation().distance(location) <= d) {
                        size--;
                        sb.append(p.getName()).append(" ");
                        playerWW1.sendMessageWithKey(Prefix.YELLOW, "werewolf.commands.admin.tp_group.perform",
                                Formatter.player(playerName));
                        game.getMapManager().transportation(playerWW1, r);
                    }
                }
            }
        }
        Bukkit.getConsoleSender().sendMessage(game.translate(Prefix.YELLOW, "werewolf.commands.admin.tp_group.broadcast",
                Formatter.format("&players&", sb.toString()),
                Formatter.player(playerName)));
    }
}

package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
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
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AdminCommand(key = "werewolf.commands.admin.group.command",
        descriptionKey = "werewolf.commands.admin.group.description",
        moderatorAccess = true,
        argNumbers = {0, 1},
        statesGame = StateGame.GAME,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, required = false)))
public class CommandGroup implements IParameterizedCommand {

    @Override
    public void execute(WereWolfAPI game, Player player, CommandContext ctx) {

        Player playerArg = ctx.has(0) ? Bukkit.getPlayer(ctx.player(0).getUUID()) : null;

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (playerArg != null) {
            int d = 20;
            Location location = playerArg.getLocation();

            players = players.stream().filter(p -> {
                IPlayerWW playerWW1 = game.getPlayerWW(p.getUniqueId()).orElse(null);

                if (playerWW1 != null && playerWW1.isState(StatePlayer.ALIVE)) {
                    if (p.getWorld().equals(playerArg.getWorld())) {
                        return p.getLocation().distance(location) <= d;
                    }
                }
                return false;
            }).collect(Collectors.toList());
        }

        for (Player p : players) {
            VersionUtils.getVersionUtils().sendTitle(p, game.translate("werewolf.commands.admin.group.top_title"), game.translate("werewolf.commands.admin.group.bot_title",
                    Formatter.number(game.getGroup())), 20, 60, 20);
            p.sendMessage(game.translate(Prefix.YELLOW, "werewolf.commands.admin.group.respect_limit",
                    Formatter.number(game.getGroup())));
        }
    }
}

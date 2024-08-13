package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
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
import java.util.UUID;
import java.util.stream.Collectors;

@AdminCommand(key = "werewolf.commands.admin.group.command",
        descriptionKey = "werewolf.commands.admin.group.description",
        moderatorAccess = true,
        argNumbers = {0,1},
        statesGame = StateGame.GAME)
public class CommandGroup implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {
        Player playerArg;

        if (args.length == 1) {
            playerArg = Bukkit.getPlayer(args[0]);
            if (playerArg == null) {
                player.sendMessage(game.translate(Prefix.RED, "werewolf.check.offline_player"));
                return;
            }
            UUID argUUID = playerArg.getUniqueId();
            IPlayerWW playerWW = game.getPlayerWW(argUUID).orElse(null);

            if (playerWW == null) {
                player.sendMessage(game.translate(Prefix.RED, "werewolf.check.player_not_found"));
                return;
            }

            if (!playerWW.isState(StatePlayer.ALIVE)) {
                player.sendMessage(game.translate(Prefix.RED, "werewolf.check.player_not_found"));
                return;
            }
        } else {
            playerArg = null;
        }

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (playerArg != null) {
            int d = 20;
            Location location = playerArg.getLocation();

            players = players.stream().filter(p -> {
                UUID uuid = p.getUniqueId();
                IPlayerWW playerWW1 = game.getPlayerWW(uuid).orElse(null);

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

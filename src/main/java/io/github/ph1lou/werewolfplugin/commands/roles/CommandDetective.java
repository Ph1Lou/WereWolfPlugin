package io.github.ph1lou.werewolfplugin.commands.roles;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.detective.InvestigateEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandDetective implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole detective = playerWW.getRole();

        if (args.length != 2) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.parameters",
                    Formatter.format("&number&",2));
            return;
        }

        if (args[0].equalsIgnoreCase(args[1])) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.two_distinct_player");
            return;
        }

        for(String p:args) {

            Player playerArg = Bukkit.getPlayer(p);

            if (playerArg == null) {
                playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.offline_player");
                return;
            }

            UUID uuid1 = playerArg.getUniqueId();
            IPlayerWW playerWW1 = game.getPlayerWW(uuid1).orElse(null);

            if (playerWW1 == null || playerWW1.isState(StatePlayer.DEATH)) {
                playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.player_not_found");
                return;
            }

            if (uuid.equals(uuid1)) {
                playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.not_yourself");
                return;
            }

            if (((IAffectedPlayers) detective).getAffectedPlayers().contains(playerWW1)) {
                playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.detective.already_inspect");
                return;
            }
        }

        Player player1 = Bukkit.getPlayer(args[0]);
        Player player2 = Bukkit.getPlayer(args[1]);

        if (player1 == null || player2 == null) return;

        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();

        IPlayerWW playerWW1 = game.getPlayerWW(uuid1).orElse(null);
        IPlayerWW playerWW2 = game.getPlayerWW(uuid2).orElse(null);

        if (playerWW1 == null || playerWW2 == null) return;

        String isLG1 = playerWW1.getRole().getDisplayCamp();
        String isLG2 = playerWW2.getRole().getDisplayCamp();

        ((IPower) detective).setPower(false);

        InvestigateEvent event = new InvestigateEvent(playerWW, Sets.newHashSet(playerWW1, playerWW2), isLG1.equals(isLG2));
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) detective).addAffectedPlayer(playerWW1);
        ((IAffectedPlayers) detective).addAffectedPlayer(playerWW2);

        if (event.isSameCamp()) {
            playerWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.detective.same_camp",
                    Formatter.format("&player1&",player1.getName()),
                    Formatter.format("&player2&",player2.getName()));
        } else
            playerWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.detective.opposing_camp",
                    Formatter.format("&player1&",player1.getName()),
                    Formatter.format("&player2&",player2.getName()));
    }
}

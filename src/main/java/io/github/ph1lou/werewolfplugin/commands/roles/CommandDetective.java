package io.github.ph1lou.werewolfplugin.commands.roles;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.InvestigateEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandDetective implements Commands {


    private final Main main;

    public CommandDetective(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles detective = playerWW.getRole();

        if (args.length != 2) {
            player.sendMessage(game.translate("werewolf.check.parameters", 2));
            return;
        }

        if (args[0].equalsIgnoreCase(args[1])) {
            player.sendMessage(game.translate("werewolf.check.two_distinct_player"));
            return;
        }

        for(String p:args) {

            Player playerArg = Bukkit.getPlayer(p);

            if (playerArg == null) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }

            UUID uuid1 = playerArg.getUniqueId();
            PlayerWW playerWW1 = game.getPlayerWW(uuid1);

            if (playerWW1 == null || playerWW1.isState(StatePlayer.DEATH)) {
                player.sendMessage(game.translate("werewolf.check.player_not_found"));
                return;
            }

            if (uuid.equals(uuid1)) {
                player.sendMessage(game.translate("werewolf.check.not_yourself"));
                return;
            }

            if (((AffectedPlayers) detective).getAffectedPlayers().contains(playerWW1)) {
                player.sendMessage(game.translate("werewolf.role.detective.already_inspect"));
                return;
            }
        }

        Player player1 = Bukkit.getPlayer(args[0]);
        Player player2 = Bukkit.getPlayer(args[1]);

        if (player1 == null || player2 == null) return;

        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();

        PlayerWW playerWW1 = game.getPlayerWW(uuid1);
        PlayerWW playerWW2 = game.getPlayerWW(uuid2);

        if (playerWW1 == null || playerWW2 == null) return;

        Camp isLG1 = playerWW1.getRole().getCamp();
        Camp isLG2 = playerWW2.getRole().getCamp();

        if (playerWW1.getRole() instanceof Display) {
            isLG1 = ((Display) playerWW1.getRole()).getDisplayCamp();
        }
        if (playerWW2.getRole() instanceof Display) {
            isLG2 = ((Display) playerWW2.getRole()).getDisplayCamp();
        }

        ((Power) detective).setPower(false);

        InvestigateEvent event = new InvestigateEvent(playerWW, Sets.newHashSet(playerWW1, playerWW2), isLG1 == isLG2);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) detective).addAffectedPlayer(playerWW1);
        ((AffectedPlayers) detective).addAffectedPlayer(playerWW2);

        if (event.isSameCamp()) {
            player.sendMessage(game.translate("werewolf.role.detective.same_camp", player1.getName(), player2.getName()));
        } else
            player.sendMessage(game.translate("werewolf.role.detective.opposing_camp", player1.getName(), player2.getName()));
    }
}

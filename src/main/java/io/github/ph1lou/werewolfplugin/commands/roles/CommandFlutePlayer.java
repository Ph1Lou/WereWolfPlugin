package io.github.ph1lou.werewolfplugin.commands.roles;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.EnchantedEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandFlutePlayer implements Commands {

    final GetWereWolfAPI api;

    public CommandFlutePlayer(GetWereWolfAPI api) {
        this.api=api;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = api.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles flutePlayer = playerWW.getRole();

        if (args[0].equals(args[1])) {
            player.sendMessage(game.translate("werewolf.check.two_distinct_player"));
            return;
        }

        List<PlayerWW> listWWs = new ArrayList<>();

        for (String p : args) {

            Player playerArg = Bukkit.getPlayer(p);

            if (playerArg == null) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }

            UUID playerUUID = playerArg.getUniqueId();
            PlayerWW playerWW1 = game.getPlayerWW(playerUUID);

            if (playerWW1 == null || playerWW1.isState(StatePlayer.DEATH)) {
                player.sendMessage(game.translate("werewolf.check.player_not_found"));
                return;
            }

            if (p.equals(playerWW.getName())) {
                player.sendMessage(game.translate("werewolf.check.not_yourself"));
                return;
            }

            if (((AffectedPlayers) flutePlayer).getAffectedPlayers().contains(playerWW)) {
                if (game.getScore().getPlayerSize() != ((AffectedPlayers) flutePlayer).getAffectedPlayers().stream().filter(playerWW2 -> playerWW2.isState(StatePlayer.ALIVE)).count() + 2) {
                    player.sendMessage(game.translate("werewolf.role.flute_player.already_enchant", playerArg.getName()));
                    return;
                }
            }

            try {
                if (player.getLocation().distance(playerArg.getLocation()) > 100) {
                    player.sendMessage(game.translate("werewolf.role.flute_player.distance", playerArg.getName()));
                    return;
                }
            } catch (Exception ignored) {
                return;
            }


            listWWs.add(playerWW1);
        }


        ((Power) flutePlayer).setPower(false);


        EnchantedEvent enchantedEvent = new EnchantedEvent(playerWW, Sets.newHashSet(listWWs));

        Bukkit.getPluginManager().callEvent(enchantedEvent);

        if (enchantedEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        for (PlayerWW playerWW1 : enchantedEvent.getPlayerWWS()) {

            ((AffectedPlayers) flutePlayer).addAffectedPlayer(playerWW1);
            playerWW1.sendMessage(game.translate("werewolf.role.flute_player.enchanted"));
            player.sendMessage(game.translate("werewolf.role.flute_player.perform", playerWW1.getName()));
        }

        game.checkVictory();
    }
}

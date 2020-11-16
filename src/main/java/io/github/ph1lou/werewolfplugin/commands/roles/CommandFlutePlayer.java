package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
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
        PlayerWW plg = game.getPlayersWW().get(uuid);
        Roles flutePlayer = plg.getRole();

        if (args.length != 2 && args.length != 1) {
            player.sendMessage(game.translate("werewolf.check.parameters", 2));
            return;
        }

            if(!((Power)flutePlayer).hasPower()) {
                player.sendMessage(game.translate("werewolf.check.power"));
                return ;
            }

            if(args.length==2 && args[0].equals(args[1])) {
                player.sendMessage(game.translate("werewolf.check.two_distinct_player"));
                return ;
            }

            List<UUID> listUUIDs = new ArrayList<>();

            for(String p:args) {

                Player playerArg = Bukkit.getPlayer(p);

                if (playerArg == null) {
                    player.sendMessage(game.translate("werewolf.check.offline_player"));
                    return;
                }

                UUID playerUUID = playerArg.getUniqueId();

                if (!game.getPlayersWW().containsKey(playerUUID) || game.getPlayersWW().get(playerUUID).isState(StatePlayer.DEATH)) {
                    player.sendMessage(game.translate("werewolf.check.player_not_found"));
                    return;
                }

                if(p.equals(plg.getName())) {
                    player.sendMessage(game.translate("werewolf.check.not_yourself"));
                    return ;
                }

                if (!game.getPlayersWW().containsKey(playerUUID)) {
                    player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
                    return;
                }

                if (((AffectedPlayers) flutePlayer).getAffectedPlayers().contains(playerUUID)) {
                    player.sendMessage(game.translate("werewolf.role.flute_player.already_enchant", playerArg.getName()));
                    return;
                }

                try {
                    if (player.getLocation().distance(playerArg.getLocation()) > 100) {
                        player.sendMessage(game.translate("werewolf.role.flute_player.distance", playerArg.getName()));
                        return;
                    }
                } catch (Exception ignored) {
                    return;
                }


                listUUIDs.add(playerUUID);
            }

        ((Power) flutePlayer).setPower(false);

        EnchantedEvent enchantedEvent = new EnchantedEvent(uuid, listUUIDs);

        Bukkit.getPluginManager().callEvent(enchantedEvent);

        if (enchantedEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        for (UUID uuid1 : enchantedEvent.getPlayersUUID()) {

            Player enchanted = Bukkit.getPlayer(uuid1);
            if (enchanted == null) return;

            ((AffectedPlayers) flutePlayer).addAffectedPlayer(uuid1);
            enchanted.sendMessage(game.translate("werewolf.role.flute_player.enchanted"));
            player.sendMessage(game.translate("werewolf.role.flute_player.perform", enchanted.getName()));
        }


        game.checkVictory();
    }
}

package io.github.ph1lou.werewolfplugin.commands.roles;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.flute_player.EnchantedEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandFlutePlayer implements ICommands {

    final GetWereWolfAPI api;

    public CommandFlutePlayer(GetWereWolfAPI api) {
        this.api = api;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = api.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        IRole flutePlayer = playerWW.getRole();

        if (args[0].equals(args[1])) {
            player.sendMessage(game.translate("werewolf.check.two_distinct_player"));
            return;
        }

        List<IPlayerWW> listWWs = new ArrayList<>();

        for (String p : args) {

            Player playerArg = Bukkit.getPlayer(p);

            if (playerArg == null) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }

            UUID playerUUID = playerArg.getUniqueId();
            IPlayerWW playerWW1 = game.getPlayerWW(playerUUID);

            if (playerWW1 == null || playerWW1.isState(StatePlayer.DEATH)) {
                player.sendMessage(game.translate("werewolf.check.player_not_found"));
                return;
            }

            if (p.equals(playerWW.getName())) {
                player.sendMessage(game.translate("werewolf.check.not_yourself"));
                return;
            }

            if (((IAffectedPlayers) flutePlayer).getAffectedPlayers().contains(playerWW)) {
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


            listWWs.add(playerWW1);
        }


        ((IPower) flutePlayer).setPower(false);


        EnchantedEvent enchantedEvent = new EnchantedEvent(playerWW, Sets.newHashSet(listWWs));

        Bukkit.getPluginManager().callEvent(enchantedEvent);

        if (enchantedEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        for (IPlayerWW playerWW1 : enchantedEvent.getPlayerWWS()) {

            ((IAffectedPlayers) flutePlayer).addAffectedPlayer(playerWW1);
            playerWW1.sendMessageWithKey("werewolf.role.flute_player.enchanted");
            playerWW.sendMessageWithKey("werewolf.role.flute_player.perform", playerWW1.getName());
        }

        game.checkVictory();
    }
}

package fr.ph1lou.werewolfplugin.utils;

import fr.ph1lou.werewolfapi.events.random_events.ExposedEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ExposedHelper {

    private ExposedHelper() {
    }

    public static boolean expose(WereWolfAPI game, IPlayerWW playerWW) {

        List<IRole> role1List = game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(playerWW))
                .map(IPlayerWW::getRole)
                .filter(r -> !r.isKey(playerWW.getRole().getKey()))
                .filter(r -> !r.isCamp(playerWW.getRole().getCamp()))
                .collect(Collectors.toList());

        if (role1List.isEmpty()) {
            return false;
        }

        IRole role1 = role1List.get(game.getRandom().nextInt(role1List.size()));

        List<IRole> role2List = game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(playerWW))
                .map(IPlayerWW::getRole)
                .filter(r -> !r.isKey(role1.getKey()))
                .filter(r -> !r.isKey(playerWW.getRole().getKey()))
                .collect(Collectors.toList());

        if (role2List.isEmpty()) {
            return false;
        }

        IRole role2 = role2List.get(game.getRandom().nextInt(role2List.size()));

        List<String> roles = new ArrayList<>(Arrays.asList(
                role1.getKey(),
                role2.getKey(),
                playerWW.getRole().getDisplayRole()));

        Collections.shuffle(roles, game.getRandom());

        ExposedEvent exposedEvent = new ExposedEvent(playerWW,
                new ArrayList<>(Arrays.asList(role1.getPlayerWW(), role2.getPlayerWW(), playerWW)));

        Bukkit.getPluginManager().callEvent(exposedEvent);

        if (exposedEvent.isCancelled()) {
            return false;
        }

        roles = roles.stream().map(game::translate).collect(Collectors.toList());

        Bukkit.broadcastMessage(game.translate("werewolf.random_events.exposed.message",
                Formatter.player(playerWW.getName()),
                Formatter.format("&role1&", roles.get(0)),
                Formatter.format("&role2&", roles.get(1)),
                Formatter.format("&role3&", roles.get(2))));

        return true;
    }
}

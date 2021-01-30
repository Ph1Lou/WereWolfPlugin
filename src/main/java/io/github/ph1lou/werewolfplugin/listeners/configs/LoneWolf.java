package io.github.ph1lou.werewolfplugin.listeners.configs;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;

public class LoneWolf extends ListenerManager {

    public LoneWolf(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void designAloneWolf(WereWolfListEvent event) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (game.getConfig().getRoleCount(RolesBase.WHITE_WEREWOLF.getKey()) > 0) return;

        List<Roles> roleWWs = game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(PlayerWW::getRole)
                .filter(Roles::isWereWolf)
                .collect(Collectors.toList());

        if (roleWWs.isEmpty()) return;

        Roles role = roleWWs.get((int) Math.floor(game.getRandom().nextDouble() * roleWWs.size()));

        role.getPlayerWW().sendMessageWithKey("werewolf.lone_wolf.message");

        role.getPlayerWW().addPlayerMaxHealth(10);

        role.setTransformedToNeutral(true);
    }
}

package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Metamorph extends RolesVillage {

    public Metamorph(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.metamorph.description");
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onRequestOnWereWolfList(AppearInWereWolfListEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        if (Objects.requireNonNull(game.getPlayerWW(getPlayerUUID()))
                .isState(StatePlayer.DEATH)) return;

        event.setAppear(true);
    }

}

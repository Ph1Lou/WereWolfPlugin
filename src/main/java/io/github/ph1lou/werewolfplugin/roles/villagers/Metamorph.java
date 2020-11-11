package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class Metamorph extends RolesVillage {

    public Metamorph(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main, game, uuid, key);
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.metamorph.description");
    }

    @EventHandler
    public void onRequestOnWereWolfList(AppearInWereWolfListEvent event) {

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if (Objects.requireNonNull(game.getPlayerWW(getPlayerUUID()))
                .isState(StatePlayer.DEATH)) return;

        event.setAppear(true);
    }

}

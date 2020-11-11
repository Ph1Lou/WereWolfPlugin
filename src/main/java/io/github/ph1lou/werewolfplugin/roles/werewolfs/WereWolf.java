package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WereWolf extends RolesWereWolf {

    public WereWolf(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.werewolf.description");
    }


}

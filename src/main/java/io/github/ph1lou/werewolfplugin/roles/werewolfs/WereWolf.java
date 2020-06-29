package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;

import java.util.UUID;

public class WereWolf extends RolesWereWolf {

    public WereWolf(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.werewolf.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.werewolf.display";
    }


}

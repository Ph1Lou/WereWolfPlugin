package io.github.ph1lou.werewolfplugin.classesroles.werewolfroles;


import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesWereWolf;

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

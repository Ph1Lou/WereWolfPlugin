package io.github.ph1lou.werewolfplugin.classesroles.villageroles;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;

import java.util.UUID;

public class Villager extends RolesVillage {
    public Villager(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.villager.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.villager.display";
    }
}

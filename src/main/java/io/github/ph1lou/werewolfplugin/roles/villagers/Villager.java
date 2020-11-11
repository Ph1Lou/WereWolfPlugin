package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Villager extends RolesVillage {
    public Villager(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.villager.description");
    }

}

package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.jetbrains.annotations.NotNull;

public class Villager extends RolesVillage {
    public Villager(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.villager.description");
    }


    @Override
    public void recoverPower() {

    }

}

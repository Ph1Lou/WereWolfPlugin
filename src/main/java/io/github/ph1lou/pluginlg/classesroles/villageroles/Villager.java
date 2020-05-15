package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;

import java.util.UUID;

public class Villager extends RolesVillage {
    public Villager(GameManager game, UUID uuid) {
        super(game,uuid);
    }

    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.VILLAGER;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.villager.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.villager.display");
    }
}

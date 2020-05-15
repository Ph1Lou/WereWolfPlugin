package io.github.ph1lou.pluginlg.classesroles.werewolfroles;


import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;

import java.util.UUID;

public class WereWolf extends RolesWereWolf {

    public WereWolf(GameManager game, UUID uuid) {
        super(game,uuid);
    }


    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.WEREWOLF;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.werewolf.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.werewolf.display");
    }


}

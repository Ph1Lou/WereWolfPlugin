package io.github.ph1lou.pluginlg.classesroles.neutralroles;


import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;

import java.util.UUID;

public class FallenAngel extends Angel {

    public FallenAngel(GameManager game, UUID uuid) {
        super(game,uuid);
        setChoice(RoleLG.FALLEN_ANGEL);
        setPower(false);
    }


    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.FALLEN_ANGEL;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.fallen_angel.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.fallen_angel.display");
    }
}

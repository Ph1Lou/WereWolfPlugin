package io.github.ph1lou.pluginlg.classesroles.neutralroles;


import io.github.ph1lou.pluginlg.classesroles.LimitedUse;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;

import java.util.UUID;

public class GuardianAngel extends Angel implements LimitedUse {

    private int use = 0;

    public GuardianAngel(GameManager game, UUID uuid) {
        super(game,uuid);
        setChoice(RoleLG.GUARDIAN_ANGEL);
        setPower(false);
    }
    @Override
    public int getUse() {
        return use;
    }

    @Override
    public void setUse(int use) {
        this.use = use;
    }

    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.GUARDIAN_ANGEL;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.guardian_angel.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.guardian_angel.display");
    }

}

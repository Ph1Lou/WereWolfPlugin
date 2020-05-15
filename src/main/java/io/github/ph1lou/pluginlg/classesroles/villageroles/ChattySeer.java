package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;

import java.util.UUID;

public class ChattySeer extends Seer {

    public ChattySeer(GameManager game, UUID uuid) {
        super(game,uuid);
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.chatty_seer.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.chatty_seer.display");
    }

    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.CHATTY_SEER;
    }
}

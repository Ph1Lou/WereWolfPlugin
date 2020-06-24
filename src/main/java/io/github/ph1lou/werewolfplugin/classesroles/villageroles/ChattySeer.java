package io.github.ph1lou.werewolfplugin.classesroles.villageroles;


import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;

import java.util.UUID;

public class ChattySeer extends Seer {

    public ChattySeer(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.chatty_seer.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.chatty_seer.display";
    }

}

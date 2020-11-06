package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.AngelForm;
import io.github.ph1lou.werewolfapi.events.AngelChoiceEvent;
import org.bukkit.Bukkit;

import java.util.UUID;

public class FallenAngel extends Angel {

    public FallenAngel(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main, game, uuid, key);
        setChoice(AngelForm.FALLEN_ANGEL);
        Bukkit.getPluginManager().callEvent(
                new AngelChoiceEvent(getPlayerUUID(), AngelForm.FALLEN_ANGEL));
    }
}

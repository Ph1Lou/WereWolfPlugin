package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.AngelForm;
import io.github.ph1lou.werewolfapi.events.AngelChoiceEvent;
import org.bukkit.Bukkit;

import java.util.UUID;

public class GuardianAngel extends Angel  {


    public GuardianAngel(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
        setChoice(AngelForm.GUARDIAN_ANGEL);
        Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(getPlayerUUID(),AngelForm.GUARDIAN_ANGEL));
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.guardian_angel.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.guardian_angel.display";
    }

}

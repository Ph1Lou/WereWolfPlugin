package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.AngelForm;
import io.github.ph1lou.werewolfapi.events.AngelChoiceEvent;
import org.bukkit.Bukkit;

public class FallenAngel extends Angel {

    public FallenAngel(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
        setChoice(AngelForm.FALLEN_ANGEL);
        Bukkit.getPluginManager().callEvent(
                new AngelChoiceEvent(getPlayerWW(), AngelForm.FALLEN_ANGEL));
    }
}

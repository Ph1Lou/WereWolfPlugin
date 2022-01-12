package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.AngelForm;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import org.bukkit.Bukkit;

public class FallenAngel extends Angel {

    public FallenAngel(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
        setChoice(AngelForm.FALLEN_ANGEL);
        Bukkit.getPluginManager().callEvent(
                new AngelChoiceEvent(this.getPlayerWW(), AngelForm.FALLEN_ANGEL));
    }
}

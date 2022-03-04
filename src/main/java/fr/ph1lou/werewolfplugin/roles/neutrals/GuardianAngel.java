package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.AngelForm;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import org.bukkit.Bukkit;

public class GuardianAngel extends Angel {


    public GuardianAngel(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
        setChoice(AngelForm.GUARDIAN_ANGEL);
        Bukkit.getPluginManager().callEvent(
                new AngelChoiceEvent(this.getPlayerWW(), AngelForm.GUARDIAN_ANGEL));
    }
}

package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.AngelForm;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.Bukkit;

@Role(key = RoleBase.GUARDIAN_ANGEL,
        category = Category.NEUTRAL,
        attributes = RoleAttribute.NEUTRAL)
public class GuardianAngel extends Angel {


    public GuardianAngel(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
        setChoice(AngelForm.GUARDIAN_ANGEL);
        Bukkit.getPluginManager().callEvent(
                new AngelChoiceEvent(this.getPlayerWW(), AngelForm.GUARDIAN_ANGEL));
    }
}

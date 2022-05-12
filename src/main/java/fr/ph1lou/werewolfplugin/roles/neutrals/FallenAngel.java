package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.AngelForm;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import org.bukkit.Bukkit;

@Role(key = RoleBase.FALLEN_ANGEL,
        category = Category.NEUTRAL,
        attributes = {RoleAttribute.NEUTRAL})
public class FallenAngel extends Angel {

    public FallenAngel(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
        setChoice(AngelForm.FALLEN_ANGEL);
        Bukkit.getPluginManager().callEvent(
                new AngelChoiceEvent(this.getPlayerWW(), AngelForm.FALLEN_ANGEL));
    }
}

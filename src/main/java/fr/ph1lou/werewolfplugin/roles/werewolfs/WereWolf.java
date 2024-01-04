package fr.ph1lou.werewolfplugin.roles.werewolfs;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.jetbrains.annotations.NotNull;

@Role(key = RoleBase.WEREWOLF,
        defaultAura = Aura.DARK,
        category = Category.WEREWOLF,
        attributes = RoleAttribute.WEREWOLF)
public class WereWolf extends RoleWereWolf {

    public WereWolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }

    @Override
    public void recoverPower() {

    }
}

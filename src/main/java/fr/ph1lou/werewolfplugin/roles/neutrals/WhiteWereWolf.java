package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.jetbrains.annotations.NotNull;


@Role(key = RoleBase.WHITE_WEREWOLF,
        defaultAura = Aura.DARK,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        configValues = @IntValue(key = IntValueBase.WHITE_WEREWOLF_HEALTH_BONUS,
                defaultValue = 10, meetUpValue = 10, step = 2, item = UniversalMaterial.RED_DYE))
public class WhiteWereWolf extends RoleNeutral {

    public WhiteWereWolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }


    @Override
    public void recoverPower() {
        this.getPlayerWW().addPlayerMaxHealth(game.getConfig().getValue(IntValueBase.WHITE_WEREWOLF_HEALTH_BONUS));
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.white_werewolf.description"))
                .setPower(game.translate("werewolf.roles.white_werewolf.heart"))
                .build();
    }


    @Override
    public boolean isWereWolf() {
        return true;
    }

    @Override
    public boolean isNeutral() {
        return true;
    }
}

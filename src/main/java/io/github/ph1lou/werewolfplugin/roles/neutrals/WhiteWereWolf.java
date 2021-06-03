package io.github.ph1lou.werewolfplugin.roles.neutrals;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleNeutral;
import org.jetbrains.annotations.NotNull;

public class WhiteWereWolf extends RoleNeutral {

    public WhiteWereWolf(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }


    @Override
    public void recoverPower() {
        this.getPlayerWW().addPlayerMaxHealth(10);
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.white_werewolf.description"))
                .setPower(game.translate("werewolf.role.white_werewolf.heart"))
                .build();
    }


    @Override
    public boolean isWereWolf() {
        return true;
    }

    @Override
    public boolean isNeutral(){
        return true;
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.DARK;
    }
}

package io.github.ph1lou.werewolfplugin.roles.neutrals;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import org.jetbrains.annotations.NotNull;

public class WhiteWereWolf extends RolesNeutral {

    public WhiteWereWolf(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }


    @Override
    public void recoverPower() {
        getPlayerWW().addPlayerMaxHealth(10);
    }


    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description",
                        game.translate("werewolf.role.white_werewolf.description")) +
                game.translate("werewolf.description.power",
                        game.translate("werewolf.role.white_werewolf.heart"));
    }


    @Override
    public boolean isWereWolf() {
        return true;
    }

    @Override
    public boolean isNeutral(){
        return true;
    }
}

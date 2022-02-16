package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import org.jetbrains.annotations.NotNull;

public class Hunter extends RoleVillage {

    public Hunter(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return null;
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }




}

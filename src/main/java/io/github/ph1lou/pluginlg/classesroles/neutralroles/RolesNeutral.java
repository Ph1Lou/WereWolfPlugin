package io.github.ph1lou.pluginlg.classesroles.neutralroles;

import io.github.ph1lou.pluginlg.classesroles.RolesImpl;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class RolesNeutral extends RolesImpl {

    public RolesNeutral(GameManager game, UUID uuid) {
        super(game,uuid);
        this.setCamp(Camp.NEUTRAL);
    }

    @Override
    public void recoverPotionEffect(Player player) {
        super.recoverPotionEffect(player);
    }

    @Override
    public void recoverPower(Player player) {
    }

}

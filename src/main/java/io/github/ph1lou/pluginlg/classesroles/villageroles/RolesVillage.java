package io.github.ph1lou.pluginlg.classesroles.villageroles;

import io.github.ph1lou.pluginlg.classesroles.RolesImpl;
import io.github.ph1lou.pluginlg.game.GameManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class RolesVillage extends RolesImpl {

    public RolesVillage(GameManager game, UUID uuid) {
        super(game,uuid);
    }

    @Override
    public void recoverPower(Player player) {
    }

    @Override
    public void recoverPotionEffect(Player player) {
        super.recoverPotionEffect(player);
    }
}

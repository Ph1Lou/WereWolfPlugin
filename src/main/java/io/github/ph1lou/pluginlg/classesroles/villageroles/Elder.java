package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlg.classesroles.Power;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Elder extends RolesVillage implements Power {

    public Elder(GameManager game, UUID uuid) {
        super(game,uuid);
    }

    private boolean power=true;
    @Override
    public void setPower(Boolean power) {
        this.power=power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
    }

    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.ELDER;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.elder.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.elder.display");
    }

    @Override
    public void recoverPotionEffect(Player player) {
        if(!hasPower()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,0,false,false));
        super.recoverPotionEffect(player);
    }
}

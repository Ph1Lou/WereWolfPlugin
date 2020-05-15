package io.github.ph1lou.pluginlg.classesroles.werewolfroles;


import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class NaughtyLittleWolf extends RolesWereWolf {

    public NaughtyLittleWolf(GameManager game, UUID uuid) {
        super(game,uuid);
    }


    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.NAUGHTY_LITTLE_WOLF;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.naughty_little_wolf.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.naughty_little_wolf.display");
    }

    @Override
    public void recoverPotionEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE,0,false,false));
        super.recoverPotionEffect(player);
    }
}

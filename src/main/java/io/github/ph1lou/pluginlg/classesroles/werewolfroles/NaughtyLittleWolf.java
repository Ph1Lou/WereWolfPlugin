package io.github.ph1lou.pluginlg.classesroles.werewolfroles;


import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesWereWolf;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class NaughtyLittleWolf extends RolesWereWolf {

    public NaughtyLittleWolf(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.naughty_little_wolf.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.naughty_little_wolf.display";
    }

    @Override
    public void recoverPotionEffect(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE,0,false,false));
        super.recoverPotionEffect(player);
    }
}

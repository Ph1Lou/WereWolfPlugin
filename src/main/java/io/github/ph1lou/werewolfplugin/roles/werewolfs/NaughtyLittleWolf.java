package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class NaughtyLittleWolf extends RolesWereWolf {

    public NaughtyLittleWolf(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.naughty_little_wolf.description");
    }


    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                Integer.MAX_VALUE,
                0,
                false,
                false));
    }
}

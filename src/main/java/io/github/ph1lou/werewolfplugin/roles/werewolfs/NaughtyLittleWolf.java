package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class NaughtyLittleWolf extends RolesWereWolf {

    public NaughtyLittleWolf(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.naughty_little_wolf.description");
    }


    @Override
    public void recoverPower() {

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

package io.github.ph1lou.pluginlg.listener.roleslisteners;

import io.github.ph1lou.pluginlg.game.PlayerLG;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ListenerRolesWhiteWerewolf extends ListenerRoles {


    @Override
    public void onNight(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
    }

    @Override
    public void onDay(Player player, PlayerLG plg) {
        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    @Override
    public void onSelectionEnd(Player player, PlayerLG plg) {

    }

    @Override
    public void onDayWillCome(Player player) {

    }

    @Override
    public void onVoteEnd(Player player, PlayerLG plg) {

    }


}

package io.github.ph1lou.pluginlg.listener.gamelisteners.roleslisteners;

import io.github.ph1lou.pluginlg.game.PlayerLG;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class ListenerRolesMischievousWolf extends ListenerRoles {
    

    @Override
    public void onNight(Player player) {
        player.sendMessage(game.text.getText(14));
    }

    @Override
    public void onDay(Player player, PlayerLG plg) {
        if (!plg.hasPower()) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            plg.setPower(true);
            player.sendMessage(game.text.getText(18));
            game.optionlg.updateNameTag();
        }
    }

    @Override
    public void onSelectionEnd(Player player, PlayerLG plg) {

    }

    @Override
    public void onDayWillCome(Player player) {
        player.sendMessage(game.text.getText(197));
    }

    @Override
    public void onVoteEnd(Player player, PlayerLG plg) {

    }

}

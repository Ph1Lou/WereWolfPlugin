package io.github.ph1lou.pluginlg.listener.roleslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import org.bukkit.entity.Player;

public class ListenerRolesComedian implements ListenerRoles {

    MainLG main;

    public void init(MainLG main) {
        this.main = main;
    }

    @Override
    public void onNight(Player player) {

    }

    @Override
    public void onDay(Player player, PlayerLG plg) {
        plg.setPower(true);
        player.removePotionEffect(plg.getLastPotionEffect());
        if (plg.getPotionEffects().size() < 4) {
            player.sendMessage(String.format(main.text.powerUse.get(RoleLG.COMEDIEN), main.score.conversion(main.config.timerValues.get(TimerLG.POWER_DURATION))));

        }
    }

    @Override
    public void onSelectionEnd(Player player, PlayerLG plg) {
        if (plg.hasPower()) {
            plg.setPower(false);
            player.sendMessage(main.text.getText(13));
        }
    }

    @Override
    public void onDayWillCome(Player player) {

    }

    @Override
    public void onVoteEnd(Player player, PlayerLG plg) {

    }

}

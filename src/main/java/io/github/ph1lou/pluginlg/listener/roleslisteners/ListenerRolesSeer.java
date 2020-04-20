package io.github.ph1lou.pluginlg.listener.roleslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.entity.Player;

public class ListenerRolesSeer implements ListenerRoles {

    MainLG main;

    public void init(MainLG main) {
        this.main = main;
    }


    @Override
    public void onNight(Player player) {

    }

    @Override
    public void onDay(Player player, PlayerLG plg) {
        if (!main.config.configValues.get(ToolLG.SEER_EVERY_OTHER_DAY) || ((-main.config.timerValues.get(TimerLG.ROLE_DURATION)) / main.config.timerValues.get(TimerLG.DAY_DURATION) / 2) % 2 == 0) {
            plg.setPower(true);
            player.sendMessage(String.format(main.text.powerUse.get(RoleLG.VOYANTE), main.score.conversion(main.config.timerValues.get(TimerLG.POWER_DURATION))));
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

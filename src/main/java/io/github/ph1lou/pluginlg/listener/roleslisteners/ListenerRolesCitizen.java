package io.github.ph1lou.pluginlg.listener.roleslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import org.bukkit.entity.Player;

public class ListenerRolesCitizen implements ListenerRoles {

    MainLG main;

    public void init(MainLG main) {
        this.main = main;
    }

    @Override
    public void onNight(Player player) {

    }

    @Override
    public void onDay(Player player, PlayerLG plg) {

    }


    @Override
    public void onSelectionEnd(Player player, PlayerLG plg) {

    }

    @Override
    public void onDayWillCome(Player player) {

    }

    @Override
    public void onVoteEnd(Player player, PlayerLG plg) {
        if (plg.getUse() < 2 || plg.hasPower()) {
            player.sendMessage(String.format(main.text.powerUse.get(RoleLG.CITOYEN), plg.hasPower() ? 1 : 0, 2 - plg.getUse(), main.score.conversion(main.config.timerValues.get(TimerLG.CITIZEN_DURATION))));
        }
    }
}

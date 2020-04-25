package io.github.ph1lou.pluginlg.listener.gamelisteners.roleslisteners;

import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import org.bukkit.entity.Player;

public class ListenerRolesCitizen extends ListenerRoles {

  
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
            player.sendMessage(String.format(game.text.powerUse.get(RoleLG.CITOYEN), plg.hasPower() ? 1 : 0, 2 - plg.getUse(), game.score.conversion(game.config.timerValues.get(TimerLG.CITIZEN_DURATION))));
        }
    }
}

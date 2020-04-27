package io.github.ph1lou.pluginlg.listener.gamelisteners.roleslisteners;

import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import org.bukkit.entity.Player;

public class ListenerRolesRaven extends ListenerRoles {
    

    @Override
    public void onNight(Player player) {

    }

    @Override
    public void onDay(Player player, PlayerLG plg) {
        plg.setPower(true);
        player.sendMessage(String.format(game.text.powerUse.get(RoleLG.CORBEAU), game.score.conversion(game.config.timerValues.get(TimerLG.POWER_DURATION))));
    }


    @Override
    public void onSelectionEnd(Player player, PlayerLG plg) {
        if (plg.hasPower()) {
            plg.setPower(false);
            player.sendMessage(game.text.getText(13));
        }
    }

    @Override
    public void onDayWillCome(Player player) {

    }

    @Override
    public void onVoteEnd(Player player, PlayerLG plg) {

    }


}
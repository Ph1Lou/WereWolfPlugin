package io.github.ph1lou.pluginlg.listener.roleslisteners;

import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import org.bukkit.entity.Player;

public class ListenerRolesFox extends ListenerRoles {
    

    @Override
    public void onNight(Player player) {

    }

    @Override
    public void onDay(Player player, PlayerLG plg) {
        if (plg.getUse() < game.config.getUseOfFlair()) {
            plg.setPower(true);
            player.sendMessage(String.format(game.text.powerUse.get(RoleLG.RENARD), game.config.getUseOfFlair() - plg.getUse()));
        }

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

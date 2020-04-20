package io.github.ph1lou.pluginlg.listener.roleslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import org.bukkit.entity.Player;

public class ListenerRolesTrapper implements ListenerRoles {


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
        player.sendMessage(main.text.powerUse.get(RoleLG.TRAPPEUR));
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

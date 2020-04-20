package io.github.ph1lou.pluginlg.listener.roleslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListenerRolesFalsifierWolf implements ListenerRoles {

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

        List<String> players = new ArrayList<>();
        for (String p : main.playerLG.keySet()) {
            if (main.playerLG.get(p).isState(State.LIVING) && !p.equals(player.getName())) {
                players.add(p);
            }
        }
        String pc = players.get((int) Math.floor(Math.random() * players.size()));
        plg.setPosterCamp(main.playerLG.get(pc).getCamp());
        plg.setPosterRole(main.playerLG.get(pc).getRole());
        player.sendMessage(String.format(main.text.powerUse.get(RoleLG.LOUP_FEUTRE), main.text.translateRole.get(main.playerLG.get(pc).getRole())));
    }

    @Override
    public void onDayWillCome(Player player) {

    }

    @Override
    public void onVoteEnd(Player player, PlayerLG plg) {

    }

}

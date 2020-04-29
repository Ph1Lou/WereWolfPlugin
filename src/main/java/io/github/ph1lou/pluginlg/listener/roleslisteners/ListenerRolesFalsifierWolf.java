package io.github.ph1lou.pluginlg.listener.roleslisteners;

import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListenerRolesFalsifierWolf extends ListenerRoles {


    @Override
    public void onNight(Player player) {

    }

    @Override
    public void onDay(Player player, PlayerLG plg) {

    }


    @Override
    public void onSelectionEnd(Player player, PlayerLG plg) {

        List<String> players = new ArrayList<>();
        for (String p : game.playerLG.keySet()) {
            if (game.playerLG.get(p).isState(State.LIVING) && !p.equals(player.getName())) {
                players.add(p);
            }
        }
        String pc = players.get((int) Math.floor(Math.random() * players.size()));
        plg.setPosterCamp(game.playerLG.get(pc).getCamp());
        plg.setPosterRole(game.playerLG.get(pc).getRole());
        player.sendMessage(String.format(game.text.powerUse.get(RoleLG.LOUP_FEUTRE), game.text.translateRole.get(game.playerLG.get(pc).getRole())));
    }

    @Override
    public void onDayWillCome(Player player) {

    }

    @Override
    public void onVoteEnd(Player player, PlayerLG plg) {

    }

}

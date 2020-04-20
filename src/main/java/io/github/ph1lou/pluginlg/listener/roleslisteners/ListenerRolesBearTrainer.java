package io.github.ph1lou.pluginlg.listener.roleslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.enumlg.Camp;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ListenerRolesBearTrainer implements ListenerRoles {

    MainLG main;


    public void init(MainLG main) {
        this.main = main;
    }

    @Override
    public void onNight(Player player) {

    }

    @Override
    public void onDay(Player player, PlayerLG plg) {

        StringBuilder builder = new StringBuilder();
        boolean ok = false;

        Location oursLocation = player.getLocation();

        for (Player pls : Bukkit.getOnlinePlayers()) {

            if (main.playerLG.containsKey(pls.getName())) {

                PlayerLG plo = main.playerLG.get(pls.getName());

                if (!plo.isRole(RoleLG.LOUP_FEUTRE) || plo.isPosterCamp(Camp.LG)) {
                    if ((plo.isCamp(Camp.LG) || plo.isRole(RoleLG.LOUP_GAROU_BLANC)) && plo.isState(State.LIVING)) {
                        if (oursLocation.distance(pls.getLocation()) < main.config.getDistanceBearTrainer()) {
                            builder.append(main.text.powerHasBeenUse.get(RoleLG.MONTREUR_OURS));
                            ok = true;
                        }
                    }
                }
            }

        }
        if (ok) {
            Bukkit.broadcastMessage(String.format(main.text.powerUse.get(RoleLG.MONTREUR_OURS), builder.toString()));
            for (Player pls : Bukkit.getOnlinePlayers()) {
                pls.playSound(pls.getLocation(), Sound.WOLF_GROWL, 1, 20);
            }
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

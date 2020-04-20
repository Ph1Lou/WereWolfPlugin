package io.github.ph1lou.pluginlg.listener.roleslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import org.bukkit.entity.Player;

public interface ListenerRoles {

    void onNight(Player player);

    void onDay(Player player, PlayerLG plg);

    void onSelectionEnd(Player player, PlayerLG plg);

    void onDayWillCome(Player player);

    void onVoteEnd(Player player, PlayerLG plg);

    void init(MainLG main);
}

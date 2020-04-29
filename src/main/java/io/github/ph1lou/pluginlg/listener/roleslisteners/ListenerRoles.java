package io.github.ph1lou.pluginlg.listener.roleslisteners;

import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import org.bukkit.entity.Player;

public abstract class ListenerRoles {

    public GameManager game;

    public abstract void onNight(Player player);

    public abstract void onDay(Player player, PlayerLG plg);

    public abstract void onSelectionEnd(Player player, PlayerLG plg);

    public abstract void onDayWillCome(Player player);

    public abstract void onVoteEnd(Player player, PlayerLG plg);

    public void init(GameManager game){
        this.game=game;
    }
}

package io.github.ph1lou.werewolfplugin.listeners.configs;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.events.WinConditionsCheckEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class VictoryLovers extends ListenerManager {

    public VictoryLovers(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDetectVictoryCancel(WinConditionsCheckEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI game = main.getWereWolfAPI();

        if (game.getLoversManager().getLovers().stream()
                .filter(LoverAPI::isAlive).anyMatch(loverAPI -> loverAPI.isKey(RolesBase.LOVER.getKey()))) {
            event.setCancelled(true);
            return;
        }

        if (game.getLoversManager().getLovers().stream()
                .filter(LoverAPI::isAlive).anyMatch(loverAPI -> loverAPI.isKey(RolesBase.AMNESIAC_WEREWOLF.getKey()))) {
            event.setCancelled(true);
        }
    }


}

package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

public class NoCleanUp extends ListenerManager {

    public NoCleanUp(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerDeath(PlayerDeathEvent event) {

        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        killer.setHealth(Math.min(killer.getHealth() + 4,
                VersionUtils.getVersionUtils().getPlayerMaxHealth(killer)));
    }
}

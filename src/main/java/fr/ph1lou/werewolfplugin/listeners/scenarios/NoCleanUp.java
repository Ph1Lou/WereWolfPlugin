package fr.ph1lou.werewolfplugin.listeners.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
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

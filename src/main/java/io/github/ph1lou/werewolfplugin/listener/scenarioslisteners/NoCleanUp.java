package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

public class NoCleanUp extends Scenarios {

    public NoCleanUp(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,key);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerDeath(PlayerDeathEvent event) {

        try {
            Player killer = event.getEntity().getKiller();
            killer.setHealth(Math.min(killer.getHealth() + 4, killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
        } catch (Exception ignored) {
        }
    }
}

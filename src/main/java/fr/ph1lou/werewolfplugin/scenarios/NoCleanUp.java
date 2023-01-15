package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

@Scenario(key = ScenarioBase.NO_CLEAN_UP)
public class NoCleanUp extends ListenerWerewolf {

    public NoCleanUp(WereWolfAPI main) {
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

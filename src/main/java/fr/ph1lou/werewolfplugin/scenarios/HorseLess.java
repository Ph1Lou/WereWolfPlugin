package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityMountEvent;

@Scenario(key = ScenarioBase.HORSE_LESS, defaultValue = true)
public class HorseLess extends ListenerManager {

    public HorseLess(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {

        if (event.getEntity() instanceof Player) {
            if (event.getMount() instanceof Horse) {
                event.setCancelled(true);
            }
        }
    }
}

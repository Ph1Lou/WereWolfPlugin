package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityMountEvent;

public class HorseLess extends Scenarios {


    public HorseLess(Main main, GameManager game, ScenarioLG horseLess) {
        super(main, game,horseLess);
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {

        if (event.getEntity() != null && event.getEntity() instanceof Player && event.getMount() != null && event.getMount() instanceof Horse) {
            event.setCancelled(true);
        }
    }
}

package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class NoEggSnowBall extends Scenarios {


    public NoEggSnowBall(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,key);
    }

    @EventHandler
    public void onProjectileThrownEvent(ProjectileLaunchEvent event) {

        if (event.getEntity() instanceof Snowball || event.getEntity() instanceof Egg) {
            event.setCancelled(true);
        }
    }

}

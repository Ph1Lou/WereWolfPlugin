package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

@Scenario(key = ScenarioBase.NO_EGG_SNOWBALL, defaultValue = true, meetUpValue = true)
public class NoEggSnowBall extends ListenerWerewolf {

    public NoEggSnowBall(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onProjectileThrownEvent(ProjectileLaunchEvent event) {

        if (event.getEntity() instanceof Snowball || event.getEntity() instanceof Egg) {
            event.setCancelled(true);
        }
    }

}

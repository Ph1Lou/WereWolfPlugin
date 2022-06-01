package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

@Scenario(key = ScenarioBase.COMPASS_MIDDLE, defaultValue = true, meetUpValue = true,
        incompatibleScenarios = ScenarioBase.COMPASS_TARGET_LAST_DEATH)
public class CompassMiddle extends ListenerWerewolf {

    public CompassMiddle(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (event.getNumber() != 1) return;

        Bukkit.getOnlinePlayers()
                .forEach(player -> player.setCompassTarget(player
                        .getWorld()
                        .getSpawnLocation()));
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setCompassTarget(player
                .getWorld()
                .getSpawnLocation());
    }

    @Override
    public void register(boolean isActive) {

        WereWolfAPI game = this.getGame();

        if (isActive) {
            if (!isRegister()) {
                BukkitUtils.registerListener(this);
                Bukkit.getOnlinePlayers()
                        .forEach(player -> player.setCompassTarget(player
                                .getWorld()
                                .getSpawnLocation()));
                register = true;
            }
        } else if (isRegister()) {

            register = false;
            HandlerList.unregisterAll(this);
            Bukkit.getOnlinePlayers()
                    .forEach(player -> game.getPlayerWW(player.getUniqueId())
                            .ifPresent(playerWW -> player.setCompassTarget(playerWW.getSpawn())));

        }
    }
}

package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashMap;
import java.util.UUID;

@Scenario(key = ScenarioBase.DOUBLE_JUMP)
public class DoubleJump extends ListenerWerewolf {

    private final HashMap<UUID, Long> jumpTime = new HashMap<>();

    public DoubleJump(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {

            Player player = (Player) entity;
            UUID uuid = player.getUniqueId();
            if (this.jumpTime.containsKey(uuid)) {
                long secs = (System.currentTimeMillis() - this.jumpTime.get(uuid)) / 1000L;
                if (secs > 15L) {
                    this.jumpTime.remove(uuid);
                } else if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onPlayerToggleFlight(PlayerToggleFlightEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);

        double vel = 0.5;
        player.setVelocity(player.getLocation().getDirection().multiply(vel).setY(1));

        this.jumpTime.put(uuid, System.currentTimeMillis());
        BukkitUtils.scheduleSyncDelayedTask(this.getGame(), () -> {
            if (player.isFlying()) {
                player.setAllowFlight(false);
            }
        }, 20L);
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        if (player.getGameMode() != GameMode.CREATIVE &&
                location.subtract(0.0, 1.0, 0.0).getBlock().getType() != Material.AIR) {
            player.setAllowFlight(true);
        }
    }


    @Override
    public void register(boolean isActive) {

        super.register(isActive);

        if (isActive) return;

        Bukkit.getOnlinePlayers()
                .forEach(player -> player.setAllowFlight(false));
    }
}

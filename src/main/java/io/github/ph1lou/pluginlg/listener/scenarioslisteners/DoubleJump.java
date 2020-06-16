package io.github.ph1lou.pluginlg.listener.scenarioslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashMap;
import java.util.UUID;

public class DoubleJump extends Scenarios  {

    private final HashMap<UUID, Long> jumpTime = new HashMap<>();


    public DoubleJump(MainLG main, GameManager game, ScenarioLG scenario) {
        super(main, game, scenario);
    }

    @EventHandler
    private void onDamage(final EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (entity instanceof Player) {

            Player player = (Player)entity;
            UUID uuid = player.getUniqueId();
            if (this.jumpTime.containsKey(uuid)) {
                long secs = (System.currentTimeMillis() - this.jumpTime.get(uuid)) / 1000L;
                if (secs > 15L) {
                    this.jumpTime.remove(uuid);
                }
                else if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onPlayerToggleFlight(final PlayerToggleFlightEvent e) {

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        e.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);

        double vel = 0.5;
        player.setVelocity(player.getLocation().getDirection().multiply(vel).setY(1));

        this.jumpTime.put(uuid, System.currentTimeMillis());
        Bukkit.getScheduler().runTaskLater(main, () -> {
            if (player.isFlying()) {
                player.setAllowFlight(false);
            }
        }, 20L);
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        if (player.getGameMode() != GameMode.CREATIVE && location.subtract(0.0, 1.0, 0.0).getBlock().getType() != Material.AIR) {
            player.setAllowFlight(true);
        }
    }

    public void register() {
        if (game.getConfig().getScenarioValues().get(scenario)) {
            if (!register) {
                Bukkit.getPluginManager().registerEvents(this, main);
                register = true;
            }
        } else {
            if (register) {
                HandlerList.unregisterAll(this);
                register = false;
                for (Player p:Bukkit.getOnlinePlayers()){
                    p.setAllowFlight(false);
                }
            }
        }
    }
}

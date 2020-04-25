package io.github.ph1lou.pluginlg.listener.gamelisteners;

import io.github.ph1lou.pluginlg.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class PatchPotions implements Listener {

    final GameManager game;

    public PatchPotions(GameManager game) {
        this.game = game;
    }

    @EventHandler
    private void onNerfStrength(EntityDamageByEntityEvent event) {

        if(!event.getEntity().getWorld().equals(game.getWorld())) return;

        if (!(event.getEntity() instanceof Player)) return;

        if (!(event.getDamager() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        Player player = (Player) event.getEntity();


        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            event.setDamage(event.getDamage() * game.config.getStrengthRate() / 100f);
        }
        if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            if (game.config.getResistanceRate() >= 100) {
                event.setCancelled(true);
            }
            event.setDamage(event.getDamage() * (100 - game.config.getResistanceRate()) / 80f);
        }
    }
}

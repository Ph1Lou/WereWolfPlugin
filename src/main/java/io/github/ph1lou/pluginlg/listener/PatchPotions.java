package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.MainLG;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class PatchPotions implements Listener {

    final MainLG main;

    public PatchPotions(MainLG main) {
        this.main = main;
    }

    @EventHandler
    private void onNerfStrength(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (!(event.getDamager() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        Player player = (Player) event.getEntity();


        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            event.setDamage(event.getDamage() * main.config.getStrengthRate() / 100f);
        }
        if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            if (main.config.getResistanceRate() >= 100) {
                event.setCancelled(true);
            }
            event.setDamage(event.getDamage() * (100 - main.config.getResistanceRate()) / 80f);
        }
    }
}

package io.github.ph1lou.werewolfplugin.listeners;


import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PatchPotions implements Listener {

    private final WereWolfAPI game;

    public PatchPotions(WereWolfAPI game) {
        this.game = game;
    }

    @EventHandler
    private void onPatchPotion(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (!(event.getDamager() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        Player player = (Player) event.getEntity();

        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {

            if (damager.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)).map(PotionEffect::getAmplifier).findFirst().orElse(-1) == 0) {
                event.setDamage(event.getDamage() / 2.3f *
                        (1 + game.getConfig().getStrengthRate() / 100f));
            } else event.setDamage(event.getDamage() *
                    (1 + game.getConfig().getStrengthRate() / 100f));
        }
        if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            if (game.getConfig().getResistanceRate() >= 100) {
                event.setCancelled(true);
            }
            event.setDamage(event.getDamage() * (100 - game.getConfig().getResistanceRate()) / 80f);
        }
    }
}

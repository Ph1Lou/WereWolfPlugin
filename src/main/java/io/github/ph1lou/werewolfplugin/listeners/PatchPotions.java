package io.github.ph1lou.werewolfplugin.listeners;


import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

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


    @EventHandler
    public void onEffectGet(PotionSplashEvent event){

        event.setCancelled(true);
        event.getAffectedEntities().stream()
                .map(livingEntity -> game.getPlayerWW(livingEntity.getUniqueId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(playerWW -> event.getPotion().getEffects().forEach(potionEffect -> playerWW.addPotionModifier(PotionModifier.add(
                        potionEffect.getType(),
                        potionEffect.getDuration(),
                        potionEffect.getAmplifier(),
                        "splash_potion"))));
    }

    @EventHandler
    public void onDrinkPotionEvent(PlayerItemConsumeEvent event){

        Player player = event.getPlayer();

        if(event.getItem().getItemMeta() instanceof PotionMeta)
        {
            event.setCancelled(true);
            PotionMeta potionMeta = (PotionMeta) event.getItem().getItemMeta();

            BukkitUtils.scheduleSyncDelayedTask(() ->
            {
                PlayerInventory inventory = player.getInventory();
                inventory.remove(event.getItem());
                inventory.addItem(new ItemStack(Material.GLASS_BOTTLE));
            });

            game.getPlayerWW(player.getUniqueId())
                    .ifPresent(playerWW -> potionMeta.getCustomEffects().forEach(potionEffect -> playerWW.addPotionModifier(PotionModifier.add(potionEffect.getType(),
                            potionEffect.getDuration(),
                            potionEffect.getAmplifier(),
                            "potion_drink"))));
        }
    }
}

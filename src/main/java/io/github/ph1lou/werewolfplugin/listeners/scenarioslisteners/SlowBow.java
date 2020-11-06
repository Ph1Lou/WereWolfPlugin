package io.github.ph1lou.werewolfplugin.listeners.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class SlowBow extends Scenarios {


    public SlowBow(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,key);
    }

    @EventHandler
    private void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Arrow)) return;

        ProjectileSource damager = ((Arrow) event.getDamager()).getShooter();

        if (!(damager instanceof Player)) return;
        Player player = (Player) event.getEntity();

        ((Player) damager).addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                160,
                0,
                false,
                false));
        player.removePotionEffect(PotionEffectType.SLOW);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW,
                160,
                0,
                false,
                false));
    }


}

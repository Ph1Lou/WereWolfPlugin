package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

@Scenario(key = ScenarioBase.SLOW_BOW)
public class SlowBow extends ListenerWerewolf {

    public SlowBow(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Arrow)) return;

        ProjectileSource damager = ((Arrow) event.getDamager()).getShooter();

        if (!(damager instanceof Player)) return;
        Player player = (Player) event.getEntity();

        ((Player) damager).addPotionEffect(new PotionEffect(
                UniversalPotionEffectType.SPEED.getPotionEffectType(),
                160,
                0,
                false,
                false));
        player.removePotionEffect(UniversalPotionEffectType.SLOWNESS.getPotionEffectType());
        player.addPotionEffect(new PotionEffect(
                UniversalPotionEffectType.SLOWNESS.getPotionEffectType(),
                160,
                0,
                false,
                false));
    }
}

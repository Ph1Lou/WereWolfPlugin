package io.github.ph1lou.pluginlg.listener.scenarioslistener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlowBow extends Scenarios {


    @EventHandler
    private void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Arrow)) return;

        if (!(((Arrow) event.getDamager()).getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        player.removePotionEffect(PotionEffectType.SLOW);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 0, false, false));
    }


}

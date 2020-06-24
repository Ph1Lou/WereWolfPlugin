package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlowBow extends Scenarios {


    public SlowBow(Main main, GameManager game, ScenarioLG slowBow) {
        super(main, game,slowBow);
    }

    @EventHandler
    private void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Arrow)) return;

        if (!(((Arrow) event.getDamager()).getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Player damager = (Player) ((Arrow) event.getDamager()).getShooter();
        damager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,160,0,false,false));
        player.removePotionEffect(PotionEffectType.SLOW);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 0, false, false));
    }


}

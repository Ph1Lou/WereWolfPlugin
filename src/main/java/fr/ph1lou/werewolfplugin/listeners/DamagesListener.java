package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.game.PlayerWW;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class DamagesListener implements Listener {

    private final WereWolfAPI game;

    public DamagesListener(WereWolfAPI game){
        this.game = game;
    }

    @EventHandler
    private void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {

        if(!game.isState(StateGame.GAME)){
            return;
        }

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        Player striker;


        if (!(event.getDamager() instanceof Player)) {

            if (!(event.getDamager() instanceof Arrow)) return;

            ProjectileSource shooter = ((Arrow) event.getDamager()).getShooter();

            if (!(shooter instanceof Player)) return;

            striker = (Player) shooter;
        }
        else{
            striker = (Player) event.getDamager();
        }

        game.getPlayerWW(player.getUniqueId())
                .ifPresent(playerWW -> game.getPlayerWW(striker.getUniqueId())
                        .ifPresent(strikerWW -> {
                            ((PlayerWW)playerWW).addLastMinutesDamagedPlayer(strikerWW);
                            BukkitUtils.scheduleSyncDelayedTask(game, () -> ((PlayerWW)playerWW).removeLastMinutesDamagedPlayer(strikerWW),60 * 20);
                        }));
    }

}

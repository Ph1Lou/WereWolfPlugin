package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.events.game.honor.HonorChangeEvent;
import fr.ph1lou.werewolfapi.events.werewolf.RequestStrengthRateEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class DebugListener implements Listener {

    private final GameManager gameManager;

    public DebugListener(WereWolfAPI gameManager) {
        this.gameManager = (GameManager) gameManager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onHonorChange(HonorChangeEvent honorChangeEvent) {
        if (gameManager.isDebug()) {
            Bukkit.broadcastMessage(String.format("[DEBUG] Le joueur %s a maintenant un honneur de %d",
                    honorChangeEvent.getPlayerWW().getName(),
                    honorChangeEvent.getPlayerWW().getHonor()));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onHonorChange(RequestStrengthRateEvent requestStrengthRateEvent) {
        if (gameManager.isDebug()) {
            Bukkit.broadcastMessage(String.format("[DEBUG] Le joueur %s a maintenant un taux de force de %d%%",
                    requestStrengthRateEvent.getPlayerWW().getName(),
                    requestStrengthRateEvent.getStrengthRate()));
        }
    }
}

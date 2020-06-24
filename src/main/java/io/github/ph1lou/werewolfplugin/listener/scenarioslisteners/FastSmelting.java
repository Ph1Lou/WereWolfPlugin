package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class FastSmelting extends Scenarios {


    public FastSmelting(Main main, GameManager game, ScenarioLG fastSmelting) {
        super(main, game,fastSmelting);
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {

        Furnace block = (Furnace) event.getBlock().getState();
        new BukkitRunnable() {
            public void run() {
                if (block.getCookTime() > 0 || block.getBurnTime() > 0) {
                    block.setCookTime((short) (block.getCookTime() + 8));
                    block.update();
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(main, 1L, 1L);
    }

}

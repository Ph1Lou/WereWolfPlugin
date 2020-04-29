package io.github.ph1lou.pluginlg.listener.scenarioslisteners;

import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class FastSmelting extends Scenarios {


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

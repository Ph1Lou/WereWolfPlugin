package io.github.ph1lou.werewolfplugin.listeners.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FastSmelting extends Scenarios {


    public FastSmelting(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,key);}

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
        }.runTaskTimer((Plugin) main, 1L, 1L);
    }

}

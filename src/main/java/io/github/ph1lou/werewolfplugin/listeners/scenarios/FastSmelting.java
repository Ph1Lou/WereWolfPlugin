package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FastSmelting extends ListenerManager {


    public FastSmelting(GetWereWolfAPI main) {
        super(main);
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
        }.runTaskTimer(JavaPlugin.getPlugin(Main.class), 1L, 1L);
    }

}

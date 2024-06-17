package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@Scenario(key = ScenarioBase.FAST_SMELTING, defaultValue = true)
public class FastSmelting extends ListenerWerewolf {

    public FastSmelting(WereWolfAPI main) {
        super(main);
    }


    @EventHandler
    public void onFurnacePlace(BlockPlaceEvent event) {

        if(event.getBlockPlaced().getType() == UniversalMaterial.FURNACE.getType()){
            try {
                event.getBlockPlaced().setType(Material.valueOf("BLAST_FURNACE"));
            }
            catch (Exception ignored){
            }
        }
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {

        Furnace block = (Furnace) event.getBlock().getState();

        if(block.getType().name().contains("BLAST_FURNACE")){
            return;
        }

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

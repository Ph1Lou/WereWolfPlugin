package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Scenario(key = ScenarioBase.TIMBER)
public class Timber extends ListenerWerewolf {

    public Timber(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        if(this.getGame().getConfig().getTimerValue(TimerBase.PVP) <= 0){
            return;
        }

        Player player = event.getPlayer();
        Material mat = event.getBlock().getType();
        if (UniversalMaterial.isLog(mat)) {
            List<Block> bList = new ArrayList<>();
            List<ItemStack> finalItems = new ArrayList<>();
            bList.add(event.getBlock());
            new BukkitRunnable() {
                public void run() {
                    for (int i = 0; i < bList.size(); ++i) {
                        Block block = bList.get(i);

                        if (UniversalMaterial.isLog(block.getType())) {
                            List<ItemStack> items = new ArrayList<>(block.getDrops());
                            block.setType(Material.AIR);
                            finalItems.addAll(items);
                        }
                            BlockFace[] values;
                            for (int length = (values = BlockFace.values()).length, j = 0; j < length; ++j) {
                                BlockFace face = values[j];
                                if (UniversalMaterial.isLog(block.getRelative(face).getType())) {
                                    bList.add(block.getRelative(face));
                                }
                            }
                        bList.remove(block);
                    }
                    if (bList.size() == 0) {
                        for (ItemStack item2 : finalItems) {
                            player.getWorld().dropItemNaturally(event.getBlock().getLocation(), item2);
                        }
                        this.cancel();
                    }
                }
            }.runTaskTimer(JavaPlugin.getPlugin(Main.class), 1L, 1L);
        }
    }
}

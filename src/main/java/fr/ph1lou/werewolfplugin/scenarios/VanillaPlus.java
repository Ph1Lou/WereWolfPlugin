package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Scenario(key = ScenarioBase.VANILLA_PLUS, defaultValue = true,
        loreKey = {"werewolf.menu.advanced_tool.ender_pearl_lore",
                "werewolf.menu.advanced_tool.flint_lore",
                "werewolf.menu.advanced_tool.apple_lore",
                "werewolf.menu.shift"},
        configValues = {
                @IntValue(key = VanillaPlus.FLINT, defaultValue = 10, meetUpValue = 0, step = 5,
                        item = UniversalMaterial.FLINT),
                @IntValue(key = VanillaPlus.PEARL, defaultValue = 30,
                        meetUpValue = 0,
                        step = 5, item = UniversalMaterial.ENDER_PEARL),
                @IntValue(key = VanillaPlus.APPLE, defaultValue = 2, meetUpValue = 0, step = 1, item = UniversalMaterial.APPLE)})
public class VanillaPlus extends ListenerManager {

    public static  final String FLINT = "werewolf.menu.advanced_tool.flint";
    public static  final String APPLE = "werewolf.menu.advanced_tool.apple";
    public static  final String PEARL = "werewolf.menu.advanced_tool.ender_pearl";

    public VanillaPlus(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onGravelBreak(BlockBreakEvent event) {

        WereWolfAPI game = this.getGame();

        Block block = event.getBlock();
        Location loc = new Location(block.getWorld(),
                block.getLocation().getBlockX() + 0.5,
                block.getLocation().getBlockY() + 0.5,
                block.getLocation().getBlockZ() + 0.5);

        if (block.getType().equals(Material.GRAVEL)) {
            block.setType(Material.AIR);
            if (Math.random() * 100 < game.getConfig().getValue(FLINT)) {
                block.getWorld().dropItem(loc, new ItemStack(Material.FLINT, 1));
            } else
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                        new ItemStack(Material.GRAVEL));
        }
    }

    @EventHandler
    public void onLeaveDecay(LeavesDecayEvent event) {

        WereWolfAPI game = this.getGame();

        event.getBlock().setType(Material.AIR);
        if (Math.random() * 100 < game.getConfig().getValue(APPLE)) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                    new ItemStack(Material.APPLE));
        }
    }

    @EventHandler
    public void onEnderManDeath(EntityDeathEvent event) {

        if (!event.getEntity().getType().equals(EntityType.ENDERMAN)) return;

        List<ItemStack> loots = event.getDrops();

        loots.clear();
        if (Math.random() * 100 < this.getGame().getConfig().getValue(PEARL)) {
            loots.add(new ItemStack(Material.ENDER_PEARL));
        }
    }
}

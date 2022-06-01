package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
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
        configValues = {
                @IntValue(key = IntValueBase.FLINT_RATE, defaultValue = 10, meetUpValue = 0, step = 5,
                        item = UniversalMaterial.FLINT),
                @IntValue(key = IntValueBase.PEARL_RATE, defaultValue = 30,
                        meetUpValue = 0,
                        step = 5, item = UniversalMaterial.ENDER_PEARL),
                @IntValue(key = IntValueBase.APPLE_RATE, defaultValue = 2, meetUpValue = 0, step = 1, item = UniversalMaterial.APPLE)})
public class VanillaPlus extends ListenerWerewolf {

    public VanillaPlus(WereWolfAPI main) {
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
            if (Math.random() * 100 < game.getConfig().getValue(IntValueBase.FLINT_RATE)) {
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
        if (Math.random() * 100 < game.getConfig().getValue(IntValueBase.APPLE_RATE)) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                    new ItemStack(Material.APPLE));
        }
    }

    @EventHandler
    public void onEnderManDeath(EntityDeathEvent event) {

        if (!event.getEntity().getType().equals(EntityType.ENDERMAN)) return;

        List<ItemStack> loots = event.getDrops();

        loots.clear();
        if (Math.random() * 100 < this.getGame().getConfig().getValue(IntValueBase.PEARL_RATE)) {
            loots.add(new ItemStack(Material.ENDER_PEARL));
        }
    }
}

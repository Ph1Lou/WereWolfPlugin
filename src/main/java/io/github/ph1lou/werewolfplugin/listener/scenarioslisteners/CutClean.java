package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.TimerLG;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class CutClean extends Scenarios {


    public CutClean(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game,key);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        final Location loc = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY() + 0.5, block.getLocation().getBlockZ() + 0.5);

        if (game.getConfig().getTimerValues().get(TimerLG.DIGGING) < 0) {
            List<Material> m = Arrays.asList(Material.REDSTONE_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE, Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE);

            if (m.contains(block.getType())) {
                block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(event.getExpToDrop());
                block.setType(Material.AIR);
            }
            return;
        }

        Material currentItemType = event.getPlayer().getItemOnCursor().getType();

        switch (block.getType()) {

            case COAL_ORE:

                if (!currentItemType.equals(Material.DIAMOND_PICKAXE) && !currentItemType.equals(Material.IRON_PICKAXE) && !currentItemType.equals(Material.STONE_PICKAXE) && !currentItemType.equals(Material.GOLDEN_PICKAXE) && !currentItemType.equals(Material.WOODEN_PICKAXE)) {
                    return;
                }
                block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(event.getExpToDrop());
                block.setType(Material.AIR);
                block.getWorld().dropItem(loc, new ItemStack(Material.TORCH, 4));
                break;


            case IRON_ORE:

                if (!currentItemType.equals(Material.DIAMOND_PICKAXE) && !currentItemType.equals(Material.IRON_PICKAXE) && !currentItemType.equals(Material.STONE_PICKAXE)) {
                    return;
                }
                block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(game.getConfig().getScenarioValues().get("werewolf.menu.scenarios.xp_boost") ? 1 : (int) (game.getConfig().getXpBoost() / 100f));
                block.setType(Material.AIR);
                block.getWorld().dropItem(loc, new ItemStack(Material.IRON_INGOT, 1));
                break;

            case GOLD_ORE:
                if (!currentItemType.equals(Material.DIAMOND_PICKAXE) && !currentItemType.equals(Material.IRON_PICKAXE)) {
                    return;
                }
                block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(game.getConfig().getScenarioValues().get("werewolf.menu.scenarios.xp_boost") ? 1 : (int) (game.getConfig().getXpBoost() / 100f));
                block.setType(Material.AIR);
                block.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT, 1));
                break;

            default:
                break;
        }
    }

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {

        List<ItemStack> loots = event.getDrops();

        for (int i = loots.size() - 1; i >= 0; --i) {
            ItemStack is = loots.get(i);
            if (is == null) {
                return;
            }
            switch (is.getType()) {
                case BEEF:
                    loots.remove(i);
                    loots.add(new ItemStack(Material.COOKED_BEEF));
                    break;

                case PORKCHOP:
                    loots.remove(i);
                    loots.add(new ItemStack(Material.COOKED_PORKCHOP));
                    break;

                case CHICKEN:
                    loots.remove(i);
                    loots.add(new ItemStack(Material.COOKED_CHICKEN));
                    break;

                case MUTTON:
                    loots.remove(i);
                    loots.add(new ItemStack(Material.COOKED_MUTTON));
                    break;

                case RABBIT:
                    loots.remove(i);
                    loots.add(new ItemStack(Material.COOKED_RABBIT));
                    break;
                default:

            }
        }
    }
}



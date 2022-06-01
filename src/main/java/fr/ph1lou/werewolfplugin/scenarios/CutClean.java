package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Scenario(key = ScenarioBase.CUT_CLEAN, defaultValue = true, meetUpValue = true)
public class CutClean extends ListenerWerewolf {

    public CutClean(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {

        WereWolfAPI game = this.getGame();
        Block block = event.getBlock();
        Location loc = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY() + 0.5, block.getLocation().getBlockZ() + 0.5);


        Material currentItemType = VersionUtils.getVersionUtils().getItemInHand(event.getPlayer()).getType();

        switch (block.getType()) {

            case COAL_ORE:

                if (!currentItemType.equals(Material.DIAMOND_PICKAXE) &&
                        !currentItemType.equals(Material.IRON_PICKAXE) &&
                        !currentItemType.equals(Material.STONE_PICKAXE) &&
                        !currentItemType.equals(UniversalMaterial.GOLDEN_PICKAXE.getType())
                        && !currentItemType.equals(UniversalMaterial.WOODEN_PICKAXE.getType())) {
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
                block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(game.getConfig().isScenarioActive(ScenarioBase.XP_BOOST) ? (int) (game.getConfig().getValue(IntValueBase.XP_BOOST) / 100f) : 1);
                block.setType(Material.AIR);
                block.getWorld().dropItem(loc, new ItemStack(Material.IRON_INGOT, 1));
                break;

            case GOLD_ORE:
                if (!currentItemType.equals(Material.DIAMOND_PICKAXE) && !currentItemType.equals(Material.IRON_PICKAXE)) {
                    return;
                }
                block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(game.getConfig().isScenarioActive(ScenarioBase.XP_BOOST) ? (int) (game.getConfig().getValue(IntValueBase.XP_BOOST) / 100f) : 1);
                block.setType(Material.AIR);
                block.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT, 1));
                break;

            default:
                break;
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        List<ItemStack> loots = event.getDrops();

        for (int i = loots.size() - 1; i >= 0; --i) {
            ItemStack is = loots.get(i);
            if (is == null) {
                return;
            }
            UniversalMaterial material = UniversalMaterial.ofType(is.getType());
            if (material == null) return;

            switch (material) {
                case RAW_BEEF:
                    loots.remove(i);
                    loots.add(new ItemStack(UniversalMaterial.COOKED_BEEF.getType()));
                    break;

                case RAW_PORK:
                    loots.remove(i);
                    loots.add(new ItemStack(UniversalMaterial.COOKED_PORKCHOP.getType()));
                    break;

                case RAW_CHICKEN:
                    loots.remove(i);
                    loots.add(new ItemStack(UniversalMaterial.COOKED_CHICKEN.getType()));
                    break;

                case RAW_MUTTON:
                    loots.remove(i);
                    loots.add(new ItemStack(UniversalMaterial.COOKED_MUTTON.getType()));
                    break;

                case RAW_RABBIT:
                    loots.remove(i);
                    loots.add(new ItemStack(UniversalMaterial.COOKED_RABBIT.getType()));
                    break;
                default:

            }
        }
    }
}



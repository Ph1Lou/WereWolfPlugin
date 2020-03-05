package io.github.ph1lou.pluginlg.listener;

import java.util.Random;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityMountEvent;

public class ScenarioListener implements Listener {

	final MainLG main;
	
	public ScenarioListener(MainLG main) {
		this.main=main;
	}
	
	@EventHandler
	public void onEntityMount(EntityMountEvent event) {
	    if (main.config.scenario.get(ScenarioLG.HORSE_LESS) && event.getEntity() != null && event.getEntity() instanceof Player && event.getMount() != null && event.getMount() instanceof Horse) {
	        event.setCancelled(true);
	    }
	}

	@EventHandler
    public void onLeaveDecay(LeavesDecayEvent event) {
        if (main.config.scenario.get(ScenarioLG.VANILLA_PLUS) && new Random(System.currentTimeMillis()).nextFloat()<main.config.getApple_rate()) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
        }
    }
	
	@EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
		
        if (event.getInventory() != null) {
        	final CraftingInventory inv = event.getInventory();
            final ItemStack AIR = new ItemStack(Material.AIR);
            if (inv.getResult().getType() == Material.GOLDEN_APPLE && inv.getResult().getDurability()== 1) {
                inv.setResult(AIR);
            }
            if (main.config.scenario.get(ScenarioLG.ROD_LESS) && inv.getResult().getType() == Material.FISHING_ROD) {
                inv.setResult(AIR);
            }
            if (main.config.scenario.get(ScenarioLG.HASTEY_BOYS)) {
            	
                Material itemType = event.getInventory().getResult().getType();
                switch (itemType) {
                   case WOOD_PICKAXE: {
                       ItemStack lo1 = new ItemStack(Material.WOOD_PICKAXE);
                       ItemMeta lo1M = lo1.getItemMeta();
                       lo1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       lo1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       lo1.setItemMeta(lo1M);
                       event.getInventory().setResult(lo1);
                       break;
                   }
                   case WOOD_AXE: {
                       ItemStack l1 = new ItemStack(Material.WOOD_AXE);
                       ItemMeta l1M = l1.getItemMeta();
                       l1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       l1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       l1.setItemMeta(l1M);
                       event.getInventory().setResult(l1);
                       break;
                   }
                   case WOOD_SPADE: {
                       ItemStack lj1 = new ItemStack(Material.WOOD_SPADE);
                       ItemMeta lj1M = lj1.getItemMeta();
                       lj1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       lj1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       lj1.setItemMeta(lj1M);
                       event.getInventory().setResult(lj1);
                       break;
                   }
                   case WOOD_HOE: {
                       ItemStack lI1 = new ItemStack(Material.WOOD_HOE);
                       ItemMeta lI1M = lI1.getItemMeta();
                       lI1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       lI1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       lI1.setItemMeta(lI1M);
                       event.getInventory().setResult(lI1);
                       break;
                   }
                   case STONE_PICKAXE: {
                       ItemStack lN1 = new ItemStack(Material.STONE_PICKAXE);
                       ItemMeta lN1M = lN1.getItemMeta();
                       lN1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       lN1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       lN1.setItemMeta(lN1M);
                       event.getInventory().setResult(lN1);
                       break;
                   }
                   case STONE_AXE: {
                       ItemStack le1 = new ItemStack(Material.STONE_AXE);
                       ItemMeta leN1M = le1.getItemMeta();
                       leN1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       leN1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       le1.setItemMeta(leN1M);
                       event.getInventory().setResult(le1);
                       break;
                   }
                   case STONE_SPADE: {
                       ItemStack lep1 = new ItemStack(Material.STONE_SPADE);
                       ItemMeta lepN1M = lep1.getItemMeta();
                       lepN1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       lepN1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       lep1.setItemMeta(lepN1M);
                       event.getInventory().setResult(lep1);
                       break;
                   }
                   case STONE_HOE: {
                       ItemStack leB1 = new ItemStack(Material.STONE_HOE);
                       ItemMeta leBN1M = leB1.getItemMeta();
                       leBN1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       leBN1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       leB1.setItemMeta(leBN1M);
                       event.getInventory().setResult(leB1);
                       break;
                   }
                   case IRON_PICKAXE: {
                       ItemStack lXe1 = new ItemStack(Material.IRON_PICKAXE);
                       ItemMeta lXeN1M = lXe1.getItemMeta();
                       lXeN1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       lXeN1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       lXe1.setItemMeta(lXeN1M);
                       event.getInventory().setResult(lXe1);
                       break;
                   }
                   case IRON_AXE: {
                       ItemStack leJ1 = new ItemStack(Material.IRON_AXE);
                       ItemMeta leJN1M = leJ1.getItemMeta();
                       leJN1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       leJN1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       leJ1.setItemMeta(leJN1M);
                       event.getInventory().setResult(leJ1);
                       break;
                   }
                   case IRON_SPADE: {
                       ItemStack lHe1 = new ItemStack(Material.IRON_SPADE);
                       ItemMeta leHN1M = lHe1.getItemMeta();
                       leHN1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       leHN1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       lHe1.setItemMeta(leHN1M);
                       event.getInventory().setResult(lHe1);
                       break;
                   }
                   case IRON_HOE: {
                       ItemStack lHej1 = new ItemStack(Material.IRON_HOE);
                       ItemMeta leHNj1M = lHej1.getItemMeta();
                       leHNj1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       leHNj1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       lHej1.setItemMeta(leHNj1M);
                       event.getInventory().setResult(lHej1);
                       break;
                   }
                   case GOLD_PICKAXE: {
                       ItemStack lnHej1 = new ItemStack(Material.GOLD_PICKAXE);
                       ItemMeta nleHNj1M = lnHej1.getItemMeta();
                       nleHNj1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       nleHNj1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       lnHej1.setItemMeta(nleHNj1M);
                       event.getInventory().setResult(lnHej1);
                       break;
                   }
                   case GOLD_AXE: {
                       ItemStack t1 = new ItemStack(Material.GOLD_AXE);
                       ItemMeta t1M = t1.getItemMeta();
                       t1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       t1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       t1.setItemMeta(t1M);
                       event.getInventory().setResult(t1);
                       break;
                   }
                   case GOLD_SPADE: {
                       ItemStack Jt1 = new ItemStack(Material.GOLD_SPADE);
                       ItemMeta Jt1M = Jt1.getItemMeta();
                       Jt1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       Jt1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       Jt1.setItemMeta(Jt1M);
                       event.getInventory().setResult(Jt1);
                       break;
                   }
                   case GOLD_HOE: {
                       ItemStack hJt1 = new ItemStack(Material.GOLD_HOE);
                       ItemMeta hJt1M = hJt1.getItemMeta();
                       hJt1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       hJt1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       hJt1.setItemMeta(hJt1M);
                       event.getInventory().setResult(hJt1);
                       break;
                   }
                   case DIAMOND_PICKAXE: {
                       ItemStack FlnHej1 = new ItemStack(Material.DIAMOND_PICKAXE);
                       ItemMeta FnleHNj1M = FlnHej1.getItemMeta();
                       FnleHNj1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       FnleHNj1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       FlnHej1.setItemMeta(FnleHNj1M);
                       event.getInventory().setResult(FlnHej1);
                       break;
                   }
                   case DIAMOND_AXE: {
                       ItemStack Qt1 = new ItemStack(Material.DIAMOND_AXE);
                       ItemMeta Qt1M = Qt1.getItemMeta();
                       Qt1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       Qt1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       Qt1.setItemMeta(Qt1M);
                       event.getInventory().setResult(Qt1);
                       break;
                   }
                   case DIAMOND_SPADE: {
                       ItemStack PJt1 = new ItemStack(Material.DIAMOND_SPADE);
                       ItemMeta PJt1M = PJt1.getItemMeta();
                       PJt1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       PJt1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       PJt1.setItemMeta(PJt1M);
                       event.getInventory().setResult(PJt1);
                       break;
                   }
                   case DIAMOND_HOE: {
                       ItemStack VhJt1 = new ItemStack(Material.DIAMOND_HOE);
                       ItemMeta VhJt1M = VhJt1.getItemMeta();
                       VhJt1M.addEnchant(Enchantment.DIG_SPEED, 3, true);
                       VhJt1M.addEnchant(Enchantment.DURABILITY, 3, true);
                       VhJt1.setItemMeta(VhJt1M);
                       event.getInventory().setResult(VhJt1);
                       break;
                   }
    			default:
    				break;
               }
           }
        }
    }
	
	@EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        if (main.config.scenario.get(ScenarioLG.FAST_SMELTING)) {
            handleCookingTime((Furnace)event.getBlock().getState());
        }
    }
    
    private void handleCookingTime(Furnace block) {
        new BukkitRunnable() {
            public void run() {
                if (block.getCookTime() > 0 || block.getBurnTime() > 0) {
                    block.setCookTime((short)(block.getCookTime() + 8));
                    block.update();
                }
                else {
                    this.cancel();
                }
            }
        }.runTaskTimer(main, 1L, 1L);
    }
    
    @EventHandler
    public void onDrinkMilk(PlayerInteractEvent event) {
        Action a = event.getAction();
        if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
        
            if(event.getPlayer().getInventory().getItemInHand().getType() == Material.MILK_BUCKET) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onProjectileThrownEvent(ProjectileLaunchEvent event) {
        if(event.getEntity() instanceof Snowball && !main.config.scenario.get(ScenarioLG.NO_SNOWBALL)) {
            event.setCancelled(true);
        }
    }

}
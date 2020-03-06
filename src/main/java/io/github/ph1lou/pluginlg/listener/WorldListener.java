package io.github.ph1lou.pluginlg.listener;


import java.util.List;
import java.util.Random;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;



public class WorldListener implements Listener {

	final MainLG main;
	
	public WorldListener(MainLG main) {
		this.main=main;
	}

	@EventHandler
	private void catchChestOpen(InventoryOpenEvent event)  {
		if(!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
		if(event.getInventory().getType().equals(InventoryType.CHEST)) {
			if(event.getInventory().getHolder() instanceof Chest) {
	            Location cloc = ((Chest) event.getInventory().getHolder()).getLocation();
	        	if(main.eventslg.chest_location.contains(cloc)) {
	        		 main.eventslg.chest_has_been_open.put(cloc,true);
	        	}
	        }
		}
    }
	
	@EventHandler
	private void onBlockBreak(BlockBreakEvent event) {	
	
		Player player =event.getPlayer();
		Block Block = event.getBlock();
        final Location loc = new Location(Block.getWorld(), Block.getLocation().getBlockX() + 0.5, Block.getLocation().getBlockY() + 0.5, Block.getLocation().getBlockZ() + 0.5);
		
		if(!main.playerlg.containsKey(player.getName())) return ;
			
		int xp_rate=1;
		
		if (main.config.scenario.get(ScenarioLG.XP_BOOST) ) {
				xp_rate=main.config.getXp_boost();
		}
		
		switch (Block.getType()){
		
		case COAL_ORE :
			
			if(!event.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_PICKAXE) && !event.getPlayer().getItemInHand().getType().equals(Material.IRON_PICKAXE) && !event.getPlayer().getItemInHand().getType().equals(Material.STONE_PICKAXE)  && !event.getPlayer().getItemInHand().getType().equals(Material.GOLD_PICKAXE)  && !event.getPlayer().getItemInHand().getType().equals(Material.WOOD_PICKAXE)){
   				return;
   			}
			Block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(event.getExpToDrop()*xp_rate);
			if (main.config.scenario.get(ScenarioLG.CUT_CLEAN) ) {
				 Block.setType(Material.AIR);
				 if(main.score.getTimer()<=main.config.value.get(TimerLG.DIGGING)) {
					 Block.getWorld().dropItem(loc, new ItemStack(Material.TORCH,4));
				 }
			}
            break;
		
		case REDSTONE_ORE:

			case LAPIS_ORE:

			case EMERALD_ORE:
				if(main.score.getTimer()> main.config.value.get(TimerLG.DIGGING))
					Block.setType(Material.AIR) ;
				Block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(event.getExpToDrop()*xp_rate);
			break;

			case DIAMOND_ORE:
			
			if(!event.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_PICKAXE) && !event.getPlayer().getItemInHand().getType().equals(Material.IRON_PICKAXE)){
				return;
			}
			
			if (main.config.scenario.get(ScenarioLG.DIAMOND_LIMIT) ) {
				if(main.playerlg.get(player.getName()).getDiamondLimit()>0) {
					if(main.score.getTimer()<=main.config.value.get(TimerLG.DIGGING)) main.playerlg.get(player.getName()).decDiamondLimit();
					else Block.setType(Material.AIR) ;
				}
				else {
					if(main.score.getTimer()<=main.config.value.get(TimerLG.DIGGING)) {
						Block.getWorld().dropItem(loc,new ItemStack(Material.GOLD_INGOT,1));
					}
					Block.setType(Material.AIR) ;
				}
			}
			
			Block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(event.getExpToDrop()*xp_rate);
			break;
			
		case IRON_ORE:
       
           	if(!event.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_PICKAXE) && !event.getPlayer().getItemInHand().getType().equals(Material.IRON_PICKAXE) && !event.getPlayer().getItemInHand().getType().equals(Material.STONE_PICKAXE)){
   				return;
   			}
           	if (main.config.scenario.get(ScenarioLG.CUT_CLEAN) ) {
           		Block.setType(Material.AIR);
           		if(main.score.getTimer()<=main.config.value.get(TimerLG.DIGGING)) {
                    Block.getWorld().dropItem(loc, new ItemStack(Material.IRON_INGOT,1));
           		}
				Block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(xp_rate);
           	}
            break;
           
		case GOLD_ORE:
			if(!event.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_PICKAXE) && !event.getPlayer().getItemInHand().getType().equals(Material.IRON_PICKAXE)){
				return;
			}
			if (main.config.scenario.get(ScenarioLG.CUT_CLEAN) ) {
				Block.setType(Material.AIR);
				if(main.score.getTimer()<=main.config.value.get(TimerLG.DIGGING)) {
					Block.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT,1 ));
				}
                Block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(xp_rate);
			}
            break;
            
		case GRAVEL :
			if (main.config.scenario.get(ScenarioLG.VANILLA_PLUS)  && new Random(System.currentTimeMillis()).nextFloat()<main.config.getFlint_rate()) {
				 Block.setType(Material.AIR);
		         Block.getWorld().dropItem(loc, new ItemStack(Material.FLINT,1));
			}
            break;
            
		default:
			break;	
		}
	}

	@EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
		
        if (main.config.scenario.get(ScenarioLG.CUT_CLEAN)) {
        	
        	List<ItemStack> loots = event.getDrops();
            
            for (int i = loots.size() - 1; i >= 0; --i) {
                ItemStack is = loots.get(i);
                if (is == null) {
                    return;
                }
                switch (is.getType()) {
                    case RAW_BEEF: 
                        loots.remove(i);
                        loots.add(new ItemStack(Material.COOKED_BEEF));
                        break;
                    
                    case PORK: 
                        loots.remove(i);
                        loots.add(new ItemStack(Material.GRILLED_PORK));
                        break;
                    
                    case RAW_CHICKEN: 
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
					case ENDER_PEARL:
						loots.remove(i);
						if(new Random(System.currentTimeMillis()).nextFloat()<main.config.getPearl_rate()){
							loots.add(new ItemStack(Material.ENDER_PEARL));
						}
						break;
					default:
                    	
                }
            }
        }
    }
	
}

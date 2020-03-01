package io.github.ph1lou.pluginlg.savelg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import org.bukkit.inventory.ItemStack;

public class StuffLG {
	
	public final Map<RoleLG,List<ItemStack>> role_stuff = new HashMap<>();
	private final List<ItemStack> death_loot = new ArrayList<>() ;
	private final List<ItemStack> start_loot = new ArrayList<>();
	
	
	public List<ItemStack> getDeathLoot() {
		return this.death_loot;
	}
	
	public List<ItemStack> getStartLoot() {
		return this.start_loot;
	}
	
	public void clearDeathLoot() {
		death_loot.clear();
	}
	
	public void clearStartLoot() {
		start_loot.clear();
	}
	
	public void addDeathLoot(ItemStack i) {
		death_loot.add(i);
	}
	
	public void addStartLoot(ItemStack i) {
		start_loot.add(i);
	}

    public void save(MainLG main, int number) {
        
        int pos = 0;
        
        for(RoleLG role:RoleLG.values()) {
        	for (ItemStack i : role_stuff.get(role)) {
                main.getConfig().set("save"+number +"."+role.toString()+ "." + pos , i);
                pos++;
            }
        	pos = 0;
        }
        for (ItemStack i : start_loot) {
            main.getConfig().set("save"+number +".start_loot." + pos , i);
            pos++;
        }
    	pos = 0;
    	for (ItemStack i : death_loot) {
            main.getConfig().set("save"+number +".death_loot." + pos , i);
            pos++;
        }
        main.saveConfig();
        
    }
     
   public void load(MainLG main, int number) {
	   
	   	start_loot.clear();
	   	death_loot.clear();
	   	role_stuff.clear();
	   	
    	for(RoleLG role:RoleLG.values()) {
    		
    		role_stuff.put(role, new ArrayList<>());
    		if(main.getConfig().getItemStack("save"+number +"."+role.toString()+".0")!=null) {
				Set<String> sl = main.getConfig().getConfigurationSection("save"+number +"."+role.toString()+".").getKeys(false);
				for (String s : sl) {
    				role_stuff.get(role).add(main.getConfig().getItemStack("save"+number +"."+role.toString()+ "." + s));
 		        }
			}
    	}
 
    	if(main.getConfig().getItemStack("save"+number +".start_loot.0")!=null) {
    		Set<String> sl = main.getConfig().getConfigurationSection("save"+number +".start_loot.").getKeys(false);
    
    		for (String s : sl) {
    			start_loot.add(main.getConfig().getItemStack("save"+number +".start_loot." + s));
    	    }
    	}
    	
  
    	
    	if(main.getConfig().getItemStack("save"+number +".death_loot.0")!=null) {
    		Set<String> sl = main.getConfig().getConfigurationSection("save"+number +".death_loot").getKeys(false);
    		for (String s : sl) {
    			death_loot.add(main.getConfig().getItemStack("save"+number +".death_loot." + s));
    	    }
    	}
    	
    }
    

	
	
	
	

}

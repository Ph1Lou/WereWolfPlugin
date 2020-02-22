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
	
	public Map<RoleLG,List<ItemStack>> rolestuff = new HashMap<>();
	private List<ItemStack> deathloot =new ArrayList<ItemStack>() ;
	private List<ItemStack>  startloot = new ArrayList<ItemStack>();
	
	
	public List<ItemStack> getdeathloot() {
		return this.deathloot;
	}
	
	public List<ItemStack> getstartloot() {
		return this.startloot;
	}
	
	public void cleardeathloot() {
		deathloot.clear();
	}
	
	public void clearstartloot() {
		startloot.clear();
	}
	
	public void adddeathloot(ItemStack i) {
		deathloot.add(i);
	}
	
	public void addstartloot(ItemStack i) {
		startloot.add(i);
	}
	
	
	
    public void save(MainLG main, int numero) {
        
        int pos = 0;
        
        for(RoleLG role:RoleLG.values()) {
        	for (ItemStack i : rolestuff.get(role)) {
                main.getConfig().set("save"+numero +"."+role.toString()+ "." + pos , i);
                pos++;
            }
        	pos = 0;
        }
        for (ItemStack i : startloot) {
            main.getConfig().set("save"+numero +".startloot." + pos , i);
            pos++;
        }
    	pos = 0;
    	for (ItemStack i : deathloot) {
            main.getConfig().set("save"+numero +".deathloot." + pos , i);
            pos++;
        }
        main.saveConfig();
        
    }
     
   public void load(MainLG main, int numero) {
	   
	   	startloot.clear();
	   	deathloot.clear();
	   	rolestuff.clear();
	   	
    	for(RoleLG role:RoleLG.values()) {
    		
    		rolestuff.put(role,new ArrayList<ItemStack>());
    		if(main.getConfig().getItemStack("save"+numero +"."+role.toString()+".0")!=null) {
				Set<String> sl = main.getConfig().getConfigurationSection("save"+numero +"."+role.toString()+".").getKeys(false);
				for (String s : sl) {
    				rolestuff.get(role).add(main.getConfig().getItemStack("save"+numero +"."+role.toString()+ "." + s));
 		        }
			}
    	}
 
    	if(main.getConfig().getItemStack("save"+numero +".startloot.0")!=null) {
    		Set<String> sl = main.getConfig().getConfigurationSection("save"+numero +".startloot.").getKeys(false);
    
    		for (String s : sl) {
    			startloot.add(main.getConfig().getItemStack("save"+numero +".startloot." + s));
    	    }
    	}
    	
  
    	
    	if(main.getConfig().getItemStack("save"+numero +".deathloot.0")!=null) {
    		Set<String> sl = main.getConfig().getConfigurationSection("save"+numero +".deathloot").getKeys(false);
    		for (String s : sl) {
    			deathloot.add(main.getConfig().getItemStack("save"+numero +".deathloot." + s));
    	    }
    	}
    	
    }
    

	
	
	
	

}

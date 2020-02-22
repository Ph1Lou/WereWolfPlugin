package io.github.ph1lou.pluginlg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.ph1lou.pluginlg.enumlg.Camp;
import io.github.ph1lou.pluginlg.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;





public class EventsLG {
	
	MainLG main;
	public Map<Location,Boolean> chesthasbeenopen = new HashMap<>();
	public List<Location> chestlocation = new ArrayList<>();
	public EventsLG(MainLG main) {
		this.main=main;
	}
	
	private void create_target(Location location,Boolean active) {
		
		World world = location.getWorld();
		Location location2 = location.clone();
		location2.setY(location2.getY()+1);
		List<String> threat = new ArrayList<>();
		Block block1 = world.getBlockAt(location);
		Block block2 = world.getBlockAt(location2);
		
		block1.setType(Material.CHEST);
		block2.setType(Material.SIGN_POST);
		
		Chest chest = (Chest) block1.getState();
		Sign sign = (Sign) block2.getState();
		
		for(String p:main.playerlg.keySet()) {
			if(!main.playerlg.get(p).isCamp(Camp.VILLAGE) && main.playerlg.get(p).isState(State.VIVANT)) {
				threat.add(p);
			}
		
		}
		
		if (active && !threat.isEmpty()){
			chest.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE,2));
			String playername = threat.get((int) Math.floor(Math.random()*threat.size()));
			sign.setLine(1,playername);
		}
		else {
			chest.getInventory().addItem(new ItemStack(Material.BONE,8));
			sign.setLine(1,"Bon toutou");
		}
		sign.update();
		location.getBlock().setType(chest.getType());
		location2.getBlock().setType(sign.getType());
	}
	
	public void event1() {
		
		World world = Bukkit.getWorld("world");
		WorldBorder wb =world.getWorldBorder();
		int nb_target= (int) Math.floor(main.score.getPlayerSize()/3);
		if(nb_target<2) {
			nb_target=2;
		}
		for (int i =0;i<nb_target;i++) {
			
			double a = Math.random()*2*Math.PI;
			int x = (int) (Math.round(wb.getSize()/3*Math.cos(a)+world.getSpawnLocation().getX()));
			int z = (int) (Math.round(wb.getSize()/3*Math.sin(a)+world.getSpawnLocation().getBlockZ()));
			Location location=new Location(world,x,world.getHighestBlockYAt(x,z),z);
			
			if (i==0) {
				create_target(location,true);
			}
			else create_target(location,false);
			
			chestlocation.add(location);
			chesthasbeenopen.put(location,false);
		}
		Bukkit.broadcastMessage(main.texte.esthetique("§m", "§e",main.texte.getText(36)+nb_target+main.texte.getText(37)));
		
	}
	
}

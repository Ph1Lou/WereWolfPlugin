package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


public class TransportationLG extends BukkitRunnable{
	
	private final MainLG main;
	private int i=-1;
	
	public TransportationLG(MainLG main) {
		this.main=main;
	}

	public void loadChunk(){

		World world = Bukkit.getWorld("world");
		WorldBorder wb = world.getWorldBorder();
		for(int i=0;i<Bukkit.getOnlinePlayers().size();i++){
			double a = i*2*Math.PI/Bukkit.getOnlinePlayers().size();
			int x = (int) (Math.round(wb.getSize()/3*Math.cos(a)+world.getSpawnLocation().getX()));
			int z = (int) (Math.round(wb.getSize()/3*Math.sin(a)+world.getSpawnLocation().getZ()));
			world.getChunkAt( new Location(world,x,world.getHighestBlockYAt(x,z)+1,z)).load(true);
		}
	}

	@Override
	public void run() {

		main.score.updateBoard();

		if(main.isState(StateLG.TRANSPORTATION)) {

			if(i==-1){
				loadChunk();
			}
			if(i<main.playerlg.size()) {
				
				String playername = (String) main.playerlg.keySet().toArray()[i];
				
				for(Player p:Bukkit.getOnlinePlayers()) {
				
					p.playSound(p.getLocation(), Sound.ORB_PICKUP,1,20);
				}

				main.optionlg.updateNameTag();

				if(Bukkit.getPlayer(playername)!=null) {

					Player player = Bukkit.getPlayer(playername);
					World world = player.getWorld();
					WorldBorder wb = world.getWorldBorder();
					main.playerlg.get(playername).clearPlayer(player);
			        player.sendMessage(main.text.getText(121));

					for(ItemStack it:main.stufflg.getStartLoot()) {
						player.getInventory().addItem(it);	
					}
					if(main.config.scenario.get(ScenarioLG.CAT_EYES)){
						player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0,false,false));
					}
					double a = i*2*Math.PI/Bukkit.getOnlinePlayers().size();
					int x = (int) (Math.round(wb.getSize()/3*Math.cos(a)+world.getSpawnLocation().getX()));
					int z = (int) (Math.round(wb.getSize()/3*Math.sin(a)+world.getSpawnLocation().getZ()));
					Location spawn = new Location(world,x,world.getHighestBlockYAt(x,z)+1,z);
					if(!main.config.tool_switch.get(ToolLG.COMPASS_MIDDLE)){
						player.setCompassTarget(spawn);
					}
					else player.setCompassTarget(world.getSpawnLocation());
					main.playerlg.get(playername).setSpawn(spawn.clone());
					spawn.setY(spawn.getY()+100);
					player.teleport(spawn);
				}
			}
			else if(i==10+main.playerlg.size()){
				World world = Bukkit.getWorld("world");
				world.setTime(0);
				main.setState(StateLG.DEBUT);
				AutoStartLG start = new AutoStartLG(main);
				start.runTaskTimer(main, 0, 5);
				cancel();
			}
			i++;
		}
		
		
	}
}

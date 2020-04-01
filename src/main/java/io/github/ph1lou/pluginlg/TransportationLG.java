package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


public class TransportationLG extends BukkitRunnable{
	
	private final MainLG main;
	private int i=0;
	
	public TransportationLG(MainLG main) {
		this.main=main;
	}

	private void createStructure(Material m, int x,int y,int z){
		try{
			World world = Bukkit.getWorld("world");
			for(int i=-2;i<3;i++){
				for(int j=-2;j<3;j++){
					if(Math.abs(j)==2 ||Math.abs(i)==2){
						for(int k=0;k<2;k++){
							new Location(world, x+i, y+99+k,z+j).getBlock().setType(m);
						}
					}
					new Location(world, x+i, y+98,z+j).getBlock().setType(m);
					new Location(world, x+i, y+102,z+j).getBlock().setType(m);
					world.getChunkAt(x+i*16,z+j*16).load(true);
				}
			}
		}catch(Exception e){
			Bukkit.broadcastMessage(main.text.getText(21));
		}
	}

	@Override
	public void run() {

		try{
			main.score.updateBoard();
			World world = Bukkit.getWorld("world");
			WorldBorder wb = world.getWorldBorder();

			if(main.isState(StateLG.TRANSPORTATION)) {

				if (i < main.playerLG.size()) {

					String playername = (String) main.playerLG.keySet().toArray()[i];

					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 20);
					}

					double a = i * 2 * Math.PI / Bukkit.getOnlinePlayers().size();
					int x = (int) (Math.round(wb.getSize() / 3 * Math.cos(a) + world.getSpawnLocation().getX()));
					int z = (int) (Math.round(wb.getSize() / 3 * Math.sin(a) + world.getSpawnLocation().getZ()));
					Location spawn = new Location(world, x, world.getHighestBlockYAt(x, z) + 1, z);
					createStructure(Material.BARRIER, x, (int) spawn.getY(), z);

					main.playerLG.get(playername).setSpawn(spawn.clone());
					main.optionlg.updateCompass();
					main.optionlg.updateNameTag();

					if (Bukkit.getPlayer(playername) != null) {

						Player player = Bukkit.getPlayer(playername);
						player.setGameMode(GameMode.SURVIVAL);
						main.playerLG.get(playername).clearPlayer(player);
						for (ItemStack it : main.stufflg.getStartLoot()) {
							player.getInventory().addItem(it);
						}
						if (main.config.scenario.get(ScenarioLG.CAT_EYES)) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
						}
						spawn.setY(spawn.getY() + 100);
						player.teleport(spawn);
					}

				} else if (i == main.playerLG.size() + 10) {
					for (PlayerLG plg : main.playerLG.values()) {
						Location loc = plg.getSpawn();
						createStructure(Material.AIR, (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
					}
					for (Player p : Bukkit.getOnlinePlayers()) {
						Title.sendTitle(p, 20, 20, 20, main.text.getText(89), main.text.getText(90));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 20);
						p.sendMessage(main.text.getText(121));
					}
					world.setTime(0);
					main.setState(StateLG.DEBUT);
					AutoStartLG start = new AutoStartLG(main);
					start.runTaskTimer(main, 0, 5);
					cancel();
				} else {
					for (Player p : Bukkit.getOnlinePlayers()) {
						Title.sendTitle(p, 25, 20, 25, "Start", "§b" + (10 - (i - main.playerLG.size())));
						p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1, 20);
					}
				}
				i++;
			}
		}catch(Exception e){
			Bukkit.broadcastMessage(main.text.getText(21));
		}
	}
}

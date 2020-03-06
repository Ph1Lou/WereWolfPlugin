package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;


public class TeleportationLG extends BukkitRunnable{
	
	private final MainLG main;
	private int i=0;
	
	public TeleportationLG(MainLG main) {
		this.main=main;
	}


	@Override
	public void run() {
		
		main.score.updateBoard();

		if(main.isState(StateLG.TELEPORTATION)) {
			
			if(i<main.playerlg.size()) {
				
				String playername = (String) main.playerlg.keySet().toArray()[i];
				
				for(Player p:Bukkit.getOnlinePlayers()) {
				
					p.playSound(p.getLocation(), Sound.ORB_PICKUP,1,20);
				}

				main.optionlg.updateScenario();

				if(Bukkit.getPlayer(playername)!=null) {

					Player player = Bukkit.getPlayer(playername);
					player.setMaxHealth(20);
					player.setHealth(20);
					player.setExp(0);
					player.setLevel(0);
					player.getInventory().clear();
			        player.getInventory().setHelmet(null);
			        player.getInventory().setChestplate(null);
			        player.getInventory().setLeggings(null);
			        player.getInventory().setBoots(null);
			        
					for(PotionEffect po:player.getActivePotionEffects()) {
						player.removePotionEffect(po.getType());
					}
					for(ItemStack it:main.stufflg.getStartLoot()) {
						player.getInventory().addItem(it);	
					}
					if(main.config.scenario.get(ScenarioLG.CAT_EYES)){
						player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0,false,false));
					}
					main.eparpillement(playername,i,main.text.getText(121));
				}
				
					
			}
			else if(i==10+main.playerlg.size()){
				World world = Bukkit.getWorld("world");
				world.setTime(0);
				main.setState(StateLG.DEBUT);
				AutoStartLG start = new AutoStartLG(main);
				start.runTaskTimer(main, 0, 20);
				if(main.config.scenario.get(ScenarioLG.NO_NAME_TAG)){
					for(String players : main.playerlg.keySet()) {
						main.board.getTeam(players).setNameTagVisibility(NameTagVisibility.NEVER);
					}
				}
				cancel();
			}
			i++;
		}
		
		
	}
}

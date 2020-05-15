package io.github.ph1lou.pluginlg.game;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EventsLG {
	
	final GameManager game;
	public final Map<Location,Boolean> chest_has_been_open = new HashMap<>();
	public final List<Location> chest_location = new ArrayList<>();
	public EventsLG(GameManager game) {
		this.game=game;
	}
	
	private void createTarget(Location location, Boolean active) {

		Location location2 = location.clone();
		location2.setY(location2.getY()+1);
		List<PlayerLG> danger = new ArrayList<>();
		Block block1 = location.getBlock();
		Block block2 = location2.getBlock();
		
		block1.setType(Material.CHEST);
		block2.setType(Material.SIGN_POST);
		
		Chest chest = (Chest) block1.getState();
		Sign sign = (Sign) block2.getState();
		
		for(PlayerLG plg:game.playerLG.values()) {
			if(!plg.getRole().isCamp(Camp.VILLAGER) && plg.isState(State.ALIVE)) {
				danger.add(plg);
			}
		}
		
		if (active && !danger.isEmpty()){
			chest.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE,2));
			PlayerLG plg = danger.get((int) Math.floor(new Random(System.currentTimeMillis()).nextFloat()*danger.size()));
			sign.setLine(1,plg.getName());
		}
		else {
			chest.getInventory().addItem(new ItemStack(Material.BONE,8));
			sign.setLine(1,game.translate("werewolf.event.on_sign"));
		}
		sign.update();
		location.getBlock().setType(chest.getType());
		location2.getBlock().setType(sign.getType());
	}
	
	public void event1() {
		
		World world = game.getWorld();
		WorldBorder wb =world.getWorldBorder();
		int nb_target = game.score.getPlayerSize() / 3;
		if(nb_target<2) {
			nb_target=2;
		}
		for (int i =0;i<nb_target;i++) {

			double a = Math.random() * 2 * Math.PI;
			int x = (int) (Math.round(wb.getSize() / 3 * Math.cos(a) + world.getSpawnLocation().getX()));
			int z = (int) (Math.round(wb.getSize() / 3 * Math.sin(a) + world.getSpawnLocation().getBlockZ()));
			Location location = new Location(world, x, world.getHighestBlockYAt(x, z), z);

			createTarget(location, i == 0);

			chest_location.add(location);
			chest_has_been_open.put(location, false);
		}
		Bukkit.broadcastMessage(game.translate("werewolf.event.seer_death", nb_target));
	}
}

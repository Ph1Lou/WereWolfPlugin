package io.github.ph1lou.werewolfplugin.game;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.ToolLG;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.ChestEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Events implements Listener {
	
	final GameManager game;
	public final Map<Location,Boolean> chest_has_been_open = new HashMap<>();
	public final List<Location> chest_location = new ArrayList<>();
	public Events(GameManager game) {
		this.game=game;
	}
	
	private void createTarget(Location location, Boolean active) {

		Location location2 = location.clone();
		location2.setY(location2.getY()+1);
		List<PlayerWW> danger = new ArrayList<>();
		Block block1 = location.getBlock();
		Block block2 = location2.getBlock();
		
		block1.setType(Material.CHEST);
		block2.setType(Material.ACACIA_SIGN);
		
		Chest chest = (Chest) block1.getState();
		Sign sign = (Sign) block2.getState();
		
		for(PlayerWW plg:game.getPlayersWW().values()) {
			if(!plg.getRole().isCamp(Camp.VILLAGER) && plg.isState(State.ALIVE)) {
				danger.add(plg);
			}
		}
		
		if (active && !danger.isEmpty()){
			chest.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE,2));
			PlayerWW plg = danger.get((int) Math.floor(new Random(System.currentTimeMillis()).nextFloat()*danger.size()));
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

	@EventHandler
	public void onActionBarRequest(ActionBarEvent event){

		StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

		if (game.getEvents().chest_has_been_open.isEmpty()) return;

		if (Bukkit.getPlayer(event.getPlayerUUID()) == null) return;

		Player player = Bukkit.getPlayer(event.getPlayerUUID());

		for (int i = 0; i < game.getEvents().chest_location.size(); i++) {
			if (!game.getEvents().chest_has_been_open.get(game.getEvents().chest_location.get(i))) {
				stringBuilder.append("§a");
			} else stringBuilder.append("§6");
			stringBuilder.append(" ").append(game.getScore().updateArrow(player, game.getEvents().chest_location.get(i)));
		}
		event.setActionBar(stringBuilder.toString());
	}

	@EventHandler
	public void event1(ChestEvent event) {

		World world = game.getWorld();
		WorldBorder wb = world.getWorldBorder();
		int nb_target = game.getScore().getPlayerSize() / 3;
		if (nb_target < 2) {
			nb_target = 2;
		}
		for (int i = 0; i < nb_target; i++) {

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

	@EventHandler
	private void catchChestOpen(InventoryOpenEvent event) {

		if (!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
		if (event.getInventory().getType().equals(InventoryType.CHEST)) {
			if (event.getInventory().getHolder() instanceof Chest) {
				Location location = ((Chest) event.getInventory().getHolder()).getLocation();
				if (game.getEvents().chest_location.contains(location)) {
					game.getEvents().chest_has_been_open.put(location, true);
					if (!game.getEvents().chest_has_been_open.containsValue(false)) {
						game.getEvents().chest_location.clear();
						game.getEvents().chest_has_been_open.clear();
						Bukkit.broadcastMessage(game.translate("werewolf.event.all_chest_find"));
						game.getConfig().getConfigValues().put(ToolLG.EVENT_SEER_DEATH, true);
					}
				}
			}
		}
	}
}

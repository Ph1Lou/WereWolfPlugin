package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.RoleRegister;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class RoleManagementLG {
	
	private final GameManager game;
	private final MainLG main;

	public RoleManagementLG(MainLG main, GameManager game) {
		this.game=game;
		this.main=main;
	}
	
	public void repartitionRolesLG() {

		List<UUID> playersUUID = new ArrayList<>(game.playerLG.keySet());
		List<RoleRegister> config = new ArrayList<>();
		game.getConfig().getConfigValues().put(ToolLG.CHAT, false);
		game.getConfig().getRoleCount().put("werewolf.role.villager.display", game.getConfig().getRoleCount().get("werewolf.role.villager.display") + playersUUID.size() - game.score.getRole());

		for (RoleRegister roleRegister:game.getRolesRegister()) {
			for (int i = 0; i < game.getConfig().getRoleCount().get(roleRegister.getKey()); i++) {
				config.add(roleRegister);
			}
		}

		while (!playersUUID.isEmpty()) {
			
			int n =(int) Math.floor(game.getRandom().nextFloat()*playersUUID.size());
			UUID playerUUID = playersUUID.get(n);
			PlayerWW plg = game.playerLG.get(playerUUID);

			try {
				Roles role = config.get(0).getConstructors().newInstance(main,game,playerUUID);
				Bukkit.getPluginManager().registerEvents((Listener) role, main);
				plg.setRole(role);

			} catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
				e.printStackTrace();
			}
			config.remove(0);	
			playersUUID.remove(n);
		}
		for(UUID uuid:game.playerLG.keySet()){
			recoverRolePower(uuid);
		}

		game.checkVictory();
	}
	
	public void recoverRolePower(UUID uuid) {
		
		if (Bukkit.getPlayer(uuid)==null) return;
		
		Player player = Bukkit.getPlayer(uuid);
		PlayerWW plg = game.playerLG.get(uuid);
		plg.setKit(true);
		
		player.performCommand("ww role");
		player.sendMessage(game.translate("werewolf.announcement.review_role"));

		plg.getRole().recoverPotionEffect(player);
		plg.getRole().recoverPower(player);

		if(!game.getStuffs().getStuffRoles().containsKey(plg.getRole().getDisplay())){
			Bukkit.getConsoleSender().sendMessage("[plugin lg] invalid plugin structure");
			return;
		}

		for(ItemStack i:game.getStuffs().getStuffRoles().get(plg.getRole().getDisplay())) {
			
			if(player.getInventory().firstEmpty()==-1) {
				player.getWorld().dropItem(player.getLocation(),i);
			}
			else {
				player.getInventory().addItem(i);
				player.updateInventory();
			}
		}
	}

	

	


	public void brotherLife() {

		int counter = 0;
		double health = 0;
		for (UUID uuid:game.playerLG.keySet()) {

			PlayerWW plg= game.playerLG.get(uuid);

			if (plg.isState(State.ALIVE) && plg.getRole().isDisplay("werewolf.role.siamese_twin.display") && Bukkit.getPlayer(uuid) != null) {
				Player c = Bukkit.getPlayer(uuid);
				counter++;
				health += c.getHealth() / c.getMaxHealth();
			}
		}
		health /= counter;
		for (UUID uuid:game.playerLG.keySet()) {

			PlayerWW plg= game.playerLG.get(uuid);

			if (plg.isState(State.ALIVE) && plg.getRole().isDisplay("werewolf.role.siamese_twin.display") && Bukkit.getPlayer(uuid) != null) {
				Player c = Bukkit.getPlayer(uuid);
				if(health * c.getMaxHealth()>10){
					if(health * c.getMaxHealth()+1<c.getHealth()){
						c.playSound(c.getLocation(), Sound.BURP,1,20);
					}
					c.setHealth(health * c.getMaxHealth());
				}
			}
		}
	}

	public UUID autoSelect(UUID playerUUID) {
		
		List<UUID> players = new ArrayList<>();
		for(UUID uuid:game.playerLG.keySet()) {
			if(game.playerLG.get(uuid).isState(State.ALIVE) && !uuid.equals(playerUUID)) {
				players.add(uuid);
			}	
		}
		if(players.isEmpty()) {
			return playerUUID;
		}
		return 	players.get((int) Math.floor(game.getRandom().nextFloat()*players.size()));
	}
	
	
	

	public boolean isWereWolf(UUID uuid){
		if(game.playerLG.containsKey(uuid)){
			PlayerWW plg = game.playerLG.get(uuid);
			return(plg.getRole()!=null && plg.getRole().isWereWolf());
		}
		return false;
	}

	

}

package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.ToolLG;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class RoleManagement {
	
	private final GameManager game;
	private final Main main;

	public RoleManagement(Main main, GameManager game) {
		this.game=game;
		this.main=main;
	}
	
	public void repartitionRolesLG() {

		List<UUID> playersUUID = new ArrayList<>(game.getPlayersWW().keySet());
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
			PlayerWW plg = game.getPlayersWW().get(playerUUID);

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
		for(PlayerWW playerWW :game.getPlayersWW().values()){
			playerWW.getRole().recoverPower();
		}

		game.checkVictory();
	}
	

	


	public void brotherLife() {

		int counter = 0;
		double health = 0;
		for (UUID uuid:game.getPlayersWW().keySet()) {

			PlayerWW plg= game.getPlayersWW().get(uuid);

			if (plg.isState(State.ALIVE) && plg.getRole().isDisplay("werewolf.role.siamese_twin.display") && Bukkit.getPlayer(uuid) != null) {
				Player c = Bukkit.getPlayer(uuid);
				counter++;
				health += c.getHealth() / c.getMaxHealth();
			}
		}
		health /= counter;
		for (UUID uuid:game.getPlayersWW().keySet()) {

			PlayerWW plg= game.getPlayersWW().get(uuid);

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
		for(UUID uuid:game.getPlayersWW().keySet()) {
			if(game.getPlayersWW().get(uuid).isState(State.ALIVE) && !uuid.equals(playerUUID)) {
				players.add(uuid);
			}	
		}
		if(players.isEmpty()) {
			return playerUUID;
		}
		return 	players.get((int) Math.floor(game.getRandom().nextFloat()*players.size()));
	}
}

package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.ToolLG;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class RoleManagement {

	private final Main main;

	public RoleManagement(Main main) {
		this.main = main;
	}

	public void repartitionRolesLG() {

		GameManager game = main.getCurrentGame();
		List<UUID> playersUUID = new ArrayList<>(game.getPlayersWW().keySet());
		List<RoleRegister> config = new ArrayList<>();
		game.getConfig().getConfigValues().put(ToolLG.CHAT, false);
		game.getConfig().getRoleCount().put("werewolf.role.villager.display", game.getConfig().getRoleCount().get("werewolf.role.villager.display") + playersUUID.size() - game.getScore().getRole());

		for (RoleRegister roleRegister : game.getRolesRegister()) {
			for (int i = 0; i < game.getConfig().getRoleCount().get(roleRegister.getKey()); i++) {
				config.add(roleRegister);
			}
		}

		while (!playersUUID.isEmpty()) {

			int n = (int) Math.floor(game.getRandom().nextFloat() * playersUUID.size());
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
			try {
				playerWW.getRole().recoverPower();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		game.checkVictory();
	}
	

	


	public void brotherLife() {

		GameManager game = main.getCurrentGame();
		int counter = 0;
		double health = 0;
		for (UUID uuid : game.getPlayersWW().keySet()) {

			PlayerWW plg = game.getPlayersWW().get(uuid);
			Player c = Bukkit.getPlayer(uuid);

			if (plg.isState(State.ALIVE) && plg.getRole().isDisplay("werewolf.role.siamese_twin.display") && c != null) {
				counter++;
				health += c.getHealth() / VersionUtils.getVersionUtils().getPlayerMaxHealth(c);
			}
		}
		health /= counter;
		for (UUID uuid:game.getPlayersWW().keySet()) {

			PlayerWW plg = game.getPlayersWW().get(uuid);
			Player c = Bukkit.getPlayer(uuid);

			if (plg.isState(State.ALIVE) && plg.getRole().isDisplay("werewolf.role.siamese_twin.display") && c != null) {

				if (health * VersionUtils.getVersionUtils().getPlayerMaxHealth(c) > 10) {
					if (health * VersionUtils.getVersionUtils().getPlayerMaxHealth(c) + 1 < c.getHealth()) {
						Sounds.BURP.play(c);
					}
					c.setHealth(health * VersionUtils.getVersionUtils().getPlayerMaxHealth(c));
				}
			}
		}
	}

	public UUID autoSelect(UUID playerUUID) {

		GameManager game = main.getCurrentGame();
		List<UUID> players = new ArrayList<>();
		for (UUID uuid : game.getPlayersWW().keySet()) {
			if (game.getPlayersWW().get(uuid).isState(State.ALIVE) && !uuid.equals(playerUUID)) {
				players.add(uuid);
			}
		}
		if (players.isEmpty()) {
			return playerUUID;
		}
		return players.get((int) Math.floor(game.getRandom().nextFloat() * players.size()));
	}
}

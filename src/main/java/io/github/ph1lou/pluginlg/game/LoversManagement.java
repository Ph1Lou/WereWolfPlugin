package io.github.ph1lou.pluginlg.game;


import io.github.ph1lou.pluginlg.classesroles.villageroles.Cupid;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class LoversManagement {

	public final List<List<UUID>> loversRange = new ArrayList<>();
	public final List<List<UUID>> cursedLoversRange = new ArrayList<>();
	public final List<List<UUID>> amnesiacLoversRange = new ArrayList<>();
	private final GameManager game;

	public LoversManagement(GameManager game) {
		this.game = game;
	}


	public void autoCursedLovers() {

		List<UUID> cursedLovers = new ArrayList<>();

		for (UUID uuid : game.playerLG.keySet()) {
			PlayerLG plg = game.playerLG.get(uuid);
			if (plg.isState(State.ALIVE) && plg.getLovers().isEmpty() && plg.getAmnesiacLoverUUID()==null) {
				cursedLovers.add(uuid);
			}
		}

		if (cursedLovers.size() < 2 && game.config.getRoleCount().get(RoleLG.CURSED_LOVER) > 0) {
			Bukkit.broadcastMessage(game.translate("werewolf.role.cursed_lover.not_enough_players"));
			return;
		}

		int i = 0;

		while (cursedLovers.size() >= 2 && i < game.config.getRoleCount().get(RoleLG.CURSED_LOVER)) {

			UUID j1 = cursedLovers.get((int) Math.floor(game.getRandom().nextFloat() * cursedLovers.size()));
			cursedLovers.remove(j1);
			UUID j2 = cursedLovers.get((int) Math.floor(game.getRandom().nextFloat() * cursedLovers.size()));
			cursedLovers.remove(j2);
			game.playerLG.get(j1).setCursedLover(j2);
			game.playerLG.get(j2).setCursedLover(j1);
			i++;
			cursedLoversRange.add(new ArrayList<>(Arrays.asList(j1, j2)));

			if (Bukkit.getPlayer(j1) != null) {
				announceCursedLovers(Bukkit.getPlayer(j1));
			} else game.playerLG.get(j1).setAnnounceCursedLoversAFK(true);
			if (Bukkit.getPlayer(j2) != null) {
				announceCursedLovers(Bukkit.getPlayer(j2));
			} else game.playerLG.get(j2).setAnnounceCursedLoversAFK(true);
		}
		game.config.getRoleCount().put(RoleLG.CURSED_LOVER, cursedLoversRange.size());
	}

	public void autoAmnesiacLovers() {

		List<UUID> amnesiacLovers = new ArrayList<>();

		for (UUID uuid : game.playerLG.keySet()) {
			PlayerLG plg = game.playerLG.get(uuid);
			if (plg.isState(State.ALIVE) && plg.getLovers().isEmpty()) {
				amnesiacLovers.add(uuid);
			}
		}

		if (amnesiacLovers.size() < 2 && game.config.getRoleCount().get(RoleLG.AMNESIAC_LOVER) > 0) {
			Bukkit.broadcastMessage(game.translate("werewolf.role.amnesiac_lover.not_enough_players"));
			return;
		}

		int i = 0;

		while (amnesiacLovers.size() >= 2 && i < game.config.getRoleCount().get(RoleLG.AMNESIAC_LOVER)) {

			UUID j1 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
			amnesiacLovers.remove(j1);
			UUID j2 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
			amnesiacLovers.remove(j2);
			game.playerLG.get(j1).setAmnesiacLoverUUID(j2);
			game.playerLG.get(j2).setAmnesiacLoverUUID(j1);
			i++;
		}
		game.config.getRoleCount().put(RoleLG.AMNESIAC_LOVER, 0);
	}

	public void detectionAmnesiacLover(){

		List<PlayerLG> amnesiacLovers = new ArrayList<>();

		for (PlayerLG plg : game.playerLG.values()) {
			if (plg.getAmnesiacLoverUUID()!=null && plg.isState(State.ALIVE) && game.playerLG.get(plg.getAmnesiacLoverUUID()).isState(State.ALIVE)) {
				amnesiacLovers.add(plg);
			}
		}

		while (!amnesiacLovers.isEmpty()){
			try{
				PlayerLG plg = amnesiacLovers.get(0);
				UUID loverUUID =plg.getAmnesiacLoverUUID();
				Player player = Bukkit.getPlayer(plg.getName());
				Player player2 = Bukkit.getPlayer(loverUUID);
				if(!amnesiacLovers.get(0).getRevealAmnesiacLover()){
					if(player.getLocation().distance(player2.getLocation())<game.config.getDistanceAmnesiacLovers()){
						amnesiacLoversRange.add(new ArrayList<>(Arrays.asList(player.getUniqueId(), loverUUID)));
						announceAmnesiacLovers(player);
						announceAmnesiacLovers(player2);
						game.config.getRoleCount().put(RoleLG.AMNESIAC_LOVER, game.config.getRoleCount().get(RoleLG.AMNESIAC_LOVER)+1);
						game.endlg.check_victory();
					}
				}
			}
			catch(Exception ignored){

			}
			finally {
				amnesiacLovers.remove(0);
			}
		}
	}


	public void announceAmnesiacLovers(Player player) {

		UUID playerUUID = player.getUniqueId();
		PlayerLG plg = game.playerLG.get(playerUUID);
		plg.setRevealAmnesiacLover(true);
		for (ItemStack k : game.stufflg.role_stuff.get(RoleLG.AMNESIAC_LOVER)) {

			if (player.getInventory().firstEmpty() == -1) {
				player.getWorld().dropItem(player.getLocation(), k);
			} else {
				player.getInventory().addItem(k);
				player.updateInventory();
			}
		}
		player.sendMessage(game.translate("werewolf.role.lover.description",game.playerLG.get(plg.getAmnesiacLoverUUID()).getName()));
		player.playSound(player.getLocation(), Sound.PORTAL_TRAVEL, 1, 20);
	}

	public void announceCursedLovers(Player player) {

		UUID playerUUID = player.getUniqueId();
		PlayerLG plg = game.playerLG.get(playerUUID);

		for (ItemStack k : game.stufflg.role_stuff.get(RoleLG.CURSED_LOVER)) {

			if (player.getInventory().firstEmpty() == -1) {
				player.getWorld().dropItem(player.getLocation(), k);
			} else {
				player.getInventory().addItem(k);
				player.updateInventory();
			}
		}
		player.setMaxHealth(player.getMaxHealth() + 2);
		player.sendMessage(game.translate("werewolf.role.cursed_lover.description", game.playerLG.get(plg.getCursedLovers()).getName()));
		player.playSound(player.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
	}

	public void autoLovers() {

		List<UUID> lovers = new ArrayList<>();
		for (UUID uuid : game.playerLG.keySet()) {
			if (game.playerLG.get(uuid).isState(State.ALIVE)) {
				lovers.add(uuid);
			}
		}
		if (lovers.size() < 2 && game.config.getRoleCount().get(RoleLG.CUPID) + game.config.getRoleCount().get(RoleLG.LOVER) > 0) {
			Bukkit.broadcastMessage(game.translate("werewolf.role.lover.not_enough_players"));
			return;
		}

		Boolean polygamy = game.config.getConfigValues().get(ToolLG.POLYGAMY);

		if (!polygamy && (game.config.getRoleCount().get(RoleLG.LOVER) == 0 && game.config.getRoleCount().get(RoleLG.CUPID) * 2 >= game.score.getPlayerSize()) || (game.config.getRoleCount().get(RoleLG.LOVER) != 0 && (game.config.getRoleCount().get(RoleLG.CUPID) + game.config.getRoleCount().get(RoleLG.LOVER)) * 2 > game.score.getPlayerSize())) {
			polygamy = true;
			Bukkit.broadcastMessage(game.translate("werewolf.role.lover.polygamy"));
		}
		UUID j1;
		UUID j2;

		for (UUID uuid : game.playerLG.keySet()) {

			PlayerLG plg = game.playerLG.get(uuid);
			if (plg.getRole() instanceof Cupid) {

				Cupid cupid = (Cupid) plg.getRole();

				if (cupid.hasPower() || !game.playerLG.get(cupid.getAffectedPlayers().get(0)).isState(State.ALIVE) || !game.playerLG.get(cupid.getAffectedPlayers().get(1)).isState(State.ALIVE)) {

					if (lovers.contains(uuid)) {
						lovers.remove(uuid);
						j1 = lovers.get((int) Math.floor(game.getRandom().nextFloat() * lovers.size()));
						lovers.remove(j1);
						j2 = lovers.get((int) Math.floor(game.getRandom().nextFloat() * lovers.size()));
						lovers.add(j1);
						lovers.add(uuid);
					} else {
						j1 = lovers.get((int) Math.floor(game.getRandom().nextFloat() * lovers.size()));
						lovers.remove(j1);
						j2 = lovers.get((int) Math.floor(game.getRandom().nextFloat() * lovers.size()));
						lovers.add(j1);
					}

					cupid.clearAffectedPlayer();
					cupid.addAffectedPlayer(j1);
					cupid.addAffectedPlayer(j2);
					cupid.setPower(false);

					if (Bukkit.getPlayer(uuid) != null) {
						Bukkit.getPlayer(uuid).sendMessage(game.translate("werewolf.role.cupid.designation_perform", game.playerLG.get(j1).getName(), game.playerLG.get(j2).getName()));
					}
				} else {
					j1 = cupid.getAffectedPlayers().get(0);
					j2 = cupid.getAffectedPlayers().get(1);
				}
				if (!polygamy) {
					lovers.remove(j1);
					lovers.remove(j2);
				}
				if (!game.playerLG.get(j1).getLovers().contains(j2)) {
					game.playerLG.get(j1).addLover(j2);
				}

				if (!game.playerLG.get(j2).getLovers().contains(j1)) {
					game.playerLG.get(j2).addLover(j1);
				}
			}
		}
		for (int i = 0; i < game.config.getRoleCount().get(RoleLG.LOVER); i++) {

			j1 = lovers.get((int) Math.floor(game.getRandom().nextFloat() * lovers.size()));
			lovers.remove(j1);
			j2 = lovers.get((int) Math.floor(game.getRandom().nextFloat() * lovers.size()));
			lovers.add(j1);

			if (!polygamy) {
				lovers.remove(j1);
				lovers.remove(j2);
			}
			if (!game.playerLG.get(j1).getLovers().contains(j2)) {
				game.playerLG.get(j1).addLover(j2);
			}

			if (!game.playerLG.get(j2).getLovers().contains(j1)) {
				game.playerLG.get(j2).addLover(j1);
			}
		}

		rangeLovers();
		reRangeLovers();
		if (!game.isState(StateLG.END)) {
			game.endlg.check_victory();
		}
		autoAmnesiacLovers();
		autoCursedLovers();
	}

	private void reRangeLovers() {

		for (List<UUID> uuidS : loversRange) {
			for (int j = 0; j < uuidS.size(); j++) {
				UUID playerUUID = uuidS.get(j);
				PlayerLG plg = game.playerLG.get(playerUUID);
				plg.clearLovers();
				for (UUID uuid : uuidS) {
					if (!uuid.equals(playerUUID)) {
						plg.addLover(uuid);
					}
				}
				if (Bukkit.getPlayer(playerUUID) != null) {
					announceLovers(Bukkit.getPlayer(playerUUID));
				}
				else plg.setAnnounceLoversAFK(true);
			}
		}
	}

	public void announceLovers(Player player) {

		for (ItemStack k : game.stufflg.role_stuff.get(RoleLG.LOVER)) {

			if (player.getInventory().firstEmpty() == -1) {
				player.getWorld().dropItem(player.getLocation(), k);
			} else {
				player.getInventory().addItem(k);
				player.updateInventory();
			}
		}

		StringBuilder couple = new StringBuilder();

		for (UUID uuid : game.playerLG.get(player.getUniqueId()).getLovers()) {
			couple.append(game.playerLG.get(uuid).getName()).append(" ");
		}
		player.sendMessage(game.translate("werewolf.role.lover.description", couple.toString()));
		player.playSound(player.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
	}


	private void rangeLovers() {

		List<UUID> lovers = new ArrayList<>();

		for (UUID uuid : game.playerLG.keySet()) {
			if (!game.playerLG.get(uuid).getLovers().isEmpty()) {
				lovers.add(uuid);
			}
		}

		while (!lovers.isEmpty()) {

			List<UUID> linkCouple = new ArrayList<>();
			linkCouple.add(lovers.get(0));
			lovers.remove(0);

			for (int j = 0; j < linkCouple.size(); j++) {
				for (UUID uuid : game.playerLG.keySet()) {
					if (game.playerLG.get(uuid).getLovers().contains(linkCouple.get(j))) {
						if (!linkCouple.contains(uuid)) {
							linkCouple.add(uuid);
							lovers.remove(uuid);
						}
					}
				}
			}
			loversRange.add(linkCouple);
		}
		game.config.getRoleCount().put(RoleLG.LOVER, loversRange.size());
	}


	public void thiefLoversRange(UUID killerUUID, UUID playerUUID) {

		int cp = -1;
		int ck = -1;
		for (int i = 0; i < loversRange.size(); i++) {
			if (loversRange.get(i).contains(playerUUID) && !loversRange.get(i).contains(killerUUID)) {
				loversRange.get(i).remove(playerUUID);
				loversRange.get(i).add(killerUUID);
				cp = i;
			} else if (!loversRange.get(i).contains(playerUUID) && loversRange.get(i).contains(killerUUID)) {
				ck = i;
			}
		}
		if (cp != -1 && ck != -1) {
			loversRange.get(ck).remove(killerUUID);
			loversRange.get(cp).addAll(loversRange.get(ck));
			loversRange.remove(ck);
		}
	}
}

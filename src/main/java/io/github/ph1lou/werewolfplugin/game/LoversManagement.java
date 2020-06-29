package io.github.ph1lou.werewolfplugin.game;


import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.ToolLG;
import io.github.ph1lou.werewolfapi.events.CupidLoversEvent;
import io.github.ph1lou.werewolfapi.events.RevealAmnesiacLoversEvent;
import io.github.ph1lou.werewolfapi.events.RevealLoversEvent;
import io.github.ph1lou.werewolfplugin.roles.villagers.Cupid;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class LoversManagement {

	private final List<List<UUID>> loversRange = new ArrayList<>();
	private final List<List<UUID>> cursedLoversRange = new ArrayList<>();

	public List<List<UUID>> getLoversRange() {
		return loversRange;
	}

	public List<List<UUID>> getCursedLoversRange() {
		return cursedLoversRange;
	}

	public List<List<UUID>> getAmnesiacLoversRange() {
		return amnesiacLoversRange;
	}

	private final List<List<UUID>> amnesiacLoversRange = new ArrayList<>();
	private final GameManager game;

	public LoversManagement(GameManager game) {
		this.game = game;
	}


	public void autoCursedLovers() {

		List<UUID> cursedLovers = new ArrayList<>();

		for (UUID uuid : game.getPlayersWW().keySet()) {
			PlayerWW plg = game.getPlayersWW().get(uuid);
			if (plg.isState(State.ALIVE) && plg.getLovers().isEmpty() && plg.getAmnesiacLoverUUID()==null) {
				cursedLovers.add(uuid);
			}
		}

		if (cursedLovers.size() < 2 && game.getConfig().getCursedLoverSize() > 0) {
			Bukkit.broadcastMessage(game.translate("werewolf.role.cursed_lover.not_enough_players"));
			return;
		}

		int i = 0;

		while (cursedLovers.size() >= 2 && i < game.getConfig().getCursedLoverSize()) {

			UUID j1 = cursedLovers.get((int) Math.floor(game.getRandom().nextFloat() * cursedLovers.size()));
			cursedLovers.remove(j1);
			UUID j2 = cursedLovers.get((int) Math.floor(game.getRandom().nextFloat() * cursedLovers.size()));
			cursedLovers.remove(j2);
			game.getPlayersWW().get(j1).setCursedLover(j2);
			game.getPlayersWW().get(j2).setCursedLover(j1);
			i++;
			cursedLoversRange.add(new ArrayList<>(Arrays.asList(j1, j2)));

			if (Bukkit.getPlayer(j1) != null) {
				announceCursedLovers(Bukkit.getPlayer(j1));
			} else game.getPlayersWW().get(j1).setAnnounceCursedLoversAFK(true);
			if (Bukkit.getPlayer(j2) != null) {
				announceCursedLovers(Bukkit.getPlayer(j2));
			} else game.getPlayersWW().get(j2).setAnnounceCursedLoversAFK(true);
		}
	}

	public void autoAmnesiacLovers() {

		List<UUID> amnesiacLovers = new ArrayList<>();

		for (UUID uuid : game.getPlayersWW().keySet()) {
			PlayerWW plg = game.getPlayersWW().get(uuid);
			if (plg.isState(State.ALIVE) && plg.getLovers().isEmpty()) {
				amnesiacLovers.add(uuid);
			}
		}

		if (amnesiacLovers.size() < 2 && game.getConfig().getAmnesiacLoverSize() > 0) {
			Bukkit.broadcastMessage(game.translate("werewolf.role.amnesiac_lover.not_enough_players"));
			return;
		}

		int i = 0;

		while (amnesiacLovers.size() >= 2 && i < game.getConfig().getAmnesiacLoverSize()) {

			UUID j1 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
			amnesiacLovers.remove(j1);
			UUID j2 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
			amnesiacLovers.remove(j2);
			game.getPlayersWW().get(j1).setAmnesiacLoverUUID(j2);
			game.getPlayersWW().get(j2).setAmnesiacLoverUUID(j1);
			i++;
		}
		game.getConfig().setAmnesiacLoverSize(0);
	}

	public void detectionAmnesiacLover(){

		List<PlayerWW> amnesiacLovers = new ArrayList<>();

		for (PlayerWW plg : game.getPlayersWW().values()) {
			if (plg.getAmnesiacLoverUUID()!=null && plg.isState(State.ALIVE) && game.getPlayersWW().get(plg.getAmnesiacLoverUUID()).isState(State.ALIVE)) {
				amnesiacLovers.add(plg);
			}
		}

		while (!amnesiacLovers.isEmpty()){
			try{
				PlayerWW plg = amnesiacLovers.get(0);
				UUID loverUUID =plg.getAmnesiacLoverUUID();
				Player player = Bukkit.getPlayer(plg.getName());
				Player player2 = Bukkit.getPlayer(loverUUID);
				if(!amnesiacLovers.get(0).getRevealAmnesiacLover()){
					if(player.getLocation().distance(player2.getLocation())<game.getConfig().getDistanceAmnesiacLovers()){
						amnesiacLoversRange.add(new ArrayList<>(Arrays.asList(player.getUniqueId(), loverUUID)));
						Bukkit.getPluginManager().callEvent(new RevealAmnesiacLoversEvent(Arrays.asList(player.getUniqueId(), loverUUID)));
						announceAmnesiacLovers(player);
						announceAmnesiacLovers(player2);
						game.getConfig().setAmnesiacLoverSize(game.getConfig().getAmnesiacLoverSize()+1);
						game.checkVictory();
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
		PlayerWW plg = game.getPlayersWW().get(playerUUID);
		plg.setRevealAmnesiacLover(true);
		player.sendMessage(game.translate("werewolf.role.lover.description",game.getPlayersWW().get(plg.getAmnesiacLoverUUID()).getName()));
		player.playSound(player.getLocation(), Sound.PORTAL_TRAVEL, 1, 20);
	}

	public void announceCursedLovers(Player player) {

		UUID playerUUID = player.getUniqueId();
		PlayerWW plg = game.getPlayersWW().get(playerUUID);
		player.setMaxHealth(player.getMaxHealth() + 2);
		player.sendMessage(game.translate("werewolf.role.cursed_lover.description", game.getPlayersWW().get(plg.getCursedLovers()).getName()));
		player.playSound(player.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
	}

	public void autoLovers() {

		List<UUID> lovers = new ArrayList<>();
		for (UUID uuid : game.getPlayersWW().keySet()) {
			if (game.getPlayersWW().get(uuid).isState(State.ALIVE)) {
				lovers.add(uuid);
			}
		}
		if (lovers.size() < 2 && game.getConfig().getRoleCount().get("werewolf.role.cupid.display") + game.getConfig().getLoverSize() > 0) {
			Bukkit.broadcastMessage(game.translate("werewolf.role.lover.not_enough_players"));
			return;
		}

		Boolean polygamy = game.getConfig().getConfigValues().get(ToolLG.POLYGAMY);

		if (!polygamy && (game.getConfig().getLoverSize() == 0 && game.getConfig().getRoleCount().get("werewolf.role.cupid.display") * 2 >= game.getScore().getPlayerSize()) || (game.getConfig().getLoverSize() != 0 && (game.getConfig().getRoleCount().get("werewolf.role.cupid.display") + game.getConfig().getLoverSize()) * 2 > game.getScore().getPlayerSize())) {
			polygamy = true;
			Bukkit.broadcastMessage(game.translate("werewolf.role.lover.polygamy"));
		}
		UUID j1;
		UUID j2;

		for (UUID uuid : game.getPlayersWW().keySet()) {

			PlayerWW plg = game.getPlayersWW().get(uuid);
			if (plg.getRole().isDisplay("werewolf.role.cupid.display")) {

				Cupid cupid = (Cupid) plg.getRole();

				if (cupid.hasPower() || !game.getPlayersWW().get(cupid.getAffectedPlayers().get(0)).isState(State.ALIVE) || !game.getPlayersWW().get(cupid.getAffectedPlayers().get(1)).isState(State.ALIVE)) {

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
					Bukkit.getPluginManager().callEvent(new CupidLoversEvent(uuid,cupid.getAffectedPlayers()));
					if (Bukkit.getPlayer(uuid) != null) {
						Bukkit.getPlayer(uuid).sendMessage(game.translate("werewolf.role.cupid.designation_perform", game.getPlayersWW().get(j1).getName(), game.getPlayersWW().get(j2).getName()));
					}
				} else {
					j1 = cupid.getAffectedPlayers().get(0);
					j2 = cupid.getAffectedPlayers().get(1);
				}
				if (!polygamy) {
					lovers.remove(j1);
					lovers.remove(j2);
				}
				if (!game.getPlayersWW().get(j1).getLovers().contains(j2)) {
					game.getPlayersWW().get(j1).addLover(j2);
				}

				if (!game.getPlayersWW().get(j2).getLovers().contains(j1)) {
					game.getPlayersWW().get(j2).addLover(j1);
				}
			}
		}
		for (int i = 0; i < game.getConfig().getLoverSize(); i++) {

			j1 = lovers.get((int) Math.floor(game.getRandom().nextFloat() * lovers.size()));
			lovers.remove(j1);
			j2 = lovers.get((int) Math.floor(game.getRandom().nextFloat() * lovers.size()));
			lovers.add(j1);

			if (!polygamy) {
				lovers.remove(j1);
				lovers.remove(j2);
			}
			if (!game.getPlayersWW().get(j1).getLovers().contains(j2)) {
				game.getPlayersWW().get(j1).addLover(j2);
			}

			if (!game.getPlayersWW().get(j2).getLovers().contains(j1)) {
				game.getPlayersWW().get(j2).addLover(j1);
			}
		}

		rangeLovers();
		reRangeLovers();

		game.getConfig().setLoverSize(this.loversRange.size());
		Bukkit.getPluginManager().callEvent(new RevealLoversEvent(this.loversRange));
		autoAmnesiacLovers();

		autoCursedLovers();
		Bukkit.getPluginManager().callEvent(new RevealLoversEvent(this.cursedLoversRange));
		game.checkVictory();
	}

	private void reRangeLovers() {

		for (List<UUID> uuidS : loversRange) {
			for (int j = 0; j < uuidS.size(); j++) {
				UUID playerUUID = uuidS.get(j);
				PlayerWW plg = game.getPlayersWW().get(playerUUID);
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

		StringBuilder couple = new StringBuilder();

		for (UUID uuid : game.getPlayersWW().get(player.getUniqueId()).getLovers()) {
			couple.append(game.getPlayersWW().get(uuid).getName()).append(" ");
		}
		player.sendMessage(game.translate("werewolf.role.lover.description", couple.toString()));
		player.playSound(player.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
	}


	private void rangeLovers() {

		List<UUID> lovers = new ArrayList<>();

		for (UUID uuid : game.getPlayersWW().keySet()) {
			if (!game.getPlayersWW().get(uuid).getLovers().isEmpty()) {
				lovers.add(uuid);
			}
		}

		while (!lovers.isEmpty()) {

			List<UUID> linkCouple = new ArrayList<>();
			linkCouple.add(lovers.get(0));
			lovers.remove(0);

			for (int j = 0; j < linkCouple.size(); j++) {
				for (UUID uuid : game.getPlayersWW().keySet()) {
					if (game.getPlayersWW().get(uuid).getLovers().contains(linkCouple.get(j))) {
						if (!linkCouple.contains(uuid)) {
							linkCouple.add(uuid);
							lovers.remove(uuid);
						}
					}
				}
			}
			loversRange.add(linkCouple);
		}
	}

	public void checkLovers(UUID playerUUID) {

		int i = 0;

		while (i < loversRange.size() && !loversRange.get(i).contains(playerUUID)) {
			i++;
		}

		if (i < loversRange.size()) {

			loversRange.get(i).remove(playerUUID);

			while (!loversRange.get(i).isEmpty() && game.getPlayersWW().get(loversRange.get(i).get(0)).isState(State.DEATH)) {
				loversRange.get(i).remove(0);
			}

			if (!loversRange.get(i).isEmpty()) {
				UUID c1 = loversRange.get(i).get(0);
				Bukkit.broadcastMessage(game.translate("werewolf.role.lover.lover_death", game.getPlayersWW().get(c1).getName()));

				game.death(c1);
			} else {
				loversRange.remove(i);
				game.getConfig().setLoverSize(game.getConfig().getLoverSize() - 1);
			}
		}
	}

	public void checkAmnesiacLovers(UUID playerUUID) {

		int i = 0;

		while (i < amnesiacLoversRange.size() && !amnesiacLoversRange.get(i).contains(playerUUID)) {
			i++;
		}

		if (i < amnesiacLoversRange.size()) {

			amnesiacLoversRange.get(i).remove(playerUUID);
			UUID c1 = amnesiacLoversRange.get(i).get(0);
			amnesiacLoversRange.remove(i);
			Bukkit.broadcastMessage(game.translate("werewolf.role.lover.lover_death", game.getPlayersWW().get(c1).getName()));
			game.death(c1);
			game.getConfig().setAmnesiacLoverSize(game.getConfig().getAmnesiacLoverSize() - 1);
		}
	}

	public void checkCursedLovers(UUID playerUUID) {

		int i = 0;

		while (i < cursedLoversRange.size() && !cursedLoversRange.get(i).contains(playerUUID)) {
			i++;
		}

		if (i < cursedLoversRange.size()) {

			cursedLoversRange.get(i).remove(playerUUID);

			UUID cursedLover = cursedLoversRange.get(i).get(0);

			if (Bukkit.getPlayer(cursedLover) != null) {
				Player killer = Bukkit.getPlayer(cursedLover);
				killer.sendMessage(game.translate("werewolf.role.cursed_lover.death_cursed_lover"));
				killer.setMaxHealth(Math.max(killer.getMaxHealth() - 2, 1));
			}
			cursedLoversRange.remove(i);
			game.getConfig().setCursedLoverSize(game.getConfig().getCursedLoverSize() - 1);
		}
	}



}

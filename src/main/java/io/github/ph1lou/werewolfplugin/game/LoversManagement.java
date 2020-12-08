package io.github.ph1lou.werewolfplugin.game;


import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.LoverManagerAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.ConfigsBase;
import io.github.ph1lou.werewolfapi.enumlg.RolesBase;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.CupidLoversEvent;
import io.github.ph1lou.werewolfapi.events.RevealLoversEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.LoverAPI;
import io.github.ph1lou.werewolfplugin.roles.lovers.AmnesiacLover;
import io.github.ph1lou.werewolfplugin.roles.lovers.CursedLover;
import io.github.ph1lou.werewolfplugin.roles.lovers.Lover;
import io.github.ph1lou.werewolfplugin.roles.villagers.Cupid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class LoversManagement implements LoverManagerAPI {


	private final List<LoverAPI> lovers = new ArrayList<>();

	private final WereWolfAPI game;

	public LoversManagement(WereWolfAPI game) {
		this.game = game;
	}


	public void autoCursedLovers() {

		List<PlayerWW> cursedLovers = new ArrayList<>();

		for (PlayerWW playerWW1 : game.getPlayerWW()) {
			if (playerWW1.isState(StatePlayer.ALIVE) &&
					playerWW1.getLovers().isEmpty()) {
				cursedLovers.add(playerWW1);
			}
		}

		if (cursedLovers.size() < 2 && game.getConfig().getCursedLoverSize() > 0) {
			Bukkit.broadcastMessage(game.translate("werewolf.role.cursed_lover.not_enough_players"));
			game.getConfig().setCursedLoverSize(0);
			return;
		}

		int i = 0;

		while (cursedLovers.size() >= 2 && i < game.getConfig().getCursedLoverSize()) {

			PlayerWW playerWW1 = cursedLovers.get((int) Math.floor(game.getRandom().nextFloat() * cursedLovers.size()));
			UUID uuid1 = playerWW1.getUUID();
			cursedLovers.remove(playerWW1);
			PlayerWW playerWW2 = cursedLovers.get((int) Math.floor(game.getRandom().nextFloat() * cursedLovers.size()));
			UUID uuid2 = playerWW2.getUUID();
			cursedLovers.remove(playerWW2);
			CursedLover cursedLover = new CursedLover(game, playerWW1, playerWW2);
			i++;
			lovers.add(cursedLover);
			Player player1 = Bukkit.getPlayer(uuid1);
			Player player2 = Bukkit.getPlayer(uuid2);
			if (player1 != null) {
				cursedLover.announceCursedLoversOnJoin(player1);
			}
			if (player2 != null) {
				cursedLover.announceCursedLoversOnJoin(player2);
			}
		}
	}

	public void autoAmnesiacLovers() {

		List<PlayerWW> amnesiacLovers = new ArrayList<>();

		for (PlayerWW playerWW1 : game.getPlayerWW()) {

			if (playerWW1.isState(StatePlayer.ALIVE) && playerWW1.getLovers().isEmpty()) {
				amnesiacLovers.add(playerWW1);
			}
		}

		if (amnesiacLovers.size() < 2 && game.getConfig().getAmnesiacLoverSize() > 0) {
			Bukkit.broadcastMessage(game.translate("werewolf.role.amnesiac_lover.not_enough_players"));
			game.getConfig().setAmnesiacLoverSize(0);
			return;
		}

		int i = 0;

		while (amnesiacLovers.size() >= 2 && i < game.getConfig().getAmnesiacLoverSize()) {

			PlayerWW playerWW1 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
			amnesiacLovers.remove(playerWW1);
			PlayerWW playerWW2 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
			amnesiacLovers.remove(playerWW2);
			lovers.add(new AmnesiacLover(game, playerWW1, playerWW2));
			i++;
		}
		game.getConfig().setAmnesiacLoverSize(0);
	}


	public void repartition(GetWereWolfAPI main) {

		List<PlayerWW> loversAvailable = new ArrayList<>();
		for (PlayerWW playerWW1 : game.getPlayerWW()) {
			if (playerWW1.isState(StatePlayer.ALIVE)) {
				loversAvailable.add(playerWW1);
			}
		}
		if (loversAvailable.size() < 2 && game.getConfig().getRoleCount().get(RolesBase.CUPID.getKey()) +
				game.getConfig().getLoverSize() > 0) {
			Bukkit.broadcastMessage(game.translate("werewolf.role.lover.not_enough_players"));
			return;
		}

		boolean polygamy = game.getConfig().getConfigValues().get(ConfigsBase.POLYGAMY.getKey());

		if (!polygamy && (game.getConfig().getLoverSize() == 0 &&
				game.getConfig().getRoleCount().get(RolesBase.CUPID.getKey()) * 2 >=
						game.getScore().getPlayerSize()) ||
				(game.getConfig().getLoverSize() != 0 &&
						(game.getConfig().getRoleCount().get(RolesBase.CUPID.getKey()) +
								game.getConfig().getLoverSize()) * 2 >
								game.getScore().getPlayerSize())) {

			polygamy = true;
			Bukkit.broadcastMessage(game.translate("werewolf.role.lover.polygamy"));
		}

		PlayerWW playerWW1;
		PlayerWW playerWW2;

		for (PlayerWW playerWW : game.getPlayerWW()) {

			if (playerWW.isKey(RolesBase.CUPID.getKey())) {

				UUID uuid = playerWW.getUUID();
				Cupid cupid = (Cupid) playerWW.getRole();

				if (cupid.hasPower()) {

					playerWW1 = cupid.getAffectedPlayers().get(0);
					playerWW2 = cupid.getAffectedPlayers().get(1);

					if (!playerWW1.isState(StatePlayer.ALIVE) ||
							!playerWW2.isState(StatePlayer.ALIVE)) {
						if (loversAvailable.contains(playerWW)) {
							loversAvailable.remove(playerWW);
							playerWW1 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
							loversAvailable.remove(playerWW1);
							playerWW2 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
							loversAvailable.add(playerWW1);
							loversAvailable.add(playerWW);
						} else {
							playerWW1 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
							loversAvailable.remove(playerWW1);
							playerWW2 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
							loversAvailable.add(playerWW1);
						}

						cupid.clearAffectedPlayer();
						cupid.addAffectedPlayer(playerWW1);
						cupid.addAffectedPlayer(playerWW2);
						cupid.setPower(false);
						Bukkit.getPluginManager().callEvent(new CupidLoversEvent(playerWW, Sets.newHashSet(cupid.getAffectedPlayers())));
						Player player1 = Bukkit.getPlayer(uuid);
						if (player1 != null) {
							player1.sendMessage(game.translate("werewolf.role.cupid.designation_perform", playerWW1.getName(), playerWW2.getName()));
						}
					}

				} else {
					playerWW1 = cupid.getAffectedPlayers().get(0);
					playerWW2 = cupid.getAffectedPlayers().get(1);
				}
				if (!polygamy) {
					loversAvailable.remove(playerWW1);
					loversAvailable.remove(playerWW2);
				}
				lovers.add(new Lover(game, cupid.getAffectedPlayers()));
			}
		}

		for (int i = 0; i < game.getConfig().getLoverSize(); i++) {

			playerWW1 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
			loversAvailable.remove(playerWW1);
			playerWW2 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
			loversAvailable.add(playerWW1);

			if (!polygamy) {
				loversAvailable.remove(playerWW1);
				loversAvailable.remove(playerWW2);
			}

			lovers.add(new Lover(game, new ArrayList<>(Arrays.asList(playerWW1, playerWW2))));
		}

		rangeLovers();
		game.getConfig().setLoverSize(lovers.size());
		Bukkit.getPluginManager().callEvent(new RevealLoversEvent(this.lovers));
		autoAmnesiacLovers();
		autoCursedLovers();
		lovers
				.forEach(loverAPI -> Bukkit.getPluginManager()
						.registerEvents((Listener) loverAPI, (Plugin) main));


		game.checkVictory();
	}



	private void rangeLovers() {

		List<Lover> loverAPIS = new ArrayList<>();
		List<PlayerWW> loversAvailable = game.getPlayerWW().stream()
				.filter(playerWW -> !playerWW.getLovers().isEmpty())
				.collect(Collectors.toList());

		while (!loversAvailable.isEmpty()) {

			List<PlayerWW> linkCouple = new ArrayList<>();
			linkCouple.add(loversAvailable.remove(0));

			for (int j = 0; j < linkCouple.size(); j++) {
				for (PlayerWW playerWW : game.getPlayerWW()) {
					for (LoverAPI loverAPI : playerWW.getLovers()) {
						if (loverAPI.getLovers().contains(linkCouple.get(j))) {
							if (!linkCouple.contains(playerWW)) {
								linkCouple.add(playerWW);
								loversAvailable.remove(playerWW);
							}
						}
					}
				}
			}
			loverAPIS.add(new Lover(game, linkCouple));
		}
		for (LoverAPI loverAPI : lovers) {
			loverAPI.getLovers()
					.forEach(playerWW -> playerWW.getLovers()
							.remove(loverAPI));
		}
		lovers.clear();
		lovers.addAll(loverAPIS);
		loverAPIS.forEach(Lover::announceLovers);
	}


	@Override
	public List<LoverAPI> getLovers() {
		return lovers;
	}
}

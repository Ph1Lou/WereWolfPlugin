package io.github.ph1lou.werewolfplugin.game;


import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.ILoverManager;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.lovers.CupidLoversEvent;
import io.github.ph1lou.werewolfapi.events.lovers.LoversRepartitionEvent;
import io.github.ph1lou.werewolfapi.events.lovers.RevealLoversEvent;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfplugin.roles.lovers.AmnesiacLover;
import io.github.ph1lou.werewolfplugin.roles.lovers.CursedLover;
import io.github.ph1lou.werewolfplugin.roles.lovers.Lover;
import io.github.ph1lou.werewolfplugin.roles.villagers.Cupid;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class LoversManagement implements ILoverManager {


	private final List<ILover> lovers = new ArrayList<>();

	private final WereWolfAPI game;

	public LoversManagement(WereWolfAPI game) {
		this.game = game;
	}


	private void autoCursedLovers() {

		List<IPlayerWW> cursedLovers = game.getPlayersWW().stream()
				.filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
				.filter(playerWW -> playerWW.getLovers().isEmpty())
				.collect(Collectors.toList());


		if (cursedLovers.size() < 2 && game.getConfig().getLoverCount(LoverType.CURSED_LOVER.getKey()) > 0) {
			Bukkit.broadcastMessage(game.translate(Prefix.RED.getKey() , "werewolf.role.cursed_lover.not_enough_players"));
			game.getConfig().setLoverCount(LoverType.CURSED_LOVER.getKey(), 0);
			return;
		}

		int i = 0;

		while (cursedLovers.size() >= 2 && i < game.getConfig().getLoverCount(LoverType.CURSED_LOVER.getKey())) {

			IPlayerWW playerWW1 = cursedLovers.get((int) Math.floor(game.getRandom().nextFloat() * cursedLovers.size()));
			cursedLovers.remove(playerWW1);
			IPlayerWW playerWW2 = cursedLovers.get((int) Math.floor(game.getRandom().nextFloat() * cursedLovers.size()));
			cursedLovers.remove(playerWW2);
			CursedLover cursedLover = new CursedLover(game, playerWW1, playerWW2);
			i++;
			lovers.add(cursedLover);
			cursedLover.announceCursedLoversOnJoin(playerWW1);
			cursedLover.announceCursedLoversOnJoin(playerWW2);
		}
	}

	private void autoAmnesiacLovers() {

		List<IPlayerWW> amnesiacLovers = game.getPlayersWW().stream()
				.filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
				.filter(playerWW -> playerWW.getLovers().isEmpty())
				.filter(playerWW -> !playerWW.getRole().isKey(RolesBase.CHARMER.getKey()))
				.filter(playerWW -> !playerWW.getRole().isKey(RolesBase.RIVAL.getKey()))
				.collect(Collectors.toList());

		if (amnesiacLovers.size() < 2 && game.getConfig().getLoverCount(LoverType.AMNESIAC_LOVER.getKey()) > 0) {
			Bukkit.broadcastMessage(game.translate(Prefix.RED.getKey() , "werewolf.role.amnesiac_lover.not_enough_players"));
			game.getConfig().setLoverCount(LoverType.AMNESIAC_LOVER.getKey(), 0);
			return;
		}

		int i = 0;

		while (amnesiacLovers.size() >= 2 && i < game.getConfig().getLoverCount(LoverType.AMNESIAC_LOVER.getKey())) {

			IPlayerWW playerWW1 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
			amnesiacLovers.remove(playerWW1);
			IPlayerWW playerWW2 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
			amnesiacLovers.remove(playerWW2);
			lovers.add(new AmnesiacLover(game, playerWW1, playerWW2));
			i++;
		}
		game.getConfig().setLoverCount(LoverType.AMNESIAC_LOVER.getKey(), 0);
	}


	public void repartition() {
		Bukkit.getPluginManager().callEvent(new LoversRepartitionEvent());
		List<ILover> temp = new ArrayList<>(this.lovers); // sauvegarder les couples mis manuellement avant la repartition pour pas les trier
		this.lovers.clear();
		this.autoLovers();
		this.rangeLovers();
		game.getConfig().setLoverCount(LoverType.LOVER.getKey(), this.lovers.size());
		this.autoAmnesiacLovers();
		this.autoCursedLovers();
		this.lovers.addAll(temp);
		this.lovers
				.forEach(lovers -> {
					BukkitUtils
							.registerEvents(lovers);
					lovers.getLovers().forEach(playerWW -> Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW)));
				});
		Bukkit.getPluginManager().callEvent(new RevealLoversEvent(this.lovers));
		this.game.checkVictory();
	}

	private void autoLovers() {

		List<IPlayerWW> loversAvailable = game.getPlayersWW().stream()
				.filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
				.filter(playerWW -> !playerWW.getRole().isKey(RolesBase.CHARMER.getKey()))
				.filter(playerWW -> !playerWW.getRole().isKey(RolesBase.RIVAL.getKey()))
				.filter(playerWW -> playerWW.getLovers().isEmpty())
				.collect(Collectors.toList());

		if (loversAvailable.size() < 2 && game.getConfig().getRoleCount(RolesBase.CUPID.getKey()) +
				game.getConfig().getLoverCount(LoverType.LOVER.getKey()) > 0) {
			Bukkit.broadcastMessage(game.translate(Prefix.RED.getKey() , "werewolf.role.lover.not_enough_players"));
			return;
		}

		boolean polygamy = false;

		if ((game.getConfig().getLoverCount(LoverType.LOVER.getKey()) == 0 &&
				game.getConfig().getRoleCount(RolesBase.CUPID.getKey()) * 2 >=
						game.getPlayerSize()) ||
				(game.getConfig().getLoverCount(LoverType.LOVER.getKey()) != 0 &&
						(game.getConfig().getRoleCount(RolesBase.CUPID.getKey()) +
								game.getConfig().getLoverCount(LoverType.LOVER.getKey())) * 2 >
								game.getPlayerSize())) {

			polygamy = true;
			Bukkit.broadcastMessage(game.translate(Prefix.ORANGE.getKey() , "werewolf.role.lover.polygamy"));
		}

		IPlayerWW playerWW1;
		IPlayerWW playerWW2;


		for (IPlayerWW playerWW : game.getPlayersWW()) {

			if (playerWW.getRole().isKey(RolesBase.CUPID.getKey())) {

				Cupid cupid = (Cupid) playerWW.getRole();

				if (cupid.hasPower() ||
						cupid.getAffectedPlayers().size() < 2 ||
						!cupid.getAffectedPlayers().get(0).isState(StatePlayer.ALIVE) ||
						!cupid.getAffectedPlayers().get(1).isState(StatePlayer.ALIVE)) {

					if (loversAvailable.contains(playerWW)) {
						loversAvailable.remove(playerWW);
						playerWW2 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
						loversAvailable.remove(playerWW2);
						playerWW1 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
						loversAvailable.add(playerWW2);
						loversAvailable.add(playerWW);
					} else {
						playerWW2 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
						loversAvailable.remove(playerWW2);
						playerWW1 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
						loversAvailable.add(playerWW2);
					}

					cupid.clearAffectedPlayer();
					cupid.addAffectedPlayer(playerWW2);
					cupid.addAffectedPlayer(playerWW1);
					cupid.setPower(false);
					Bukkit.getPluginManager().callEvent(new CupidLoversEvent(playerWW, Sets.newHashSet(cupid.getAffectedPlayers())));
					playerWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.cupid.designation_perform",
							Formatter.format("&player1&",playerWW2.getName()),
							Formatter.format("&player2&",playerWW1.getName()));

				} else {
					playerWW2 = cupid.getAffectedPlayers().get(0);
					playerWW1 = cupid.getAffectedPlayers().get(1);
				}
				if (!polygamy) {
					loversAvailable.remove(playerWW2);
					loversAvailable.remove(playerWW1);
				}
				lovers.add(new Lover(game, cupid.getAffectedPlayers()));
			}
		}

		for (int i = 0; i < game.getConfig().getLoverCount(LoverType.LOVER.getKey()); i++) {

			playerWW2 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
			loversAvailable.remove(playerWW2);
			playerWW1 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
			loversAvailable.add(playerWW2);

			if (!polygamy) {
				loversAvailable.remove(playerWW2);
				loversAvailable.remove(playerWW1);
			}

			lovers.add(new Lover(game, new ArrayList<>(Arrays.asList(playerWW2, playerWW1))));
		}
	}


	private void rangeLovers() {

		List<Lover> loverAPIS = new ArrayList<>();
		List<IPlayerWW> loversAvailable = game.getPlayersWW().stream()
				.filter(playerWW -> !playerWW.getLovers().isEmpty())
				.filter(playerWW -> playerWW
						.getLovers()
						.stream().anyMatch(lover -> lover.getKey().equals(LoverType.LOVER.getKey())))
				.collect(Collectors.toList());

		while (!loversAvailable.isEmpty()) {

			List<IPlayerWW> linkCouple = new ArrayList<>();
			linkCouple.add(loversAvailable.remove(0));

			for (int j = 0; j < linkCouple.size(); j++) {

				IPlayerWW playerWWLover = linkCouple.get(j);

				game.getPlayersWW().forEach(playerWW -> playerWW
						.getLovers()
						.stream()
						.filter(iLover -> iLover.getKey().equals(LoverType.LOVER.getKey()))
						.forEach(lover -> {
							if (lover.getLovers().contains(playerWWLover)) {
								if (!linkCouple.contains(playerWW)) {
									linkCouple.add(playerWW);
									loversAvailable.remove(playerWW);
								}
							}
						}));
			}
			loverAPIS.add(new Lover(game, linkCouple));
		}
		for (ILover lover : lovers) {
			lover.getLovers()
					.forEach(playerWW -> playerWW.getLovers()
							.remove(lover));
		}
		lovers.clear();
		lovers.addAll(loverAPIS);
		loverAPIS.forEach(Lover::announceLovers);
	}


	@Override
	public List<? extends ILover> getLovers() {
		return lovers;
	}

	@Override
	public void removeLover(ILover lover) {
		this.lovers.remove(lover);
	}

	@Override
	public void addLover(ILover lover) {
		this.lovers.add(lover);
	}
}

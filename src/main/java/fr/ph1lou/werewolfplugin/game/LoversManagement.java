package fr.ph1lou.werewolfplugin.game;


import com.google.common.collect.Sets;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfplugin.roles.lovers.AmnesiacLover;
import fr.ph1lou.werewolfplugin.roles.lovers.CursedLover;
import fr.ph1lou.werewolfplugin.roles.lovers.LoverImpl;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.lovers.ILoverManager;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.lovers.CupidLoversEvent;
import fr.ph1lou.werewolfapi.events.lovers.RevealLoversEvent;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.roles.villagers.Cupid;
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


		if (cursedLovers.size() < 2 && game.getConfig().getLoverCount(LoverBase.CURSED_LOVER) > 0) {
			Bukkit.broadcastMessage(game.translate(Prefix.RED , "werewolf.role.cursed_lover.not_enough_players"));
			game.getConfig().setLoverCount(LoverBase.CURSED_LOVER, 0);
			return;
		}

		int i = 0;

		while (cursedLovers.size() >= 2 && i < game.getConfig().getLoverCount(LoverBase.CURSED_LOVER)) {

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
				.filter(playerWW -> !playerWW.getRole().isKey(RoleBase.CHARMER))
				.filter(playerWW -> !playerWW.getRole().isKey(RoleBase.RIVAL))
				.collect(Collectors.toList());

		if (amnesiacLovers.size() < 2 && game.getConfig().getLoverCount(LoverBase.AMNESIAC_LOVER) > 0) {
			Bukkit.broadcastMessage(game.translate(Prefix.RED , "werewolf.role.amnesiac_lover.not_enough_players"));
			game.getConfig().setLoverCount(LoverBase.AMNESIAC_LOVER, 0);
			return;
		}

		int i = 0;

		while (amnesiacLovers.size() >= 2 && i < game.getConfig().getLoverCount(LoverBase.AMNESIAC_LOVER)) {

			IPlayerWW playerWW1 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
			amnesiacLovers.remove(playerWW1);
			IPlayerWW playerWW2 = amnesiacLovers.get((int) Math.floor(game.getRandom().nextFloat() * amnesiacLovers.size()));
			amnesiacLovers.remove(playerWW2);
			lovers.add(new AmnesiacLover(game, playerWW1, playerWW2));
			i++;
		}
		game.getConfig().setLoverCount(LoverBase.AMNESIAC_LOVER, 0);
	}


	public void repartition() {
		List<ILover> temp = new ArrayList<>(this.lovers); // sauvegarder les couples mis manuellement avant la repartition pour pas les trier
		this.lovers.clear();
		this.autoLovers();
		this.rangeLovers();
		game.getConfig().setLoverCount(LoverBase.LOVER, this.lovers.size());
		this.autoAmnesiacLovers();
		this.autoCursedLovers();
		this.lovers.addAll(temp);
		this.lovers
				.forEach(lovers -> {
					BukkitUtils
							.registerListener(lovers);
					lovers.getLovers().forEach(playerWW -> Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW)));
				});
		Bukkit.getPluginManager().callEvent(new RevealLoversEvent(this.lovers));
		this.game.checkVictory();
	}

	private void autoLovers() {

		List<IPlayerWW> loversAvailable = game.getPlayersWW().stream()
				.filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
				.filter(playerWW -> !playerWW.getRole().isKey(RoleBase.CHARMER))
				.filter(playerWW -> !playerWW.getRole().isKey(RoleBase.RIVAL))
				.filter(playerWW -> playerWW.getLovers().isEmpty())
				.collect(Collectors.toList());

		if (loversAvailable.size() < 2 && game.getConfig().getRoleCount(RoleBase.CUPID) +
				game.getConfig().getLoverCount(LoverBase.LOVER) > 0) {
			Bukkit.broadcastMessage(game.translate(Prefix.RED , "werewolf.role.lover.not_enough_players"));
			return;
		}

		boolean polygamy = false;

		if ((game.getConfig().getLoverCount(LoverBase.LOVER) == 0 &&
				game.getConfig().getRoleCount(RoleBase.CUPID) * 2 >=
						game.getPlayersCount()) ||
				(game.getConfig().getLoverCount(LoverBase.LOVER) != 0 &&
						(game.getConfig().getRoleCount(RoleBase.CUPID) +
								game.getConfig().getLoverCount(LoverBase.LOVER)) * 2 >
								game.getPlayersCount())) {

			polygamy = true;
			Bukkit.broadcastMessage(game.translate(Prefix.ORANGE , "werewolf.role.lover.polygamy"));
		}

		IPlayerWW playerWW1;
		IPlayerWW playerWW2;


		for (IPlayerWW playerWW : game.getPlayersWW()) {

			if (playerWW.getRole().isKey(RoleBase.CUPID)) {

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
					playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.role.cupid.designation_perform",
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
				lovers.add(new LoverImpl(game, cupid.getAffectedPlayers()));
			}
		}

		for (int i = 0; i < game.getConfig().getLoverCount(LoverBase.LOVER); i++) {

			playerWW2 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
			loversAvailable.remove(playerWW2);
			playerWW1 = loversAvailable.get((int) Math.floor(game.getRandom().nextFloat() * loversAvailable.size()));
			loversAvailable.add(playerWW2);

			if (!polygamy) {
				loversAvailable.remove(playerWW2);
				loversAvailable.remove(playerWW1);
			}

			lovers.add(new LoverImpl(game, new ArrayList<>(Arrays.asList(playerWW2, playerWW1))));
		}
	}


	private void rangeLovers() {

		List<LoverImpl> loverImplAPIS = new ArrayList<>();
		List<IPlayerWW> loversAvailable = game.getPlayersWW().stream()
				.filter(playerWW -> !playerWW.getLovers().isEmpty())
				.filter(playerWW -> playerWW
						.getLovers()
						.stream().anyMatch(lover -> lover.getKey().equals(LoverBase.LOVER)))
				.collect(Collectors.toList());

		while (!loversAvailable.isEmpty()) {

			List<IPlayerWW> linkCouple = new ArrayList<>();
			linkCouple.add(loversAvailable.remove(0));

			for (int j = 0; j < linkCouple.size(); j++) {

				IPlayerWW playerWWLover = linkCouple.get(j);

				game.getPlayersWW().forEach(playerWW -> playerWW
						.getLovers()
						.stream()
						.filter(iLover -> iLover.getKey().equals(LoverBase.LOVER))
						.forEach(lover -> {
							if (lover.getLovers().contains(playerWWLover)) {
								if (!linkCouple.contains(playerWW)) {
									linkCouple.add(playerWW);
									loversAvailable.remove(playerWW);
								}
							}
						}));
			}
			loverImplAPIS.add(new LoverImpl(game, linkCouple));
		}
		for (ILover lover : lovers) {
			lover.getLovers()
					.forEach(playerWW -> playerWW.getLovers()
							.remove(lover));
		}
		lovers.clear();
		lovers.addAll(loverImplAPIS);
		loverImplAPIS.forEach(LoverImpl::announceLovers);
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

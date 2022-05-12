package fr.ph1lou.werewolfplugin.tasks;


import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfplugin.Register;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.save.Configuration;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.enums.StateGame;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;


public class GameTask extends BukkitRunnable {

	private final GameManager game;

	public GameTask(GameManager game) {
		this.game = game;
	}

	@Override
	public void run() {

		if (game.isState(StateGame.END)) {
			game.getScore().updateBoard();
			cancel();
			return;
		}

		World world = game.getMapManager().getWorld();
		game.getScore().updateBoard();

		game.getPlayersWW().stream().map(IPlayerWW::getRole).forEach(role -> {
			try {
				role.second();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		});

		game.getLoversManager().getLovers().forEach(lover -> {
			try {
				lover.second();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		});

		//World Border
		IConfiguration config = game.getConfig();
		WorldBorder worldBorder = world.getWorldBorder();

		if (config.getBorderMax() != config.getBorderMin()) {
			worldBorder.setSize(config.getBorderMin(), (long) ((long) Math.abs(worldBorder.getSize() - config.getBorderMin()) / config.getBorderSpeed()));
			config.setBorderMax((int) (worldBorder.getSize()));
		}


		world.setTime((long) (world.getTime() + 20 *
				(600f /
						game.getConfig().getTimerValue(
								"werewolf.menu.timers.day_duration") - 1)));

		game.setTimer(game.getTimer()+1);

		Register.get().getTimersRegister()
				.forEach(timerRegister -> {

					if (timerRegister.getMetaDatas().decrement() ||
							(timerRegister.getMetaDatas().decrementAfterRole() &&
									!game.getConfig().isTrollSV() &&
									game.getConfig().getTimerValue(TimerBase.ROLE_DURATION) < 0)) {
						if (game.getConfig().getTimerValue(timerRegister.getMetaDatas().key()) == 0) {
							try {
								Bukkit.getPluginManager().callEvent(
										timerRegister.getMetaDatas().onZero().getConstructor()
												.newInstance()
								);
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
								e.printStackTrace();
							}
						}
						((Configuration) game.getConfig()).decreaseTimer(timerRegister.getMetaDatas().key());
					}
				});

	}

}


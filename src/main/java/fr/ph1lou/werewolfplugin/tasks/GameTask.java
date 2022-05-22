package fr.ph1lou.werewolfplugin.tasks;


import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.save.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;


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

		if(game.getConfig().getTimerValue(TimerBase.BORDER_BEGIN) < 0){
			//World Border
			IConfiguration config = game.getConfig();
			WorldBorder worldBorder = world.getWorldBorder();

			if (config.getBorderMax() != config.getBorderMin()) {
				worldBorder.setSize(config.getBorderMin(), (long) ((long) Math.abs(worldBorder.getSize() - config.getBorderMin()) / config.getBorderSpeed()));
				config.setBorderMax((int) (worldBorder.getSize()));
			}
		}



		world.setTime((long) (world.getTime() + 20 *
				(600f /
						game.getConfig().getTimerValue(
								"werewolf.menu.timers.day_duration") - 1)));

		game.setTimer(game.getTimer()+1);

		Stream.concat(Register.get().getTimersRegister()
						.stream()
						.map(Wrapper::getMetaDatas),
				Stream.concat(Register.get().getRolesRegister().stream()
						.map(Wrapper::getMetaDatas)
						.flatMap(role -> Stream.of(role.timers())),
						Register.get().getRandomEventsRegister().stream()
								.map(Wrapper::getMetaDatas)
								.flatMap(role -> Stream.of(role.timers()))))
				.forEach(timerRegister -> {

					if (timerRegister.decrement() ||
							(timerRegister.decrementAfterRole() &&
									!game.getConfig().isTrollSV() &&
									game.getConfig().getTimerValue(TimerBase.ROLE_DURATION) < 0) ||
							(!timerRegister.decrementAfterTimer().equals("") &&
									game.getConfig().getTimerValue(timerRegister.decrementAfterTimer()) < 0)) {
						if (game.getConfig().getTimerValue(timerRegister.key()) == 0) {
							try {
								if(!timerRegister.onZero().equals(Event.class)){
									Bukkit.getPluginManager().callEvent(
											timerRegister.onZero().getConstructor()
													.newInstance()
									);
								}
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
							}
						}
						((Configuration) game.getConfig()).decreaseTimer(timerRegister.key());
					}
				});

	}

}


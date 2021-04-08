package io.github.ph1lou.werewolfplugin.tasks;


import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfplugin.RegisterManager;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.save.Configuration;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;


public class GameTask extends BukkitRunnable {

	private final GameManager game;

	public GameTask(GameManager game) {
		this.game = game;
	}

	@Override
	public void run() {

		if (game.isState(StateGame.END)) {
			cancel();
			return;
		}

		World world = game.getMapManager().getWorld();
		game.getScore().updateBoard();

		game.getPlayerWW().stream().map(IPlayerWW::getRole).forEach(role -> {
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


		world.setTime((long) (world.getTime() + 20 *
				(600f /
						game.getConfig().getTimerValue(
								"werewolf.menu.timers.day_duration") - 1)));

		game.getScore().addTimer();

		RegisterManager.get().getTimersRegister()
				.forEach(timerRegister -> {

					if (timerRegister.getPredicate().test(game)) {
						if (game.getConfig().getTimerValue(timerRegister.getKey()) == 0) {
							timerRegister.getConsumer().accept(game);
						}
						((Configuration) game.getConfig()).decreaseTimer(timerRegister.getKey());
					}
				});

	}

}


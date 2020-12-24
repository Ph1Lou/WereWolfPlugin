package io.github.ph1lou.werewolfplugin.tasks;


import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
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
		PluginManager pm = Bukkit.getPluginManager();
		pm.callEvent(new UpdateEvent());
		world.setTime((long) (world.getTime() + 20 *
				(600f /
						game.getConfig().getTimerValue(
								"werewolf.menu.timers.day_duration") - 1)));

		game.getScore().addTimer();

		game.getMain().getRegisterManager().getTimersRegister()
				.forEach(timerRegister -> {

					if (timerRegister.getPredicate().test(game)) {
						if (game.getConfig().getTimerValue(timerRegister.getKey()) == 0) {
							timerRegister.getConsumer().accept(game);
						}
						game.getConfig().decreaseTimer(timerRegister.getKey());
					}
				});

	}

}


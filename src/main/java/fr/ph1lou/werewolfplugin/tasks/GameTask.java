package fr.ph1lou.werewolfplugin.tasks;


import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfplugin.RegisterManager;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.save.Configuration;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.enums.StateGame;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;


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


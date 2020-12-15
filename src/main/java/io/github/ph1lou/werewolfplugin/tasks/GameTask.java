package io.github.ph1lou.werewolfplugin.tasks;


import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
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
		WorldBorder wb = world.getWorldBorder();
		PluginManager pm = Bukkit.getPluginManager();
		pm.callEvent(new UpdateEvent());
		world.setTime((long) (world.getTime() + 20 *
				(600f /
						game.getConfig().getTimerValue(
								"werewolf.menu.timers.day_duration") - 1)));


		game.getScore().addTimer();

		if (game.getConfig().getTimerValue(TimersBase.INVULNERABILITY.getKey()) == 0) {
			pm.callEvent(new InvulnerabilityEvent());
		}
		game.getConfig().decreaseTimer(TimersBase.INVULNERABILITY.getKey());

		if (game.getConfig().getTimerValue(TimersBase.ROLE_DURATION.getKey()) == 0) {

			if (game.getConfig().isTrollSV()) {
				pm.callEvent(new TrollEvent());
			} else {
				pm.callEvent(new RepartitionEvent());
			}
		}
		game.getConfig().decreaseTimer(TimersBase.ROLE_DURATION.getKey());

		if (game.getConfig().getTimerValue(TimersBase.PVP.getKey()) == 0) {
			pm.callEvent(new PVPEvent());
		}
		game.getConfig().decreaseTimer(TimersBase.PVP.getKey());

		if (game.getConfig().getTimerValue(TimersBase.ROLE_DURATION.getKey()) < 0 && !game.getConfig().isTrollSV()) {

			if (game.getConfig().getTimerValue(TimersBase.MODEL_DURATION.getKey()) == 0) {
				pm.callEvent(new AutoModelEvent());
			}
			game.getConfig().decreaseTimer(TimersBase.MODEL_DURATION.getKey());

			if (game.getConfig().getTimerValue(TimersBase.LOVER_DURATION.getKey()) == 0) {
				pm.callEvent(new LoversRepartitionEvent());

			}
			game.getConfig().decreaseTimer(TimersBase.LOVER_DURATION.getKey());

			if (game.getConfig().getTimerValue(TimersBase.ANGEL_DURATION.getKey()) == 0) {
				pm.callEvent(new AutoAngelEvent());
			}
			game.getConfig().decreaseTimer(TimersBase.ANGEL_DURATION.getKey());

			if (game.getConfig().getTimerValue(TimersBase.WEREWOLF_LIST.getKey()) == 0) {
				pm.callEvent(new WereWolfListEvent());

			}
			game.getConfig().decreaseTimer(TimersBase.WEREWOLF_LIST.getKey());
		}

		if (game.getConfig().getTimerValue(TimersBase.BORDER_BEGIN.getKey()) == 0) {

			if (wb.getSize() != game.getConfig().getBorderMin()) {
				pm.callEvent(new BorderStartEvent());
			}
		} else if (game.getConfig().getTimerValue(TimersBase.BORDER_BEGIN.getKey()) < 0) {

			if (game.getConfig().getBorderMax() != game.getConfig().getBorderMin()) {
				wb.setSize(game.getConfig().getBorderMin(), (long) ((long) Math.abs(wb.getSize() - game.getConfig().getBorderMin()) / game.getConfig().getBorderSpeed()));
				game.getConfig().setBorderMax((int) (wb.getSize()));
			}

		}

		game.getConfig().decreaseTimer(TimersBase.BORDER_BEGIN.getKey());

		if (game.getConfig().getTimerValue(TimersBase.DIGGING.getKey()) == 0) {
			pm.callEvent(new DiggingEndEvent());

		}
		game.getConfig().decreaseTimer(TimersBase.DIGGING.getKey());

		game.getConfig().decreaseTimer(TimersBase.VOTE_BEGIN.getKey());
	}

}


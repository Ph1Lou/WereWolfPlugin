package io.github.ph1lou.werewolfplugin.tasks;


import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.enumlg.TimersBase;
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
						game.getConfig().getTimerValues().get(
								"werewolf.menu.timers.day_duration") - 1)));

		game.getLoversManage().detectionAmnesiacLover();
		game.getScore().addTimer();

		if (game.getConfig().getTimerValues().get(TimersBase.INVULNERABILITY.getKey()) == 0) {
			pm.callEvent(new InvulnerabilityEvent());
		}
		game.getConfig().getTimerValues().put(TimersBase.INVULNERABILITY.getKey(), game.getConfig().getTimerValues().get(TimersBase.INVULNERABILITY.getKey()) - 1);

		if (game.getConfig().getTimerValues().get(TimersBase.ROLE_DURATION.getKey()) == 0) {

			if (game.getConfig().isTrollSV()) {
				pm.callEvent(new TrollEvent());
			} else {
				pm.callEvent(new RepartitionEvent());
			}
		}
		game.getConfig().getTimerValues().put(TimersBase.ROLE_DURATION.getKey(), game.getConfig().getTimerValues().get(TimersBase.ROLE_DURATION.getKey()) - 1);

		if (game.getConfig().getTimerValues().get(TimersBase.PVP.getKey()) == 0) {
			pm.callEvent(new PVPEvent());
		}
		game.getConfig().getTimerValues().put(TimersBase.PVP.getKey(), game.getConfig().getTimerValues().get(TimersBase.PVP.getKey()) - 1);

		if (game.getConfig().getTimerValues().get(TimersBase.ROLE_DURATION.getKey()) < 0 && !game.getConfig().isTrollSV()) {

			if (game.getConfig().getTimerValues().get(TimersBase.MODEL_DURATION.getKey()) == 0) {
				pm.callEvent(new AutoModelEvent());
			}
			game.getConfig().getTimerValues().put(TimersBase.MODEL_DURATION.getKey(), game.getConfig().getTimerValues().get(TimersBase.MODEL_DURATION.getKey()) - 1);

			if (game.getConfig().getTimerValues().get(TimersBase.LOVER_DURATION.getKey()) == 0) {
				pm.callEvent(new LoversRepartitionEvent());

			}
			game.getConfig().getTimerValues().put(TimersBase.LOVER_DURATION.getKey(), game.getConfig().getTimerValues().get(TimersBase.LOVER_DURATION.getKey()) - 1);

			if (game.getConfig().getTimerValues().get(TimersBase.ANGEL_DURATION.getKey()) == 0) {
				pm.callEvent(new AutoAngelEvent());
			}
			game.getConfig().getTimerValues().put(TimersBase.ANGEL_DURATION.getKey(), game.getConfig().getTimerValues().get(TimersBase.ANGEL_DURATION.getKey()) - 1);

			if (game.getConfig().getTimerValues().get(TimersBase.WEREWOLF_LIST.getKey()) == 0) {
				pm.callEvent(new WereWolfListEvent());

			}
			game.getConfig().getTimerValues().put(TimersBase.WEREWOLF_LIST.getKey(), game.getConfig().getTimerValues().get(TimersBase.WEREWOLF_LIST.getKey()) - 1);
		}

		if (game.getConfig().getTimerValues().get(TimersBase.BORDER_BEGIN.getKey()) == 0) {

			if (wb.getSize() != game.getConfig().getBorderMin()) {
				pm.callEvent(new BorderStartEvent());
			}
		} else if (game.getConfig().getTimerValues().get(TimersBase.BORDER_BEGIN.getKey()) < 0) {

			if (game.getConfig().getBorderMax() != game.getConfig().getBorderMin()) {
				wb.setSize(game.getConfig().getBorderMin(), (long) Math.abs(wb.getSize() - game.getConfig().getBorderMin()) * game.getConfig().getTimerValues().get(TimersBase.BORDER_DURATION.getKey()) / 100);
				game.getConfig().setBorderMax((int) (wb.getSize()));
			}

		}

		game.getConfig().getTimerValues().put(TimersBase.BORDER_BEGIN.getKey(), game.getConfig().getTimerValues().get(TimersBase.BORDER_BEGIN.getKey()) - 1);

		if (game.getConfig().getTimerValues().get(TimersBase.DIGGING.getKey()) == 0) {
			pm.callEvent(new DiggingEndEvent());

		}
		game.getConfig().getTimerValues().put(TimersBase.DIGGING.getKey(), game.getConfig().getTimerValues().get(TimersBase.DIGGING.getKey()) - 1);


		game.getConfig().getTimerValues().put(TimersBase.VOTE_BEGIN.getKey(), game.getConfig().getTimerValues().get(TimersBase.VOTE_BEGIN.getKey()) - 1);

	}

}


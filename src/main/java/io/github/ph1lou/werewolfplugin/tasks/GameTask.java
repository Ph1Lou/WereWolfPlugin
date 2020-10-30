package io.github.ph1lou.werewolfplugin.tasks;


import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.enumlg.Timers;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;


public class GameTask extends BukkitRunnable {

	private final GameManager game;
	int counter = 0;

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
		counter++;
		world.setTime((long) (world.getTime() + 5 * (600f / game.getConfig().getTimerValues().get(Timers.DAY_DURATION.getKey()) - 1)));


		for (Player p : Bukkit.getOnlinePlayers()) {
			game.getScore().actionBar(p);
		}
		if (counter % 4 != 0) return;


		WorldBorder wb = world.getWorldBorder();
		PluginManager pm = Bukkit.getPluginManager();
		pm.callEvent(new UpdateEvent());
		game.getLoversManage().detectionAmnesiacLover();
		game.getScore().addTimer();

		if (game.getConfig().getTimerValues().get(Timers.INVULNERABILITY.getKey()) == 0) {
			pm.callEvent(new InvulnerabilityEvent());
		}
		game.getConfig().getTimerValues().put(Timers.INVULNERABILITY.getKey(), game.getConfig().getTimerValues().get(Timers.INVULNERABILITY.getKey()) - 1);

		if (game.getConfig().getTimerValues().get(Timers.ROLE_DURATION.getKey()) == 0) {

			if (game.getConfig().isTrollSV()) {
				pm.callEvent(new TrollEvent());
			} else {
				pm.callEvent(new RepartitionEvent());
			}
		}
		game.getConfig().getTimerValues().put(Timers.ROLE_DURATION.getKey(), game.getConfig().getTimerValues().get(Timers.ROLE_DURATION.getKey()) - 1);

		if (game.getConfig().getTimerValues().get(Timers.PVP.getKey()) == 0) {
			pm.callEvent(new PVPEvent());
		}
		game.getConfig().getTimerValues().put(Timers.PVP.getKey(), game.getConfig().getTimerValues().get(Timers.PVP.getKey()) - 1);

		if (game.getConfig().getTimerValues().get(Timers.ROLE_DURATION.getKey()) < 0 && !game.getConfig().isTrollSV()) {

			if (game.getConfig().getTimerValues().get(Timers.MODEL_DURATION.getKey()) == 0) {
				pm.callEvent(new AutoModelEvent());
			}
			game.getConfig().getTimerValues().put(Timers.MODEL_DURATION.getKey(), game.getConfig().getTimerValues().get(Timers.MODEL_DURATION.getKey()) - 1);

			if (game.getConfig().getTimerValues().get(Timers.LOVER_DURATION.getKey()) == 0) {
				pm.callEvent(new LoversRepartitionEvent());

			}
			game.getConfig().getTimerValues().put(Timers.LOVER_DURATION.getKey(), game.getConfig().getTimerValues().get(Timers.LOVER_DURATION.getKey()) - 1);

			if (game.getConfig().getTimerValues().get(Timers.ANGEL_DURATION.getKey()) == 0) {
				pm.callEvent(new AutoAngelEvent());
			}
			game.getConfig().getTimerValues().put(Timers.ANGEL_DURATION.getKey(), game.getConfig().getTimerValues().get(Timers.ANGEL_DURATION.getKey()) - 1);

			if (game.getConfig().getTimerValues().get(Timers.WEREWOLF_LIST.getKey()) == 0) {
				pm.callEvent(new WereWolfListEvent());

			}
			game.getConfig().getTimerValues().put(Timers.WEREWOLF_LIST.getKey(), game.getConfig().getTimerValues().get(Timers.WEREWOLF_LIST.getKey()) - 1);
		}

		if (game.getConfig().getTimerValues().get(Timers.BORDER_BEGIN.getKey()) == 0) {

			if (wb.getSize() != game.getConfig().getBorderMin()) {
				pm.callEvent(new BorderStartEvent());
			}
		} else if (game.getConfig().getTimerValues().get(Timers.BORDER_BEGIN.getKey()) < 0) {

			if (game.getConfig().getBorderMax() != game.getConfig().getBorderMin()) {
				wb.setSize(game.getConfig().getBorderMin(), (long) Math.abs(wb.getSize() - game.getConfig().getBorderMin()) * game.getConfig().getTimerValues().get(Timers.BORDER_DURATION.getKey()) / 100);
				game.getConfig().setBorderMax((int) (wb.getSize()));
			}

		}

		game.getConfig().getTimerValues().put(Timers.BORDER_BEGIN.getKey(), game.getConfig().getTimerValues().get(Timers.BORDER_BEGIN.getKey()) - 1);

		if (game.getConfig().getTimerValues().get(Timers.DIGGING.getKey()) == 0) {
			pm.callEvent(new DiggingEndEvent());

		}
		game.getConfig().getTimerValues().put(Timers.DIGGING.getKey(), game.getConfig().getTimerValues().get(Timers.DIGGING.getKey()) - 1);


		game.getConfig().getTimerValues().put(Timers.VOTE_BEGIN.getKey(), game.getConfig().getTimerValues().get(Timers.VOTE_BEGIN.getKey()) - 1);

	}

}


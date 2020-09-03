package io.github.ph1lou.werewolfplugin.tasks;


import io.github.ph1lou.werewolfapi.enumlg.StateLG;
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

		if (game.isState(StateLG.END)) {
			cancel();
			return;
		}

		counter++;
		for (Player p : Bukkit.getOnlinePlayers()) {
			game.getScore().actionBar(p);
		}
		if (counter % 4 != 0) return;

		World world = game.getMapManager().getWorld();
		WorldBorder wb = world.getWorldBorder();
		PluginManager pm = Bukkit.getPluginManager();
		pm.callEvent(new UpdateEvent());
		game.getLoversManage().detectionAmnesiacLover();
		world.setTime((long) (world.getTime() + 20 * (600f / game.getConfig().getTimerValues().get("werewolf.menu.timers.day_duration") - 1)));
		game.getScore().addTimer();

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.invulnerability") == 0) {
			pm.callEvent(new InvulnerabilityEvent());
		}
		game.getConfig().getTimerValues().put("werewolf.menu.timers.invulnerability", game.getConfig().getTimerValues().get("werewolf.menu.timers.invulnerability") - 1);

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.role_duration") == 0) {

			if (game.getConfig().isTrollSV()) {
				pm.callEvent(new TrollEvent());
			} else {
				pm.callEvent(new RepartitionEvent());
			}
		}
		game.getConfig().getTimerValues().put("werewolf.menu.timers.role_duration", game.getConfig().getTimerValues().get("werewolf.menu.timers.role_duration") - 1);

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.pvp") == 0) {
			pm.callEvent(new PVPEvent());
		}
		game.getConfig().getTimerValues().put("werewolf.menu.timers.pvp", game.getConfig().getTimerValues().get("werewolf.menu.timers.pvp") - 1);

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.role_duration") < 0 && !game.getConfig().isTrollSV()) {

			if (game.getConfig().getTimerValues().get("werewolf.menu.timers.model_duration") == 0) {
				pm.callEvent(new AutoModelEvent());
			}
			game.getConfig().getTimerValues().put("werewolf.menu.timers.model_duration", game.getConfig().getTimerValues().get("werewolf.menu.timers.model_duration") - 1);

			if (game.getConfig().getTimerValues().get("werewolf.menu.timers.lover_duration") == 0) {
				pm.callEvent(new LoversRepartitionEvent());

			}
			game.getConfig().getTimerValues().put("werewolf.menu.timers.lover_duration", game.getConfig().getTimerValues().get("werewolf.menu.timers.lover_duration") - 1);

			if (game.getConfig().getTimerValues().get("werewolf.menu.timers.angel_duration") == 0) {
				pm.callEvent(new AutoAngelEvent());
			}
			game.getConfig().getTimerValues().put("werewolf.menu.timers.angel_duration", game.getConfig().getTimerValues().get("werewolf.menu.timers.angel_duration") - 1);

			if (game.getConfig().getTimerValues().get("werewolf.menu.timers.werewolf_list") == 0) {
				pm.callEvent(new WereWolfListEvent());

			}
			game.getConfig().getTimerValues().put("werewolf.menu.timers.werewolf_list", game.getConfig().getTimerValues().get("werewolf.menu.timers.werewolf_list") - 1);
		}

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.border_begin") == 0) {

			if (wb.getSize() != game.getConfig().getBorderMin()) {
				pm.callEvent(new BorderStartEvent());
			}
		} else if (game.getConfig().getTimerValues().get("werewolf.menu.timers.border_begin") < 0) {
			game.getConfig().setBorderMax((int) (wb.getSize()));
			if(game.getConfig().getBorderMax()==game.getConfig().getBorderMin()) {
				pm.callEvent(new BorderStopEvent());
			}
			else wb.setSize(game.getConfig().getBorderMin(), (long) Math.abs(wb.getSize() - game.getConfig().getBorderMin())* game.getConfig().getTimerValues().get("werewolf.menu.timers.border_duration") / 100);
		}
		game.getConfig().getTimerValues().put("werewolf.menu.timers.border_begin", game.getConfig().getTimerValues().get("werewolf.menu.timers.border_begin") - 1);

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.digging_end") == 0) {
			pm.callEvent(new DiggingEndEvent());

		}
		game.getConfig().getTimerValues().put("werewolf.menu.timers.digging_end", game.getConfig().getTimerValues().get("werewolf.menu.timers.digging_end") - 1);


		game.getConfig().getTimerValues().put("werewolf.menu.timers.vote_begin", game.getConfig().getTimerValues().get("werewolf.menu.timers.vote_begin") - 1);

	}

}


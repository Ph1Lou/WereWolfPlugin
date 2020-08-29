package io.github.ph1lou.werewolfplugin.tasks;


import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
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

		World world = game.getWorld();
		WorldBorder wb = world.getWorldBorder();
		long time = world.getTime();
		Bukkit.getPluginManager().callEvent(new UpdateEvent());
		game.getScore().updateBoard();
		game.getLoversManage().detectionAmnesiacLover();
		game.getRoleManage().brotherLife();

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.invulnerability") == 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(game.translate("werewolf.announcement.invulnerability"));
				Sounds.GLASS.play(p);
			}

		}
		game.getConfig().getTimerValues().put("werewolf.menu.timers.invulnerability", game.getConfig().getTimerValues().get("werewolf.menu.timers.invulnerability") - 1);

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.role_duration") == 0) {

			game.setState(StateLG.GAME);
			game.getConfig().getConfigValues().put("werewolf.menu.global.chat", false);

			if (game.getConfig().isTrollSV()) {
				Bukkit.getPluginManager().callEvent(new TrollEvent());
			} else {
				for (Player p : Bukkit.getOnlinePlayers()) {
					Sounds.EXPLODE.play(p);
				}
				game.getRoleManage().repartitionRolesLG();
			}
		}
		game.getConfig().getTimerValues().put("werewolf.menu.timers.role_duration", game.getConfig().getTimerValues().get("werewolf.menu.timers.role_duration") - 1);

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.pvp") == 0) {
			Bukkit.getPluginManager().callEvent(new PVPEvent());
		}
		game.getConfig().getTimerValues().put("werewolf.menu.timers.pvp", game.getConfig().getTimerValues().get("werewolf.menu.timers.pvp") - 1);

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.role_duration") < 0 && !game.getConfig().isTrollSV()) {

			if (game.getConfig().getTimerValues().get("werewolf.menu.timers.model_duration") == 0) {
				Bukkit.getPluginManager().callEvent(new AutoModelEvent());
			}
			game.getConfig().getTimerValues().put("werewolf.menu.timers.model_duration", game.getConfig().getTimerValues().get("werewolf.menu.timers.model_duration") - 1);

			if (game.getConfig().getTimerValues().get("werewolf.menu.timers.lover_duration") == 0) {
				Bukkit.getPluginManager().callEvent(new LoversRepartitionEvent());

			}
			game.getConfig().getTimerValues().put("werewolf.menu.timers.lover_duration", game.getConfig().getTimerValues().get("werewolf.menu.timers.lover_duration") - 1);

			if (game.getConfig().getTimerValues().get("werewolf.menu.timers.angel_duration") == 0) {
				Bukkit.getPluginManager().callEvent(new AutoAngelEvent());
			}
			game.getConfig().getTimerValues().put("werewolf.menu.timers.angel_duration", game.getConfig().getTimerValues().get("werewolf.menu.timers.angel_duration") - 1);

			if (game.getConfig().getTimerValues().get("werewolf.menu.timers.werewolf_list") == 0) {
				Bukkit.getPluginManager().callEvent(new WereWolfListEvent());

			}
			game.getConfig().getTimerValues().put("werewolf.menu.timers.werewolf_list", game.getConfig().getTimerValues().get("werewolf.menu.timers.werewolf_list") - 1);
		}


		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.border_begin") == 0) {

			if (wb.getSize() != game.getConfig().getBorderMin()) {
				Bukkit.getPluginManager().callEvent(new BorderStartEvent());
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(game.translate("werewolf.announcement.border"));
					Sounds.FIREWORK_LAUNCH.play(p);
				}
			}
		} else if (game.getConfig().getTimerValues().get("werewolf.menu.timers.border_begin") < 0) {
			game.getConfig().setBorderMax((int) (wb.getSize()));
			if(game.getConfig().getBorderMax()==game.getConfig().getBorderMin()){
				Bukkit.getPluginManager().callEvent(new BorderStopEvent());
			}
			else wb.setSize(game.getConfig().getBorderMin(), (long) Math.abs(wb.getSize() - game.getConfig().getBorderMin())* game.getConfig().getTimerValues().get("werewolf.menu.timers.border_duration") / 100);
		}
		game.getConfig().getTimerValues().put("werewolf.menu.timers.border_begin", game.getConfig().getTimerValues().get("werewolf.menu.timers.border_begin") - 1);

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.digging_end") == 0) {
			Bukkit.getPluginManager().callEvent(new DiggingEndEvent());

		}
		game.getConfig().getTimerValues().put("werewolf.menu.timers.digging_end", game.getConfig().getTimerValues().get("werewolf.menu.timers.digging_end") - 1);


		game.getConfig().getTimerValues().put("werewolf.menu.timers.vote_begin", game.getConfig().getTimerValues().get("werewolf.menu.timers.vote_begin") - 1);


		world.setTime((long) (time + 20 * (600f / game.getConfig().getTimerValues().get("werewolf.menu.timers.day_duration") - 1)));

		game.getScore().addTimer();
	}

}


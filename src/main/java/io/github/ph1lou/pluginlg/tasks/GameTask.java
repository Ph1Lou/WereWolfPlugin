package io.github.ph1lou.pluginlg.tasks;


import io.github.ph1lou.pluginlg.events.AutoAngelEvent;
import io.github.ph1lou.pluginlg.events.AutoModelEvent;
import io.github.ph1lou.pluginlg.events.UpdateEvent;
import io.github.ph1lou.pluginlg.events.WereWolfListEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
			game.score.actionBar(p);
		}
		if (counter % 4 != 0) return;

		World world = game.getWorld();
		WorldBorder wb = world.getWorldBorder();
		long time = world.getTime();
		Bukkit.getPluginManager().callEvent(new UpdateEvent(game.getGameUUID()));
		game.optionlg.updateSelectionTimer();
		game.optionlg.updateSelectionBorder();
		game.score.updateBoard();
		game.loversManage.detectionAmnesiacLover();
		game.roleManage.brotherLife();

		if (game.config.getTimerValues().get(TimerLG.INVULNERABILITY) == 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(game.translate("werewolf.announcement.invulnerability"));
				p.playSound(p.getLocation(), Sound.GLASS, 1, 20);
			}

		}
		game.config.getTimerValues().put(TimerLG.INVULNERABILITY, game.config.getTimerValues().get(TimerLG.INVULNERABILITY) - 1);

		if (game.config.getTimerValues().get(TimerLG.ROLE_DURATION) == 0) {

			game.setState(StateLG.GAME);

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (game.config.isTrollSV() && game.playerLG.containsKey(p.getUniqueId())) {
					p.playSound(p.getLocation(), Sound.PORTAL_TRIGGER, 1, 20);
					p.sendMessage(game.translate("werewolf.announcement.troll"));
				} else p.playSound(p.getLocation(), Sound.EXPLODE, 1, 20);
			}
			game.config.setTrollSV(false);
			game.roleManage.repartitionRolesLG();
		}

		if (game.config.getTimerValues().get(TimerLG.ROLE_DURATION) - 120 == 0 && game.config.isTrollSV()) {

			game.setState(StateLG.GAME);

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (game.playerLG.containsKey(p.getUniqueId())) {
					p.sendMessage(game.translate("werewolf.role.villager.description"));
					p.playSound(p.getLocation(), Sound.EXPLODE, 1, 20);
				}
			}
		}
		game.config.getTimerValues().put(TimerLG.ROLE_DURATION, game.config.getTimerValues().get(TimerLG.ROLE_DURATION) - 1);

		if (game.config.getTimerValues().get(TimerLG.PVP) == 0) {
			world.setPVP(true);
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(game.translate("werewolf.announcement.pvp"));
				p.playSound(p.getLocation(), Sound.DONKEY_ANGRY, 1, 20);
			}
		}
		game.config.getTimerValues().put(TimerLG.PVP, game.config.getTimerValues().get(TimerLG.PVP) - 1);

		if (game.config.getTimerValues().get(TimerLG.ROLE_DURATION) < 0) {

			if (game.config.getTimerValues().get(TimerLG.MODEL_DURATION) == 0) {
				Bukkit.getPluginManager().callEvent(new AutoModelEvent(game.getGameUUID()));
			}
			game.config.getTimerValues().put(TimerLG.MODEL_DURATION, game.config.getTimerValues().get(TimerLG.MODEL_DURATION) - 1);

			if (game.config.getTimerValues().get(TimerLG.LOVER_DURATION) == 0) {
				game.loversManage.autoLovers();
			}
			game.config.getTimerValues().put(TimerLG.LOVER_DURATION, game.config.getTimerValues().get(TimerLG.LOVER_DURATION) - 1);

			if (game.config.getTimerValues().get(TimerLG.ANGEL_DURATION) == 0) {
				Bukkit.getPluginManager().callEvent(new AutoAngelEvent(game.getGameUUID()));
			}
			game.config.getTimerValues().put(TimerLG.ANGEL_DURATION, game.config.getTimerValues().get(TimerLG.ANGEL_DURATION) - 1);

			if (game.config.getTimerValues().get(TimerLG.WEREWOLF_LIST) == 0) {
				Bukkit.getPluginManager().callEvent(new WereWolfListEvent(game.getGameUUID()));
			}
			game.config.getTimerValues().put(TimerLG.WEREWOLF_LIST, game.config.getTimerValues().get(TimerLG.WEREWOLF_LIST) - 1);
		}


		if (game.config.getTimerValues().get(TimerLG.BORDER_BEGIN) == 0) {

			if (wb.getSize() != game.config.getBorderMin()) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(game.translate("werewolf.announcement.border"));
					p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 1, 20);
				}
			}
		} else if (game.config.getTimerValues().get(TimerLG.BORDER_BEGIN) < 0) {
			game.config.setBorderMax((int) (wb.getSize()));
			wb.setSize(game.config.getBorderMin(), (long) (wb.getSize() - game.config.getBorderMin())* game.config.getTimerValues().get(TimerLG.BORDER_DURATION) / 100);
		}
		game.config.getTimerValues().put(TimerLG.BORDER_BEGIN, game.config.getTimerValues().get(TimerLG.BORDER_BEGIN) - 1);

		if (game.config.getTimerValues().get(TimerLG.DIGGING) == 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(game.translate("werewolf.announcement.mining"));
				p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 1, 20);
			}
		}
		game.config.getTimerValues().put(TimerLG.DIGGING, game.config.getTimerValues().get(TimerLG.DIGGING) - 1);


		game.config.getTimerValues().put(TimerLG.VOTE_BEGIN, game.config.getTimerValues().get(TimerLG.VOTE_BEGIN) - 1);


		world.setTime((long) (time + 20 * (600f / game.config.getTimerValues().get(TimerLG.DAY_DURATION) - 1)));

		game.score.addTimer();
	}

}


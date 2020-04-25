package io.github.ph1lou.pluginlg.tasks;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.*;
import io.github.ph1lou.pluginlg.game.GameManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class GameTask extends BukkitRunnable {

	private final MainLG main;
	private final GameManager game;
	int counter = 0;

	public GameTask(GameManager game, MainLG main) {
		this.game = game;
		this.main=main;
	}

	@Override
	public void run() {

		counter++;
		for (Player p : Bukkit.getOnlinePlayers()) {
			game.score.actionBar(p);
		}
		if (counter % 4 != 0) return;

		World world = game.getWorld();
		WorldBorder wb = world.getWorldBorder();
		long time = world.getTime();
		game.optionlg.updateSelectionTimer();
		game.optionlg.updateSelectionBorder();
		game.score.updateBoard();
		game.proximity.sister_proximity();
		game.proximity.renard_proximity();
		game.death_manage.deathTimer();
		game.roleManage.brotherLife();

		if (game.config.timerValues.get(TimerLG.INVULNERABILITY) == 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (game.playerLG.containsKey(p.getName())) {
					p.sendMessage(game.text.getText(117));
					p.playSound(p.getLocation(), Sound.GLASS, 1, 20);
				}
			}

		}
		game.config.timerValues.put(TimerLG.INVULNERABILITY, game.config.timerValues.get(TimerLG.INVULNERABILITY) - 1);

		if (game.config.timerValues.get(TimerLG.ROLE_DURATION) == 0) {

			game.setState(StateLG.LG);

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (game.config.isTrollSV() && game.playerLG.containsKey(p.getName())) {
					p.playSound(p.getLocation(), Sound.PORTAL_TRIGGER, 1, 20);
					p.sendMessage(game.text.getText(87));
				} else p.playSound(p.getLocation(), Sound.EXPLODE, 1, 20);
			}
			game.config.setTrollSV(false);
			game.roleManage.repartitionRolesLG();
		}

		if (game.config.timerValues.get(TimerLG.ROLE_DURATION) - 120 == 0 && game.config.isTrollSV()) {

			game.setState(StateLG.LG);

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (game.playerLG.containsKey(p.getName())) {
					p.sendMessage(game.text.description.get(RoleLG.VILLAGEOIS));
					p.playSound(p.getLocation(), Sound.EXPLODE, 1, 20);
				}
			}
		}
		game.config.timerValues.put(TimerLG.ROLE_DURATION, game.config.timerValues.get(TimerLG.ROLE_DURATION) - 1);

		if (game.config.timerValues.get(TimerLG.PVP) == 0) {
			world.setPVP(true);
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (game.playerLG.containsKey(p.getName())) {
					p.sendMessage(game.text.getText(6));
					p.playSound(p.getLocation(), Sound.DONKEY_ANGRY, 1, 20);
				}
			}
		}
		game.config.timerValues.put(TimerLG.PVP, game.config.timerValues.get(TimerLG.PVP) - 1);

		if (game.config.timerValues.get(TimerLG.ROLE_DURATION) < 0) {

			if (game.config.timerValues.get(TimerLG.MASTER_DURATION) == 0) {
				game.roleManage.auto_master();
			}
			game.config.timerValues.put(TimerLG.MASTER_DURATION, game.config.timerValues.get(TimerLG.MASTER_DURATION) - 1);

			if (game.config.timerValues.get(TimerLG.COUPLE_DURATION) == 0) {
				game.loversManage.autoLovers();
			}
			game.config.timerValues.put(TimerLG.COUPLE_DURATION, game.config.timerValues.get(TimerLG.COUPLE_DURATION) - 1);

			if (game.config.timerValues.get(TimerLG.ANGE_DURATION) == 0) {
				game.roleManage.auto_ange();
			}
			game.config.timerValues.put(TimerLG.ANGE_DURATION, game.config.timerValues.get(TimerLG.ANGE_DURATION) - 1);
		}

		if (game.config.timerValues.get(TimerLG.LG_LIST) == 0 && game.config.configValues.get(ToolLG.LG_LIST)) {
			game.roleManage.lgList();
		}
		game.config.timerValues.put(TimerLG.LG_LIST, game.config.timerValues.get(TimerLG.LG_LIST) - 1);

		if (game.config.timerValues.get(TimerLG.BORDER_BEGIN) == 0) {

			if (wb.getSize() != game.config.borderValues.get(BorderLG.BORDER_MIN)) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (game.playerLG.containsKey(p.getName())) {
						p.sendMessage(game.text.getText(7));
						p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 1, 20);
					}
				}
			}
		} else if (game.config.timerValues.get(TimerLG.BORDER_BEGIN) < 0) {
			game.config.borderValues.put(BorderLG.BORDER_MAX, (int) wb.getSize());
			wb.setSize(game.config.borderValues.get(BorderLG.BORDER_MIN), (long) (wb.getSize() - game.config.borderValues.get(BorderLG.BORDER_MIN)) * game.config.timerValues.get(TimerLG.BORDER_DURATION) / 100);
		}
		game.config.timerValues.put(TimerLG.BORDER_BEGIN, game.config.timerValues.get(TimerLG.BORDER_BEGIN) - 1);

		if (game.config.timerValues.get(TimerLG.DIGGING) == 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (game.playerLG.containsKey(p.getName())) {
					p.sendMessage(game.text.getText(8));
					p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 1, 20);
				}
			}
		}
		game.config.timerValues.put(TimerLG.DIGGING, game.config.timerValues.get(TimerLG.DIGGING) - 1);

		if (game.isDay(Day.NIGHT)) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (game.playerLG.containsKey(p.getName()) && game.playerLG.get(p.getName()).isState(State.LIVING) && !game.playerLG.get(p.getName()).hasPower() && (game.playerLG.get(p.getName()).isRole(RoleLG.LOUP_PERFIDE) || game.playerLG.get(p.getName()).isRole(RoleLG.PETITE_FILLE))) {
					for (Player p2 : Bukkit.getOnlinePlayers()) {
						if (game.playerLG.containsKey(p2.getName()) && game.playerLG.get(p2.getName()).isState(State.LIVING) && !game.playerLG.get(p2.getName()).hasPower() && !p.equals(p2) && (game.playerLG.get(p2.getName()).isRole(RoleLG.LOUP_PERFIDE) || game.playerLG.get(p2.getName()).isRole(RoleLG.PETITE_FILLE))) {

							if (game.playerLG.get(p2.getName()).isRole(RoleLG.LOUP_PERFIDE)) {
								p.playEffect(p2.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
							}
							if (game.playerLG.get(p2.getName()).isRole(RoleLG.PETITE_FILLE)) {
								p.playEffect(p2.getLocation(), Effect.STEP_SOUND, Material.LAPIS_BLOCK);
							}
						}
					}
				}
			}
		}
		game.config.timerValues.put(TimerLG.VOTE_BEGIN, game.config.timerValues.get(TimerLG.VOTE_BEGIN) - 1);

		if (game.score.getTimer() % (game.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == 0 && !game.isDay(Day.DAY)) {

			game.setDay(Day.DAY);
			if (game.config.configValues.get(ToolLG.VOTE) && game.score.getPlayerSize() < game.config.getPlayerRequiredVoteEnd()) {
				game.config.configValues.put(ToolLG.VOTE, false);
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (game.playerLG.containsKey(p.getName())) {
						p.sendMessage(game.text.getText(9));
					}
				}
			}
			game.cycle.day();
		}

		if (game.score.getTimer() % (game.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == game.config.timerValues.get(TimerLG.VOTE_DURATION)) {
			if (game.config.configValues.get(ToolLG.VOTE) && game.config.timerValues.get(TimerLG.VOTE_DURATION) + game.config.timerValues.get(TimerLG.VOTE_BEGIN) < 0) {
				game.cycle.preVoteResult();
			}
		}
		if (game.score.getTimer() % (game.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == game.config.timerValues.get(TimerLG.VOTE_DURATION) + game.config.timerValues.get(TimerLG.CITIZEN_DURATION)) {
			game.vote.showResultVote(game.vote.getResult());
		}
		if (game.score.getTimer() % (game.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == game.config.timerValues.get(TimerLG.POWER_DURATION)) {
			game.cycle.selectionEnd();
		}
		if (game.score.getTimer() % (game.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == game.config.timerValues.get(TimerLG.DAY_DURATION) * 2 - 30) {
			game.cycle.preDay();
		}

		if (game.score.getTimer() % (game.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == game.config.timerValues.get(TimerLG.DAY_DURATION) && !game.isDay(Day.NIGHT)) {
			game.setDay(Day.NIGHT);
			game.cycle.night();
		}

		world.setTime((long) (time + 20 * (600f / game.config.timerValues.get(TimerLG.DAY_DURATION) - 1)));

		if (game.isState(StateLG.FIN)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, game::deleteGame,1200);
			cancel();
		}

		game.score.addTimer();
	}

}


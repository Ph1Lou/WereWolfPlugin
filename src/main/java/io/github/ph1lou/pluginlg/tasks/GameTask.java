package io.github.ph1lou.pluginlg.tasks;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class GameTask extends BukkitRunnable {

	private final MainLG main;
	int counter = 0;

	public GameTask(MainLG main) {
		this.main = main;
	}

	@Override
	public void run() {

		counter++;
		for (Player p : Bukkit.getOnlinePlayers()) {
			main.score.actionBar(p);
		}
		if (counter % 4 != 0) return;

		World world = Bukkit.getWorld("world");
		WorldBorder wb = world.getWorldBorder();
		long time = world.getTime();
		main.optionlg.updateSelectionTimer();
		main.optionlg.updateSelectionBorder();
		main.score.updateBoard();
		main.proximity.sister_proximity();
		main.proximity.renard_proximity();
		main.death_manage.deathTimer();
		main.roleManage.brotherLife();

		if (main.config.timerValues.get(TimerLG.INVULNERABILITY) == 0) {
			Bukkit.broadcastMessage(main.text.getText(117));
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.GLASS, 1, 20);
			}
		}
		main.config.timerValues.put(TimerLG.INVULNERABILITY, main.config.timerValues.get(TimerLG.INVULNERABILITY) - 1);

		if (main.config.timerValues.get(TimerLG.ROLE_DURATION) == 0) {

			main.setState(StateLG.LG);

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (main.config.isTrollSV() && main.playerLG.containsKey(p.getName())) {
					p.playSound(p.getLocation(), Sound.PORTAL_TRIGGER, 1, 20);
					p.sendMessage(main.text.getText(87));
				} else p.playSound(p.getLocation(), Sound.EXPLODE, 1, 20);
			}
			main.config.setTrollSV(false);
			main.roleManage.repartitionRolesLG();
		}

		if (main.config.timerValues.get(TimerLG.ROLE_DURATION) - 120 == 0 && main.config.isTrollSV()) {

			main.setState(StateLG.LG);

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (main.playerLG.containsKey(p.getName())) {
					p.sendMessage(main.text.description.get(RoleLG.VILLAGEOIS));
					p.playSound(p.getLocation(), Sound.EXPLODE, 1, 20);
				}
			}
		}
		main.config.timerValues.put(TimerLG.ROLE_DURATION, main.config.timerValues.get(TimerLG.ROLE_DURATION) - 1);

		if (main.config.timerValues.get(TimerLG.PVP) == 0) {
			world.setPVP(true);
			Bukkit.broadcastMessage(main.text.getText(6));
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.DONKEY_ANGRY, 1, 20);
			}
		}
		main.config.timerValues.put(TimerLG.PVP, main.config.timerValues.get(TimerLG.PVP) - 1);

		if (main.config.timerValues.get(TimerLG.ROLE_DURATION) < 0) {

			if (main.config.timerValues.get(TimerLG.MASTER_DURATION) == 0) {
				main.roleManage.auto_master();
			}
			main.config.timerValues.put(TimerLG.MASTER_DURATION, main.config.timerValues.get(TimerLG.MASTER_DURATION) - 1);

			if (main.config.timerValues.get(TimerLG.COUPLE_DURATION) == 0) {
				main.loversManage.autoLovers();
			}
			main.config.timerValues.put(TimerLG.COUPLE_DURATION, main.config.timerValues.get(TimerLG.COUPLE_DURATION) - 1);

			if (main.config.timerValues.get(TimerLG.ANGE_DURATION) == 0) {
				main.roleManage.auto_ange();
			}
			main.config.timerValues.put(TimerLG.ANGE_DURATION, main.config.timerValues.get(TimerLG.ANGE_DURATION) - 1);
		}

		if (main.config.timerValues.get(TimerLG.LG_LIST) == 0 && main.config.configValues.get(ToolLG.LG_LIST)) {
			main.roleManage.lgList();
		}
		main.config.timerValues.put(TimerLG.LG_LIST, main.config.timerValues.get(TimerLG.LG_LIST) - 1);

		if (main.config.timerValues.get(TimerLG.BORDER_BEGIN) == 0) {

			if (wb.getSize() != main.config.borderValues.get(BorderLG.BORDER_MIN)) {
				Bukkit.broadcastMessage(main.text.getText(7));
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 1, 20);
				}
			}
		} else if (main.config.timerValues.get(TimerLG.BORDER_BEGIN) < 0) {
			main.config.borderValues.put(BorderLG.BORDER_MAX, (int) wb.getSize());
			wb.setSize(main.config.borderValues.get(BorderLG.BORDER_MIN), (long) (wb.getSize() - main.config.borderValues.get(BorderLG.BORDER_MIN)) * main.config.timerValues.get(TimerLG.BORDER_DURATION) / 100);
		}
		main.config.timerValues.put(TimerLG.BORDER_BEGIN, main.config.timerValues.get(TimerLG.BORDER_BEGIN) - 1);

		if (main.config.timerValues.get(TimerLG.DIGGING) == 0) {
			Bukkit.broadcastMessage(main.text.getText(8));
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 1, 20);
			}
		}
		main.config.timerValues.put(TimerLG.DIGGING, main.config.timerValues.get(TimerLG.DIGGING) - 1);

		if (main.isDay(Day.NIGHT)) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (main.playerLG.containsKey(p.getName()) && main.playerLG.get(p.getName()).isState(State.LIVING) && !main.playerLG.get(p.getName()).hasPower() && (main.playerLG.get(p.getName()).isRole(RoleLG.LOUP_PERFIDE) || main.playerLG.get(p.getName()).isRole(RoleLG.PETITE_FILLE))) {
					for (Player p2 : Bukkit.getOnlinePlayers()) {
						if (main.playerLG.containsKey(p2.getName()) && main.playerLG.get(p2.getName()).isState(State.LIVING) && !main.playerLG.get(p2.getName()).hasPower() && !p.equals(p2) && (main.playerLG.get(p2.getName()).isRole(RoleLG.LOUP_PERFIDE) || main.playerLG.get(p2.getName()).isRole(RoleLG.PETITE_FILLE))) {

							if (main.playerLG.get(p2.getName()).isRole(RoleLG.LOUP_PERFIDE)) {
								p.playEffect(p2.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
							}
							if (main.playerLG.get(p2.getName()).isRole(RoleLG.PETITE_FILLE)) {
								p.playEffect(p2.getLocation(), Effect.STEP_SOUND, Material.LAPIS_BLOCK);
							}
						}
					}
				}
			}
		}
		main.config.timerValues.put(TimerLG.VOTE_BEGIN, main.config.timerValues.get(TimerLG.VOTE_BEGIN) - 1);

		if (main.score.getTimer() % (main.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == 0 && !main.isDay(Day.DAY)) {

			main.setDay(Day.DAY);
			if (main.config.configValues.get(ToolLG.VOTE) && main.score.getPlayerSize() < main.config.getPlayerRequiredVoteEnd()) {
				main.config.configValues.put(ToolLG.VOTE, false);
				Bukkit.broadcastMessage(main.text.getText(9));
			}
			main.cycle.day();
		}

		if (main.score.getTimer() % (main.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == main.config.timerValues.get(TimerLG.VOTE_DURATION)) {
			if (main.config.configValues.get(ToolLG.VOTE) && main.config.timerValues.get(TimerLG.VOTE_DURATION) + main.config.timerValues.get(TimerLG.VOTE_BEGIN) < 0) {
				main.cycle.preVoteResult();
			}
		}
		if (main.score.getTimer() % (main.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == main.config.timerValues.get(TimerLG.VOTE_DURATION) + main.config.timerValues.get(TimerLG.CITIZEN_DURATION)) {
			main.vote.showResultVote(main.vote.getResult());
		}
		if (main.score.getTimer() % (main.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == main.config.timerValues.get(TimerLG.POWER_DURATION)) {
			main.cycle.selectionEnd();
		}
		if (main.score.getTimer() % (main.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == main.config.timerValues.get(TimerLG.DAY_DURATION) * 2 - 30) {
			main.cycle.preDay();
		}

		if (main.score.getTimer() % (main.config.timerValues.get(TimerLG.DAY_DURATION) * 2) == main.config.timerValues.get(TimerLG.DAY_DURATION) && !main.isDay(Day.NIGHT)) {
			main.setDay(Day.NIGHT);
			main.cycle.night();
		}

		world.setTime((long) (time + 20 * (600f / main.config.timerValues.get(TimerLG.DAY_DURATION) - 1)));

		if (main.isState(StateLG.FIN)) {
			cancel();
		}

		main.score.addTimer();
	}

}


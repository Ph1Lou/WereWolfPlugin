package io.github.ph1lou.pluginlg;


import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LoversManagement {

	public final List<List<String>> loversRange = new ArrayList<>();
	public final List<List<String>> cursedLoversRange = new ArrayList<>();
	private final MainLG main;

	public LoversManagement(MainLG main) {
		this.main = main;
	}


	public void autoCursedLovers() {

		List<String> cursedLovers = new ArrayList<>();

		for (String p : main.playerLG.keySet()) {
			if (main.playerLG.get(p).isState(State.LIVING) && main.playerLG.get(p).getLovers().isEmpty()) {
				cursedLovers.add(p);
			}
		}

		if (cursedLovers.size() < 2 && main.config.roleCount.get(RoleLG.COUPLE_MAUDIT) > 0) {
			Bukkit.broadcastMessage(main.text.getText(42));
			return;
		}

		int i = 0;

		while (cursedLovers.size() >= 2 && i < main.config.roleCount.get(RoleLG.COUPLE_MAUDIT)) {

			String j1 = cursedLovers.get((int) Math.floor(main.roleManage.r.nextFloat() * cursedLovers.size()));
			cursedLovers.remove(j1);
			String j2 = cursedLovers.get((int) Math.floor(main.roleManage.r.nextFloat() * cursedLovers.size()));
			cursedLovers.remove(j2);
			main.playerLG.get(j1).setCursedLover(j2);
			main.playerLG.get(j2).setCursedLover(j1);
			i++;
			cursedLoversRange.add(new ArrayList<>(Arrays.asList(j1, j2)));

			if (Bukkit.getPlayer(j1) != null) {
				announceCursedLovers(Bukkit.getPlayer(j1));
			} else main.playerLG.get(j1).setAnnounceCursedLoversAFK(true);
			if (Bukkit.getPlayer(j2) != null) {
				announceCursedLovers(Bukkit.getPlayer(j2));
			} else main.playerLG.get(j2).setAnnounceCursedLoversAFK(true);
		}
		main.config.roleCount.put(RoleLG.COUPLE_MAUDIT, cursedLoversRange.size());
	}


	public void announceCursedLovers(Player player) {

		for (ItemStack k : main.stufflg.role_stuff.get(RoleLG.COUPLE_MAUDIT)) {

			if (player.getInventory().firstEmpty() == -1) {
				player.getWorld().dropItem(player.getLocation(), k);
			} else {
				player.getInventory().addItem(k);
				player.updateInventory();
			}
		}
		player.setMaxHealth(player.getMaxHealth() + 2);
		player.sendMessage(String.format(main.text.description.get(RoleLG.COUPLE_MAUDIT), main.playerLG.get(player.getName()).getCursedLovers()));
		player.playSound(player.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
	}

	public void autoLovers() {

		List<String> lovers = new ArrayList<>();
		for (String p : main.playerLG.keySet()) {
			if (main.playerLG.get(p).isState(State.LIVING)) {
				lovers.add(p);
			}
		}
		if (lovers.size() < 2 && main.config.roleCount.get(RoleLG.CUPIDON) + main.config.roleCount.get(RoleLG.COUPLE) > 0) {
			Bukkit.broadcastMessage(main.text.getText(12));
			return;
		}

		Boolean polygamy = main.config.configValues.get(ToolLG.POLYGAMY);

		if (!polygamy && (main.config.roleCount.get(RoleLG.COUPLE) == 0 && main.config.roleCount.get(RoleLG.CUPIDON) * 2 >= main.score.getPlayerSize()) || (main.config.roleCount.get(RoleLG.COUPLE) != 0 && (main.config.roleCount.get(RoleLG.CUPIDON) + main.config.roleCount.get(RoleLG.COUPLE)) * 2 > main.score.getPlayerSize())) {
			polygamy = true;
			Bukkit.broadcastMessage(main.text.getText(192));
		}
		String j1;
		String j2;

		for (String playername : main.playerLG.keySet()) {

			if (main.playerLG.get(playername).isRole(RoleLG.CUPIDON)) {

				if (main.playerLG.get(playername).hasPower() || !main.playerLG.get(main.playerLG.get(playername).getAffectedPlayer().get(0)).isState(State.LIVING) || !main.playerLG.get(main.playerLG.get(playername).getAffectedPlayer().get(1)).isState(State.LIVING)) {

					if (lovers.contains(playername)) {
						lovers.remove(playername);
						j1 = lovers.get((int) Math.floor(main.roleManage.r.nextFloat() * lovers.size()));
						lovers.remove(j1);
						j2 = lovers.get((int) Math.floor(main.roleManage.r.nextFloat() * lovers.size()));
						lovers.add(j1);
						lovers.add(playername);
					} else {
						j1 = lovers.get((int) Math.floor(main.roleManage.r.nextFloat() * lovers.size()));
						lovers.remove(j1);
						j2 = lovers.get((int) Math.floor(main.roleManage.r.nextFloat() * lovers.size()));
						lovers.add(j1);
					}

					main.playerLG.get(playername).clearAffectedPlayer();
					main.playerLG.get(playername).addAffectedPlayer(j1);
					main.playerLG.get(playername).addAffectedPlayer(j2);
					main.playerLG.get(playername).setPower(false);
					if (Bukkit.getPlayer(playername) != null) {
						Bukkit.getPlayer(playername).sendMessage(String.format(main.text.powerHasBeenUse.get(RoleLG.CUPIDON), j1, j2));
					}
				} else {
					j1 = main.playerLG.get(playername).getAffectedPlayer().get(0);
					j2 = main.playerLG.get(playername).getAffectedPlayer().get(1);
				}
				if (!polygamy) {
					lovers.remove(j1);
					lovers.remove(j2);
				}
				if (!main.playerLG.get(j1).getLovers().contains(j2)) {
					main.playerLG.get(j1).addLover(j2);
				}

				if (!main.playerLG.get(j2).getLovers().contains(j1)) {
					main.playerLG.get(j2).addLover(j1);
				}
			}
		}
		for (int i = 0; i < main.config.roleCount.get(RoleLG.COUPLE); i++) {

			j1 = lovers.get((int) Math.floor(main.roleManage.r.nextFloat() * lovers.size()));
			lovers.remove(j1);
			j2 = lovers.get((int) Math.floor(main.roleManage.r.nextFloat() * lovers.size()));
			lovers.add(j1);

			if (!polygamy) {
				lovers.remove(j1);
				lovers.remove(j2);
			}
			if (!main.playerLG.get(j1).getLovers().contains(j2)) {
				main.playerLG.get(j1).addLover(j2);
			}

			if (!main.playerLG.get(j2).getLovers().contains(j1)) {
				main.playerLG.get(j2).addLover(j1);
			}
		}

		rangeLovers();
		reRangeLovers();
		if (!main.isState(StateLG.FIN)) {
			main.endlg.check_victory();
		}
		autoCursedLovers();
	}

	private void reRangeLovers() {

		for (List<String> strings : loversRange) {
			for (int j = 0; j < strings.size(); j++) {
				String p = strings.get(j);
				PlayerLG plg = main.playerLG.get(p);
				plg.clearLovers();
				for (String string : strings) {
					if (!string.equals(p)) {
						plg.addLover(string);
					}
				}
				if (Bukkit.getPlayer(p) != null) {
					announceLovers(Bukkit.getPlayer(p));
				}
			}
		}
	}

	public void announceLovers(Player player) {

		for (ItemStack k : main.stufflg.role_stuff.get(RoleLG.COUPLE)) {

			if (player.getInventory().firstEmpty() == -1) {
				player.getWorld().dropItem(player.getLocation(), k);
			} else {
				player.getInventory().addItem(k);
				player.updateInventory();
			}
		}

		StringBuilder couple = new StringBuilder();

		for (String c : main.playerLG.get(player.getName()).getLovers()) {
			couple.append(c).append(" ");
		}
		player.sendMessage(String.format(main.text.description.get(RoleLG.COUPLE), couple.toString()));
		player.playSound(player.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
	}


	private void rangeLovers() {

		List<String> lovers = new ArrayList<>();

		for (String playername : main.playerLG.keySet()) {
			if (!main.playerLG.get(playername).getLovers().isEmpty()) {
				lovers.add(playername);
			}
		}

		while (!lovers.isEmpty()) {

			List<String> linkCouple = new ArrayList<>();
			linkCouple.add(lovers.get(0));
			lovers.remove(0);

			for (int j = 0; j < linkCouple.size(); j++) {
				for (String playername : main.playerLG.keySet()) {
					if (main.playerLG.get(playername).getLovers().contains(linkCouple.get(j))) {
						if (!linkCouple.contains(playername)) {
							linkCouple.add(playername);
							lovers.remove(playername);
						}
					}
				}
			}
			loversRange.add(linkCouple);
		}
		main.config.roleCount.put(RoleLG.COUPLE, loversRange.size());
	}


	public void thiefLoversRange(String killername, String playername) {

		int cp = -1;
		int ck = -1;
		for (int i = 0; i < loversRange.size(); i++) {
			if (loversRange.get(i).contains(playername) && !loversRange.get(i).contains(killername)) {
				loversRange.get(i).remove(playername);
				loversRange.get(i).add(killername);
				cp = i;
			} else if (!loversRange.get(i).contains(playername) && loversRange.get(i).contains(killername)) {
				ck = i;
			}
		}
		if (cp != -1 && ck != -1) {
			loversRange.get(ck).remove(killername);
			loversRange.get(cp).addAll(loversRange.get(ck));
			loversRange.remove(ck);
		}
	}
}

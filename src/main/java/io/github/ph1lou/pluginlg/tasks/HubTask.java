package io.github.ph1lou.pluginlg.tasks;


import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;


public class HubTask extends BukkitRunnable {

	private final MainLG main;

	public HubTask(MainLG main) {
		this.main=main;
	}

	@Override
	public void run() {

		List<String> scoreboard = new ArrayList<>(main.defaultLanguage.getScoreBoard0());
		scoreboard.set(3,String.format(scoreboard.get(3), Bukkit.getOnlinePlayers().size()));
		scoreboard.set(5,String.format(scoreboard.get(5), main.listGames.size()));
		updateHubInventory();
		for (FastBoard board : main.boards.values()) {
			board.updateLines(scoreboard);
		}

	}


	private void updateHubInventory() {

		int[] SlotRedGlass = {0,1,2,6,7,8,9,10,16,17,18,26,27,35,36,37,43,44,45,46,47,51,52,53};
		int[] SlotBlackGlass = {3,4,5,11,15,19,25,28,34,38,42,48,49,50};
		int[] SlotHost = {21,22,23,30,31,32};
		for (int slotRedGlass : SlotRedGlass) {
			main.hubTool.setItem(slotRedGlass, changeMeta(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14), null, null));
		}
		for (int slotBlackGlass : SlotBlackGlass) {
			main.hubTool.setItem(slotBlackGlass, changeMeta(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), null, null));
		}
		for(int i=0;i<main.listGames.size();i++){

			GameManager game = ((GameManager) main.listGames.values().toArray()[i]);
			List<String> lore = new ArrayList<>();
			lore.add(game.score.getScoreboard1().get(3));
			lore.add(game.score.getScoreboard1().get(5));
			lore.add(String.format(game.text.getText(game.isWhiteList() ? 169 : 168),game.text.getText(63)));
			lore.add(main.defaultLanguage.getText(64));
			lore.add(main.defaultLanguage.getText(309 + game.getSpectatorMode()));
			main.hubTool.setItem(SlotHost[i], changeMeta(new ItemStack(Material.DIAMOND_BLOCK), game.getGameName(), lore));
		}
	}

	public ItemStack changeMeta(ItemStack item, String item_name, List<String> lore) {
		ItemMeta meta1 = item.getItemMeta();
		meta1.setDisplayName(item_name);
		meta1.setLore(lore);
		item.setItemMeta(meta1);
		return item;
	}

}


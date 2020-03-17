package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class OptionLG {

	private Inventory invTool;
	private Inventory invRole;
	private Inventory invTimer;
	private Inventory invBorder;
	private Inventory invScenario;
	private Inventory invStuff;
	private Inventory invSave;
	private Inventory invConfig;
	private final MainLG main;

	public OptionLG(MainLG main) {
		this.main=main;
	}

	public void initInv(){
		invTool = Bukkit.createInventory(null, 18,main.text.getText(175));
		invRole = Bukkit.createInventory(null, 45,main.text.getText(176));
		invTimer = Bukkit.createInventory(null, 27,main.text.getText(177));
		invConfig = Bukkit.createInventory(null, 36,main.text.getText(178));
		invScenario = Bukkit.createInventory(null, 36,main.text.getText(76));
		invBorder = Bukkit.createInventory(null, 18,main.text.getText(179));
		invSave = Bukkit.createInventory(null, 18,main.text.getText(180));
		invStuff = Bukkit.createInventory(null, 18,main.text.getText(77));
	}

	public void toolBar(Player player) {
		invTool.setItem(1, changeMeta(Material.BEACON,main.text.getText(176),1,null));
		invTool.setItem(3, changeMeta(Material.ANVIL,main.text.getText(177),1,null));
		invTool.setItem(5, changeMeta(Material.MAP,main.text.getText(178),1,null));
		invTool.setItem(7, changeMeta(Material.CHEST,main.text.getText(77),1,null));
		invTool.setItem(10, changeMeta(Material.GLASS,main.text.getText(179),1,null));
		invTool.setItem(12, changeMeta(Material.ARMOR_STAND,main.text.getText(180),1,null));
		invTool.setItem(14, changeMeta(Material.PUMPKIN,main.text.getText(76),1,null));
		player.openInventory(invTool);
	}

	public void chooseRole(Player player) {
		invRole.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		invRole.setItem(8, changeMeta(Material.BARRIER,main.text.getText(183),1,null));
		updateSelection();
		player.openInventory(invRole);
	}

	public void timerTool(Player player) {
		invTimer.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionTimer(findSelect(invTimer));
		player.openInventory(invTimer);
	}

	public void globalTool(Player player) {
		invConfig.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionTool();
		player.openInventory(invConfig);
	}

	public void globalScenario(Player player) {
		invScenario.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionScenario();
		player.openInventory(invScenario);
	}


	public void borderTool(Player player) {
		invBorder.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionBorder(findSelect(invBorder));
		player.openInventory(invBorder);
	}

	public void saveTool(Player player) {

		invSave.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionSave( findSelect(invSave)+9);
		player.openInventory(invSave);
	}

	public void stuffTool(Player player) {
		invStuff.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		invStuff.setItem(2, changeMeta(Material.EGG,main.text.getText(83),1,null));
		invStuff.setItem(4, changeMeta(Material.GOLD_SWORD,main.text.getText(84),1,null));
		invStuff.setItem(6, changeMeta(Material.JUKEBOX,main.text.getText(85),1,null));
		invStuff.setItem(10, changeMeta(Material.BARRIER,main.text.getText(86),1,null));
		invStuff.setItem(13, changeMeta(Material.CHEST,main.text.getText(182),1,null));
		invStuff.setItem(16, changeMeta(Material.ENDER_CHEST,main.text.getText(181),1,null));
		player.openInventory(invStuff);
	}

	public void updateSelectionSave( int j) {

		for(int i=1;i<9;i++) {

			if(i==j) {
				invSave.setItem(i, changeMeta(Material.FEATHER,String.format(main.text.getText(174),i),1,null));
			}
			else invSave.setItem(i, changeMeta(Material.PAPER,String.format(main.text.getText(174),i),1,null));
		}

		invSave.setItem(12, changeMeta(Material.EMERALD_BLOCK,String.format(main.text.getText(173),j),1,null));

		File file = new File(main.getDataFolder(), "save"+j+".json");

		if(file.exists()) {
			invSave.setItem(14, changeMeta(Material.BARRIER,String.format(main.text.getText(171),j),1,null));
			invSave.setItem(12, changeMeta(Material.BED,String.format(main.text.getText(172),j),1,null));
		}
		else invSave.setItem(14, null);
	}

	public void load() {
		int j= findSelect(invSave)+9;
		main.config.getConfig(main, j);
		main.stufflg.load(main, j);
		updateSelectionSave(j);
	}

	public void saveOrErase() {
		int i= findSelect(invSave)+9;
		File file = new File(main.getDataFolder(), "save"+i+".json");
		main.filelg.save(file, main.serialize.serialize(main.config));
		main.stufflg.save(main, i);
		updateSelectionSave( i);
	}



	public ItemStack changeMeta(Material m, String item_name, int i, List<String> lore) {
		ItemStack item1 = new ItemStack(m,i);
		ItemMeta meta1 = item1.getItemMeta();
		meta1.setDisplayName(item_name);
		meta1.setLore(lore);
		item1.setItemMeta(meta1);
		return item1;
	}

	private int findSelect(Inventory inv) {
		int i=0;
		boolean find = false;
		while (i<inv.getSize() && !find) {
			if(inv.getItem(i)!=null && inv.getItem(i).getType()==Material.FEATHER) {
				find=true;
			}
			else i++;
		}
		if(!find){
			i=9;
		}
		return (i-9);
	}

	public void selectMinus(int i) {

		if(!main.isState(StateLG.LG)) {
			int j = main.config.role_count.get(RoleLG.values()[i]);
			if(j>0) {
				if(!RoleLG.values()[i].equals(RoleLG.COUPLE)) {
					main.score.setRole(main.score.getRole()-1);
				}
				main.config.role_count.put(RoleLG.values()[i],j-1);
				main.score.updateBoard();
				updateSelection();
			}
		}
	}

	public void selectPlus(int i) {

		if(!main.isState(StateLG.LG)) {
			int j = main.config.role_count.get(RoleLG.values()[i]);
			main.config.role_count.put(RoleLG.values()[i],j+1);
			if(!RoleLG.values()[i].equals(RoleLG.COUPLE)) {
				main.score.setRole(main.score.getRole()+1);
			}
			main.score.updateBoard();
			updateSelection();
		}
	}

	public void selectMinusBorder() {
		int i = findSelect(invBorder) ;
		int j = main.config.border_value.get(BorderLG.values()[i]);
		if(j>=100) {
			main.config.border_value.put(BorderLG.values()[i],j-100);
			updateSelectionBorder(i);
		}
	}

	public void selectPlusBorder() {
		int i = findSelect(invBorder) ;
		int j = main.config.border_value.get(BorderLG.values()[i]);
		main.config.border_value.put(BorderLG.values()[i],j+100);
		updateSelectionBorder(i);
	}

	public void SelectMinusTimer(int v) {
		int i = findSelect(invTimer);
		int j = main.config.value.get(TimerLG.values()[i]);
		if(j>=v) {
			main.config.value.put(TimerLG.values()[i],j-v);
			updateSelectionTimer(i);
		}
	}

	public void selectPlusTimer(int v) {
		int i = findSelect(invTimer);
		main.config.value.put(TimerLG.values()[i],main.config.value.get(TimerLG.values()[i])+v);
		updateSelectionTimer(i);
	}



	public void updateSelection(){

		List<String> lore = Collections.singletonList(main.text.getText(87));
		for (int i=0;i<RoleLG.values().length;i++) {

			if (main.config.role_count.get(RoleLG.values()[i])>0) {
				invRole.setItem(9+i, changeMeta(Material.EMERALD,main.text.translaterole.get(RoleLG.values()[i]),main.config.role_count.get(RoleLG.values()[i]),lore));
			}
			else invRole.setItem(9+i, changeMeta(Material.REDSTONE,main.text.translaterole.get(RoleLG.values()[i]),1,lore));
		}
	}

	public void updateSelectionTimer(){
		updateSelectionTimer(findSelect(invTimer));
	}

	public void updateSelectionTimer(int j){

		String c= main.score.conversion(main.config.value.get(TimerLG.values()[j]));

		invTimer.setItem(1, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"-10m",c),1,null));
		invTimer.setItem(2, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"-1m",c),1,null));
		invTimer.setItem(3, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"-10s",c),1,null));
		invTimer.setItem(4, changeMeta(Material.BEACON,String.format(main.text.translatetimer.get(TimerLG.values()[j]),c),1,null));
		invTimer.setItem(5, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"+10s",c),1,null));
		invTimer.setItem(6, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"+1m",c),1,null));
		invTimer.setItem(7, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"+10m",c),1,null));

		for (int i=0;i<TimerLG.values().length;i++) {
			if(i==j) {
				invTimer.setItem(9+i, changeMeta(Material.FEATHER,String.format(main.text.translatetimer.get(TimerLG.values()[i]),c),1,null));
			}
			else invTimer.setItem(9+i, changeMeta(Material.ANVIL,String.format(main.text.translatetimer.get(TimerLG.values()[i]),main.score.conversion(main.config.value.get(TimerLG.values()[i]))),1,null));
		}
	}

	public void updateSelectionBorder(){
		updateSelectionBorder(findSelect(invBorder));
	}

	public void updateSelectionBorder(int j) {
		invBorder.setItem(3, changeMeta(Material.STONE_BUTTON,"- (§3"+main.config.border_value.get(BorderLG.values()[j])+"§r)",1,null));
		invBorder.setItem(4, changeMeta(Material.BEACON,String.format(main.text.translatebordure.get(BorderLG.values()[j]),main.config.border_value.get(BorderLG.values()[j])),1,null));
		invBorder.setItem(5, changeMeta(Material.STONE_BUTTON,"+ (§3"+main.config.border_value.get(BorderLG.values()[j])+"§r)",1,null));
		for (int i = 0; i< BorderLG.values().length; i++) {
			if(i==j) {
				invBorder.setItem(9+i, changeMeta(Material.FEATHER,String.format(main.text.translatebordure.get(BorderLG.values()[i]),main.config.border_value.get(BorderLG.values()[i])),1,null));
			}
			else invBorder.setItem(9+i, changeMeta(Material.GLASS,String.format(main.text.translatebordure.get(BorderLG.values()[i]),main.config.border_value.get(BorderLG.values()[i])),1,null));
		}
	}

	public void updateSelectionTool(){
		for (int i = 0; i< ToolLG.values().length; i++) {
			if (main.config.tool_switch.get(ToolLG.values()[i])) {
				invConfig.setItem(9+i, changeMeta(Material.EMERALD,String.format(main.text.getText(168),main.text.translatetool.get(ToolLG.values()[i])),1,null));
			}
			else invConfig.setItem(9+i, changeMeta(Material.REDSTONE,String.format(main.text.getText(169),main.text.translatetool.get(ToolLG.values()[i])),1,null));
		}
	}

	public void updateSelectionScenario() {
		for (int i = 0; i< ScenarioLG.values().length; i++) {
			if (main.config.scenario.get(ScenarioLG.values()[i])) {
				invScenario.setItem(9+i, changeMeta(Material.EMERALD,String.format(main.text.getText(168),main.text.translatescenario.get(ScenarioLG.values()[i])),1,null));
			}
			else invScenario.setItem(9+i, changeMeta(Material.REDSTONE,String.format(main.text.getText(169),main.text.translatescenario.get(ScenarioLG.values()[i])),1,null));
		}
		updateNameTag();
	}

	public void updateNameTag() {

		for(String playername : main.playerlg.keySet()) {

			Scoreboard board = main.playerlg.get(playername).getScoreBoard();

			if(!board.equals(main.board)){

				for(String players : main.playerlg.keySet()) {

					if(board.getTeam(players)==null){
						board.registerNewTeam(players);
						board.getTeam(players).addEntry(players);
					}
					if(main.config.scenario.get(ScenarioLG.NO_NAME_TAG)){
						board.getTeam(players).setNameTagVisibility(NameTagVisibility.NEVER);
					}
					else {
						if(!main.playerlg.get(players).hasPower() && (main.playerlg.get(players).isRole(RoleLG.LOUP_PERFIDE) || main.playerlg.get(players).isRole(RoleLG.PETITE_FILLE))){
							board.getTeam(players).setNameTagVisibility(NameTagVisibility.NEVER);
						}
						else board.getTeam(players).setNameTagVisibility(NameTagVisibility.ALWAYS);
					}
				}
			}
			if(main.config.scenario.get(ScenarioLG.NO_NAME_TAG)){
				main.board.getTeam(playername).setNameTagVisibility(NameTagVisibility.NEVER);
			}
			else {
				if(!main.playerlg.get(playername).hasPower() && (main.playerlg.get(playername).isRole(RoleLG.LOUP_PERFIDE) || main.playerlg.get(playername).isRole(RoleLG.PETITE_FILLE))){
					main.board.getTeam(playername).setNameTagVisibility(NameTagVisibility.NEVER);
				}
				else {
					main.board.getTeam(playername).setNameTagVisibility(NameTagVisibility.ALWAYS);
				}
			}
		}
	}

	public void resetRole() {
		for (int i=0;i<RoleLG.values().length;i++) {
			main.config.role_count.put(RoleLG.values()[i],0);
		}
		main.score.setRole(0);
		updateSelection();
		main.score.updateBoard();
	}
}


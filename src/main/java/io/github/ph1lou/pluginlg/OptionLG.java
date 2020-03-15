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
import java.util.Arrays;
import java.util.List;


public class OptionLG {


	private Inventory invtool;
	private Inventory invrole;
	public Inventory invtimer;
	public Inventory invborder;
	private Inventory invscenario;
	private Inventory invstuff;
	private Inventory invsave;
	private Inventory invconfig;
	
	private final MainLG main;
	
	public OptionLG(MainLG main) {
		this.main=main;

	}

	public void initInv(){
		invtool = Bukkit.createInventory(null, 18,main.text.getText(175));
		invrole = Bukkit.createInventory(null, 45,main.text.getText(176));
		invtimer = Bukkit.createInventory(null, 27,main.text.getText(177));
		invconfig = Bukkit.createInventory(null, 36,main.text.getText(178));
		invscenario = Bukkit.createInventory(null, 36,main.text.getText(76));
		invborder = Bukkit.createInventory(null, 18,main.text.getText(179));
		invsave = Bukkit.createInventory(null, 18,main.text.getText(180));
		invstuff = Bukkit.createInventory(null, 18,main.text.getText(77));
	}
	
	public void toolBar(Player player) {


		invtool.setItem(1, changeMeta(Material.BEACON,main.text.getText(176),1,null));
		invtool.setItem(3, changeMeta(Material.ANVIL,main.text.getText(177),1,null));
		invtool.setItem(5, changeMeta(Material.MAP,main.text.getText(178),1,null));
		invtool.setItem(7, changeMeta(Material.CHEST,main.text.getText(77),1,null));
		invtool.setItem(10, changeMeta(Material.GLASS,main.text.getText(179),1,null));
		invtool.setItem(12, changeMeta(Material.ARMOR_STAND,main.text.getText(180),1,null));
		invtool.setItem(14, changeMeta(Material.PUMPKIN,main.text.getText(76),1,null));
		player.openInventory(invtool);
	}
	
	public void chooseRole(Player player) {
		

		invrole.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		invrole.setItem(8, changeMeta(Material.BARRIER,main.text.getText(183),1,null));
		updateSelection(invrole);
		player.openInventory(invrole);
	}
	
	public void timerTool(Player player) {

		invtimer.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionTimer(invtimer, 0);
		player.openInventory(invtimer);
	}
	
	public void globalTool(Player player) {

		invconfig.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionTool(invconfig);
		player.openInventory(invconfig);
	}

	public void globalScenario(Player player) {

		invscenario.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionScenario(invscenario);
		player.openInventory(invscenario);
	}

	
	public void borderTool(Player player) {

		invborder.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionBorder(invborder,0);
		player.openInventory(invborder);
	}
	
	public void saveTool(Player player) {

		invsave.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionSave(invsave, 1);
		player.openInventory(invsave);
	}

	public void stuffTool(Player player) {

		invstuff.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		invstuff.setItem(2, changeMeta(Material.EGG,"Charger Stuff Role normal",1,null));
		invstuff.setItem(4, changeMeta(Material.GOLD_SWORD,"Charger Stuff MeetUp",1,null));
		invstuff.setItem(6, changeMeta(Material.JUKEBOX,"Charger Stuff Depart Chill",1,null));
		invstuff.setItem(10, changeMeta(Material.BARRIER,"Clear le Stuff de Départ et Mort",1,null));
		invstuff.setItem(13, changeMeta(Material.CHEST,main.text.getText(182),1,null));
		invstuff.setItem(16, changeMeta(Material.ENDER_CHEST,main.text.getText(181),1,null));
		player.openInventory(invstuff);
	}

	public void updateSelectionSave(Inventory inv, int j) {
		
		for(int i=1;i<9;i++) {
			
			if(i==j) {
				inv.setItem(i, changeMeta(Material.FEATHER,String.format(main.text.getText(174),i),1,null));
			}
			else inv.setItem(i, changeMeta(Material.PAPER,String.format(main.text.getText(174),i),1,null));
		}
		
		inv.setItem(12, changeMeta(Material.EMERALD_BLOCK,String.format(main.text.getText(173),j),1,null));
		
		File file = new File(main.getDataFolder(), "save"+j+".json");
		
		if(file.exists()) {
			inv.setItem(14, changeMeta(Material.BARRIER,String.format(main.text.getText(171),j),1,null));
			inv.setItem(12, changeMeta(Material.BED,String.format(main.text.getText(172),j),1,null));
		}
		else inv.setItem(14, null);
	}
	
	public void load(Inventory inv) {
		int j= findSelect(inv)+9;
		main.config.getConfig(main, j);
		main.stufflg.load(main, j);
		updateSelectionSave(inv, j);
	}
	
	public void saveOrErase(Inventory inv) {
		
		int i= findSelect(inv)+9;
		File file = new File(main.getDataFolder(), "save"+i+".json");
		main.filelg.save(file, main.serialize.serialize(main.config));
		main.stufflg.save(main, i);
		updateSelectionSave(inv, i);
	}

	

	public ItemStack changeMeta(Material m, String item_name, int i, List<String> lore) {
		
		ItemStack item1 = new ItemStack(m,i);
		ItemMeta meta1 = item1.getItemMeta();
		meta1.setDisplayName(item_name);
		meta1.setLore(lore);
		item1.setItemMeta(meta1);
		return item1;
	}
	
	public int findSelect(Inventory inv) {
		
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
	
	public void selectMoins(Inventory inv, int i) {

		int j = main.config.role_count.get(RoleLG.values()[i]);
		if(j>0) {
			if(!RoleLG.values()[i].equals(RoleLG.COUPLE)) {
				main.score.setRole(main.score.getRole()-1);
			}
			main.config.role_count.put(RoleLG.values()[i],j-1);
			main.score.updateBoard();
			updateSelection(inv);
		}
	}

	public void selectPlus(Inventory inv, int i) {

		int j = main.config.role_count.get(RoleLG.values()[i]);
		main.config.role_count.put(RoleLG.values()[i],j+1);
		if(!RoleLG.values()[i].equals(RoleLG.COUPLE)) {
			main.score.setRole(main.score.getRole()+1);
		}
		main.score.updateBoard();
		updateSelection(inv);
	}
	
	public void selectMoinsBorder(Inventory inv) {
		
		int i = findSelect(inv) ;
		int j = main.config.border_value.get(BorderLG.values()[i]);
		if(j>=100) {
			main.config.border_value.put(BorderLG.values()[i],j-100);
			updateSelectionBorder(inv, i);
		}
	}

	public void selectPlusBorder(Inventory inv) {
		
		int i = findSelect(inv) ;
		int j = main.config.border_value.get(BorderLG.values()[i]);
		main.config.border_value.put(BorderLG.values()[i],j+100);
		updateSelectionBorder(inv, i);
	}
	
	public void SelectMoinsTimer(Inventory inv, int v) {
		
		int i = findSelect(inv);
		int j = main.config.value.get(TimerLG.values()[i]);
		if(j>=v) {
			main.config.value.put(TimerLG.values()[i],j-v);
			updateSelectionTimer(inv, i);
		}
	}

	public void selectPlusTimer(Inventory inv, int v) {
		int i = findSelect(inv);
		main.config.value.put(TimerLG.values()[i],main.config.value.get(TimerLG.values()[i])+v);
		updateSelectionTimer(inv,i);
	}
	

	
	public void updateSelection(Inventory inv){

		List<String> lore = Arrays.asList("Clique-Gauche>>+", "Clique-Droit>>-", "Shift-Clique>>Stuff");
		for (int i=0;i<RoleLG.values().length;i++) {

			if (main.config.role_count.get(RoleLG.values()[i])>0) {
				inv.setItem(9+i, changeMeta(Material.EMERALD,main.text.translaterole.get(RoleLG.values()[i]),main.config.role_count.get(RoleLG.values()[i]),lore));
			}
			else inv.setItem(9+i, changeMeta(Material.REDSTONE,main.text.translaterole.get(RoleLG.values()[i]),1,lore));
		}
	}
	
	public void updateSelectionTimer(Inventory inv, int j){
		
		String c= main.conversion(main.config.value.get(TimerLG.values()[j]));
		
		inv.setItem(1, changeMeta(Material.STONE_BUTTON,"-10m ("+c+")",1,null));
		inv.setItem(2, changeMeta(Material.STONE_BUTTON,"-1m ("+c+")",1,null));
		inv.setItem(3, changeMeta(Material.STONE_BUTTON,"-10s ("+c+")",1,null));
		inv.setItem(4, changeMeta(Material.BEACON,String.format(main.text.translatetimer.get(TimerLG.values()[j]),c),1,null));
		inv.setItem(5, changeMeta(Material.STONE_BUTTON,"+10s ("+c+")",1,null));
		inv.setItem(6, changeMeta(Material.STONE_BUTTON,"+1m ("+c+")",1,null));
		inv.setItem(7, changeMeta(Material.STONE_BUTTON,"+10m ("+c+")",1,null));
		
		for (int i=0;i<TimerLG.values().length;i++) {
			if(i==j) {
				inv.setItem(9+i, changeMeta(Material.FEATHER,String.format(main.text.translatetimer.get(TimerLG.values()[i]),c),1,null));
			}
			else inv.setItem(9+i, changeMeta(Material.ANVIL,String.format(main.text.translatetimer.get(TimerLG.values()[i]),main.conversion(main.config.value.get(TimerLG.values()[i]))),1,null));
		}
	}
	
	public void updateSelectionBorder(Inventory inv, int j) {
		
		inv.setItem(3, changeMeta(Material.STONE_BUTTON,"- ("+main.config.border_value.get(BorderLG.values()[j])+")",1,null));
		inv.setItem(4, changeMeta(Material.BEACON,String.format(main.text.translatebordure.get(BorderLG.values()[j]),main.config.border_value.get(BorderLG.values()[j])),1,null));
		inv.setItem(5, changeMeta(Material.STONE_BUTTON,"+ ("+main.config.border_value.get(BorderLG.values()[j])+")",1,null));

		for (int i = 0; i< BorderLG.values().length; i++) {
			
			if(i==j) {
				inv.setItem(9+i, changeMeta(Material.FEATHER,String.format(main.text.translatebordure.get(BorderLG.values()[i]),main.config.border_value.get(BorderLG.values()[i])),1,null));

			}
			else inv.setItem(9+i, changeMeta(Material.GLASS,String.format(main.text.translatebordure.get(BorderLG.values()[i]),main.config.border_value.get(BorderLG.values()[i])),1,null));

		}
		
	}
	public void updateSelectionTool(Inventory inv){
		
		for (int i = 0; i< ToolLG.values().length; i++) {
			
			if (main.config.tool_switch.get(ToolLG.values()[i])) {
				
				inv.setItem(9+i, changeMeta(Material.EMERALD,String.format(main.text.getText(168),main.text.translatetool.get(ToolLG.values()[i])),1,null));
			}
			else inv.setItem(9+i, changeMeta(Material.REDSTONE,String.format(main.text.getText(169),main.text.translatetool.get(ToolLG.values()[i])),1,null));
		}
	}

	public void updateSelectionScenario(Inventory inv) {

		for (int i = 0; i< ScenarioLG.values().length; i++) {

			if (main.config.scenario.get(ScenarioLG.values()[i])) {

				inv.setItem(9+i, changeMeta(Material.EMERALD,String.format(main.text.getText(168),main.text.translatescenario.get(ScenarioLG.values()[i])),1,null));
			}
			else inv.setItem(9+i, changeMeta(Material.REDSTONE,String.format(main.text.getText(169),main.text.translatescenario.get(ScenarioLG.values()[i])),1,null));
		}
		updateScenario();
	}

	public void updateScenario() {

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

	public void resetrole(Inventory inv) {
		for (int i=0;i<RoleLG.values().length;i++) {
			main.config.role_count.put(RoleLG.values()[i],0);
		}
		main.score.setRole(0);
		updateSelection(inv);
		main.score.updateBoard();
	}



}


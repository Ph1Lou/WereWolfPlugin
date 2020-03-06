package io.github.ph1lou.pluginlg;


import java.io.File;

import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;


public class OptionLG {
	
	
	
	private final MainLG main;
	
	public OptionLG(MainLG main) {
		this.main=main;
	}
	
	public void toolbar(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 18,main.text.getText(175));
		inv.setItem(1, changeMeta(Material.BEACON,main.text.getText(176),1));
		inv.setItem(3, changeMeta(Material.ANVIL,main.text.getText(177),1));
		inv.setItem(5, changeMeta(Material.MAP,main.text.getText(178),1));
		inv.setItem(7, changeMeta(Material.CHEST,main.text.getText(182),1));
		inv.setItem(10, changeMeta(Material.GLASS,main.text.getText(179),1));
		inv.setItem(12, changeMeta(Material.ARMOR_STAND,main.text.getText(180),1));
		inv.setItem(14, changeMeta(Material.PUMPKIN,main.text.getText(76),1));
		inv.setItem(16, changeMeta(Material.CHEST,main.text.getText(181),1));
		player.openInventory(inv);
	}
	
	public void chooserole(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 45,main.text.getText(176));
		inv.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1));
		inv.setItem(8, changeMeta(Material.BARRIER,main.text.getText(183),1));
		updateselection(inv, 0);
		player.openInventory(inv);
	}
	
	public void timertool(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 27,main.text.getText(177));
		inv.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1));
		updateselectiontimer(inv, 0);
		player.openInventory(inv);
	}
	
	public void globaltool(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 36,main.text.getText(178));
		inv.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1));
		updateselectiontool(inv);
		player.openInventory(inv);
	}

	public void globalscenario(Player player) {

		Inventory inv = Bukkit.createInventory(null, 36,main.text.getText(76));
		inv.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1));
		updateSelectionScenario(inv);
		player.openInventory(inv);
	}

	
	public void borduretool(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 18,main.text.getText(179));
		inv.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1));
		updateselectionbordure(inv,0);
		player.openInventory(inv);
	}
	
	public void savetool(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 18,main.text.getText(180));
		inv.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1));
		updateselectionsave(inv, 1);
		player.openInventory(inv);
	}

	public void updateselectionsave(Inventory inv, int j) {
		
		for(int i=1;i<9;i++) {
			
			if(i==j) {
				inv.setItem(i, changeMeta(Material.FEATHER,main.text.getText(174)+i,1));
			}
			else inv.setItem(i, changeMeta(Material.PAPER,main.text.getText(174)+i,1));
		}
		
		inv.setItem(12, changeMeta(Material.EMERALD_BLOCK,main.text.getText(173)+j,1));
		
		File file = new File(main.getDataFolder(), "save"+j+".json");
		
		if(file.exists()) {
			inv.setItem(14, changeMeta(Material.BARRIER,main.text.getText(171)+j,1));
			inv.setItem(12, changeMeta(Material.BED,main.text.getText(172)+j,1));
		}
		else inv.setItem(14, null);
	}
	
	public void load(Inventory inv) {
		int j= findSelect(inv)+9;
		main.config.getConfig(main, j);
		main.stufflg.load(main, j);
		updateselectionsave(inv, j);
	}
	
	public void saveorerase(Inventory inv) {
		
		int i= findSelect(inv)+9;
		File file = new File(main.getDataFolder(), "save"+i+".json");
		main.filelg.save(file, main.serialize.serialize(main.config));
		main.stufflg.save(main, i);
		updateselectionsave(inv, i);
	}

	

	public ItemStack changeMeta(Material m, String itemname, int i) {
		
		ItemStack item1 = new ItemStack(m,i);
		ItemMeta meta1 = item1.getItemMeta();
		meta1.setDisplayName(itemname);
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
		return (i-9);
	}
	
	public void selectmoins(Inventory inv, int i) {

		int compt = main.config.role_count.get(RoleLG.values()[i]);
		if(compt>0) {
			if(!RoleLG.values()[i].equals(RoleLG.COUPLE)) {
				main.score.setRole(main.score.getRole()-1);
			}
			main.config.role_count.put(RoleLG.values()[i],compt-1);
			main.score.updateBoard();
			updateselection(inv, i);
		}
	}

	public void selectplus(Inventory inv, int i) {

		int compt = main.config.role_count.get(RoleLG.values()[i]);
		main.config.role_count.put(RoleLG.values()[i],compt+1);
		if(!RoleLG.values()[i].equals(RoleLG.COUPLE)) {
			main.score.setRole(main.score.getRole()+1);
		}
		main.score.updateBoard();
		updateselection(inv, i);
	}
	
	public void selectmoinsbordure(Inventory inv) {
		
		int i = findSelect(inv) ;
		int compt = main.config.border_value.get(BorderLG.values()[i]);
		if(compt>=100) {
			main.config.border_value.put(BorderLG.values()[i],compt-100);
			updateselectionbordure(inv, i);
		}
	}

	public void selectplusbordure(Inventory inv) {
		
		int i = findSelect(inv) ;
		int compt = main.config.border_value.get(BorderLG.values()[i]);
		main.config.border_value.put(BorderLG.values()[i],compt+100);
		updateselectionbordure(inv, i);
	}
	
	public void selectmoinstimer(Inventory inv) {
		
		int i = findSelect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		if(compt>=10) {
			main.config.value.put(TimerLG.values()[i],compt-10);
			updateselectiontimer(inv, i);
		}
	}
	
	public void selectmoinsmoinstimer(Inventory inv) {
		
		int i = findSelect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		if(compt>=60) {
			main.config.value.put(TimerLG.values()[i],compt-60);
			updateselectiontimer(inv, i);
		}
		
	}
	
	public void selectmoinsmoinsmoinstimer(Inventory inv) {
		
		int i = findSelect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		if(compt>=600) {
			main.config.value.put(TimerLG.values()[i],compt-600);
			updateselectiontimer(inv, i);
		}
	}

	public void selectplustimer(Inventory inv) {
		int i = findSelect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		main.config.value.put(TimerLG.values()[i],compt+10);
		updateselectiontimer(inv,i);
	}
	
	public void selectplusplustimer(Inventory inv) {
		int i = findSelect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		main.config.value.put(TimerLG.values()[i],compt+60);
		updateselectiontimer(inv,i);
	}
	
	public void selectplusplusplustimer(Inventory inv) {
		int i = findSelect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		main.config.value.put(TimerLG.values()[i],compt+600);
		updateselectiontimer(inv,i);
	}
	
	public void updateselection(Inventory inv,int j){
		

		inv.setItem(4, changeMeta(Material.BEACON,main.text.getText(200)+main.text.translaterole.get(RoleLG.values()[j]),1));
		
		for (int i=0;i<RoleLG.values().length;i++) {

			if (main.config.role_count.get(RoleLG.values()[i])>0) {
				inv.setItem(9+i, changeMeta(Material.EMERALD,main.text.translaterole.get(RoleLG.values()[i]),main.config.role_count.get(RoleLG.values()[i])));
			}
			else inv.setItem(9+i, changeMeta(Material.REDSTONE,main.text.translaterole.get(RoleLG.values()[i]),1));
		}
	}
	
	public void updateselectiontimer(Inventory inv,int j){
		
		String c= main.conversion(main.config.value.get(TimerLG.values()[j]));
		
		inv.setItem(1, changeMeta(Material.STONE_BUTTON,"-10m ("+c+")",1));
		inv.setItem(2, changeMeta(Material.STONE_BUTTON,"-1m ("+c+")",1));
		inv.setItem(3, changeMeta(Material.STONE_BUTTON,"-10s ("+c+")",1));
		inv.setItem(4, changeMeta(Material.BEACON,main.text.translatetimer.get(TimerLG.values()[j])+" ("+c+")",1));
		inv.setItem(5, changeMeta(Material.STONE_BUTTON,"+10s ("+c+")",1));
		inv.setItem(6, changeMeta(Material.STONE_BUTTON,"+1m ("+c+")",1));
		inv.setItem(7, changeMeta(Material.STONE_BUTTON,"+10m ("+c+")",1));
		
		for (int i=0;i<TimerLG.values().length;i++) {
			if(i==j) {
				inv.setItem(9+i, changeMeta(Material.FEATHER,main.text.translatetimer.get(TimerLG.values()[i])+" ("+c+")",1));
			}
			else inv.setItem(9+i, changeMeta(Material.ANVIL,main.text.translatetimer.get(TimerLG.values()[i])+" ("+main.conversion(main.config.value.get(TimerLG.values()[i]))+")",1));
		}
	}
	
	public void updateselectionbordure(Inventory inv, int j) {
		
		inv.setItem(3, changeMeta(Material.STONE_BUTTON,"- ("+main.config.border_value.get(BorderLG.values()[j])+")",1));
		inv.setItem(4, changeMeta(Material.BEACON,main.text.translatebordure.get(BorderLG.values()[j])+" ("+main.config.border_value.get(BorderLG.values()[j])+")",1));
		inv.setItem(5, changeMeta(Material.STONE_BUTTON,"+ ("+main.config.border_value.get(BorderLG.values()[j])+")",1));

		for (int i = 0; i< BorderLG.values().length; i++) {
			
			if(i==j) {
				inv.setItem(9+i, changeMeta(Material.FEATHER,main.text.translatebordure.get(BorderLG.values()[i])+" ("+main.config.border_value.get(BorderLG.values()[i])+")",1));

			}
			else inv.setItem(9+i, changeMeta(Material.GLASS,main.text.translatebordure.get(BorderLG.values()[i])+" ("+main.config.border_value.get(BorderLG.values()[i])+")",1));

		}
		
	}
	public void updateselectiontool(Inventory inv){
		
		for (int i = 0; i< ToolLG.values().length; i++) {
			
			if (main.config.tool_switch.get(ToolLG.values()[i])) {
				
				inv.setItem(9+i, changeMeta(Material.EMERALD,main.text.getText(168)+main.text.translatetool.get(ToolLG.values()[i]),1));
			}
			else inv.setItem(9+i, changeMeta(Material.REDSTONE,main.text.getText(169)+main.text.translatetool.get(ToolLG.values()[i]),1));
		}
	}

	public void updateSelectionScenario(Inventory inv) {

		for (int i = 0; i< ScenarioLG.values().length; i++) {

			if (main.config.scenario.get(ScenarioLG.values()[i])) {

				inv.setItem(9+i, changeMeta(Material.EMERALD,main.text.getText(168)+main.text.translatescenario.get(ScenarioLG.values()[i]),1));
			}
			else inv.setItem(9+i, changeMeta(Material.REDSTONE,main.text.getText(169)+main.text.translatescenario.get(ScenarioLG.values()[i]),1));
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
		updateselection(inv, findSelect(inv));
		main.score.updateBoard();
	}



}


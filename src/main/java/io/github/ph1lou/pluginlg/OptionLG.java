package io.github.ph1lou.pluginlg;


import java.io.File;

import io.github.ph1lou.pluginlg.enumlg.BordureLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.Bukkit;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;



public class OptionLG {
	
	
	
	private MainLG main;
	
	public OptionLG(MainLG main) {
		
		this.main=main;	
	
	}
	
	public void toolbar(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 18,main.texte.getText(175));
		inv.setItem(1,changemeta(Material.BEACON,main.texte.getText(176),1));
		inv.setItem(3,changemeta(Material.ANVIL,main.texte.getText(177),1));
		inv.setItem(5,changemeta(Material.MAP,main.texte.getText(178),1));
		inv.setItem(7,changemeta(Material.CHEST,main.texte.getText(182),1));
		inv.setItem(10,changemeta(Material.GLASS,main.texte.getText(179),1));
		inv.setItem(12,changemeta(Material.ARMOR_STAND,main.texte.getText(180),1));
		inv.setItem(16,changemeta(Material.CHEST,main.texte.getText(181),1));
		player.openInventory(inv);
	}
	
	public void chooserole(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 45,main.texte.getText(176));
		inv.setItem(0,changemeta(Material.COMPASS,main.texte.getText(170),1));
		inv.setItem(8,changemeta(Material.BARRIER,main.texte.getText(183),1));
		updateselection(inv, 0);
		player.openInventory(inv);
	}
	
	public void timertool(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 27,main.texte.getText(177));
		inv.setItem(0,changemeta(Material.COMPASS,main.texte.getText(170),1));
		updateselectiontimer(inv, 0);
		player.openInventory(inv);
	}
	
	public void globaltool(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 36,main.texte.getText(178));
		inv.setItem(0,changemeta(Material.COMPASS,main.texte.getText(170),1));
		updateselectiontool(inv);
		player.openInventory(inv);
	}
	
	public void borduretool(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 18,main.texte.getText(179));
		inv.setItem(0,changemeta(Material.COMPASS,main.texte.getText(170),1));
		updateselectionbordure(inv,0);
		player.openInventory(inv);
	}
	
	public void savetool(Player player) {
		
		Inventory inv = Bukkit.createInventory(null, 18,main.texte.getText(180));
		inv.setItem(0,changemeta(Material.COMPASS,main.texte.getText(170),1));
		
		updateselectionsave(inv, 1);
		player.openInventory(inv);
		
	}

	public void updateselectionsave(Inventory inv, int j) {
		
		for(int i=1;i<9;i++) {
			
			if(i==j) {
				inv.setItem(i,changemeta(Material.FEATHER,main.texte.getText(174)+i,1));
			}
			else inv.setItem(i,changemeta(Material.PAPER,main.texte.getText(174)+i,1));
		}
		
		inv.setItem(12,changemeta(Material.EMERALD_BLOCK,main.texte.getText(173)+j,1));
		
		File file = new File(main.getDataFolder(), "savelg"+j+".json");
		
		if(file.exists()) {
			inv.setItem(14,changemeta(Material.BARRIER,main.texte.getText(171)+j,1));
			inv.setItem(12,changemeta(Material.BED,main.texte.getText(172)+j,1));
		}
		else inv.setItem(14, null);
		
		
	}
	
	public void load(Inventory inv) {
		int j=findselect(inv)+9;
		main.config.getconfig(main, j);
		main.stufflg.load(main, j);
		updateselectionsave(inv, j);
		
	}

	

	
	
	public void saveorerase(Inventory inv) {
		
		int i=findselect(inv)+9;
		File file = new File(main.getDataFolder(), "savelg"+i+".json");
		main.filelg.save(file, main.serialize.serialize(main.config));
		main.stufflg.save(main, i);
		updateselectionsave(inv, i);
		
		
		
	}

	

	public ItemStack changemeta(Material m,String itemname,int i) {
		
		ItemStack item1 = new ItemStack(m,i);
		ItemMeta meta1 = item1.getItemMeta();
		meta1.setDisplayName(itemname);
		item1.setItemMeta(meta1);
		return item1;
	}
	
	public int findselect(Inventory inv) {
		
		int i=0;
		Boolean trouver = false;
		while (i<inv.getSize() && !trouver) {
			if(inv.getItem(i)!=null && inv.getItem(i).getType()==Material.FEATHER) {
				trouver=true;
			}
			else i++;
		}
		return (i-9);
	}
	
	public void selectmoins(Inventory inv) {
		
		int i = findselect(inv) ;
		int compt = main.config.rolecount.get(RoleLG.values()[i]);
		if(compt>0) {
			if(!RoleLG.values()[i].equals(RoleLG.COUPLE)) {
				main.score.setRole(main.score.getRole()-1);
			}
			main.config.rolecount.put(RoleLG.values()[i],compt-1);
			main.score.updateBoard();
			updateselection(inv, i);
		}
	}

	public void selectplus(Inventory inv) {
		
		int i = findselect(inv) ;
		int compt = main.config.rolecount.get(RoleLG.values()[i]);
		main.config.rolecount.put(RoleLG.values()[i],compt+1);
		if(!RoleLG.values()[i].equals(RoleLG.COUPLE)) {
			main.score.setRole(main.score.getRole()+1);
		}
		main.score.updateBoard();
		updateselection(inv, i);
	}
	
	public void selectmoinsbordure(Inventory inv) {
		
		int i = findselect(inv) ;
		int compt = main.config.bordurevalue.get(BordureLG.values()[i]);
		if(compt>=100) {
			main.config.bordurevalue.put(BordureLG.values()[i],compt-100);
			updateselectionbordure(inv, i);
		}
	}

	public void selectplusbordure(Inventory inv) {
		
		int i = findselect(inv) ;
		int compt = main.config.bordurevalue.get(BordureLG.values()[i]);
		main.config.bordurevalue.put(BordureLG.values()[i],compt+100);
		updateselectionbordure(inv, i);
	}
	
	public void selectmoinstimer(Inventory inv) {
		
		int i =findselect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		if(compt>=10) {
			main.config.value.put(TimerLG.values()[i],compt-10);
			updateselectiontimer(inv, i);
		}
	}
	
	public void selectmoinsmoinstimer(Inventory inv) {
		
		int i =findselect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		if(compt>=60) {
			main.config.value.put(TimerLG.values()[i],compt-60);
			updateselectiontimer(inv, i);
		}
		
	}
	
	public void selectmoinsmoinsmoinstimer(Inventory inv) {
		
		int i =findselect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		if(compt>=600) {
			main.config.value.put(TimerLG.values()[i],compt-600);
			updateselectiontimer(inv, i);
		}
	}

	public void selectplustimer(Inventory inv) {
		int i =findselect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		main.config.value.put(TimerLG.values()[i],compt+10);
		updateselectiontimer(inv,i);
	}
	
	public void selectplusplustimer(Inventory inv) {
		int i =findselect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		main.config.value.put(TimerLG.values()[i],compt+60);
		updateselectiontimer(inv,i);
	}
	
	public void selectplusplusplustimer(Inventory inv) {
		int i =findselect(inv);
		int compt = main.config.value.get(TimerLG.values()[i]);
		main.config.value.put(TimerLG.values()[i],compt+600);
		updateselectiontimer(inv,i);
	}
	
	public void updateselection(Inventory inv,int j){
		
		inv.setItem(3,changemeta(Material.STONE_BUTTON,"- ("+main.config.rolecount.get(RoleLG.values()[j])+")",1));
		inv.setItem(4,changemeta(Material.BEACON,main.texte.getText(200)+main.texte.translaterole.get(RoleLG.values()[j]),1));
		inv.setItem(5,changemeta(Material.STONE_BUTTON,"+ ("+main.config.rolecount.get(RoleLG.values()[j])+")",1));
		
		for (int i=0;i<RoleLG.values().length;i++) {
			
			if(i==j) {
				if(main.config.rolecount.get(RoleLG.values()[i])==0) {
					inv.setItem(9+i,changemeta(Material.FEATHER,main.texte.translaterole.get(RoleLG.values()[i]),1));
				}
				else inv.setItem(9+i,changemeta(Material.FEATHER,main.texte.translaterole.get(RoleLG.values()[i]),main.config.rolecount.get(RoleLG.values()[i])));
			}
			else if (main.config.rolecount.get(RoleLG.values()[i])>0) {
				
				inv.setItem(9+i,changemeta(Material.EMERALD,main.texte.translaterole.get(RoleLG.values()[i]),main.config.rolecount.get(RoleLG.values()[i])));
			}
			else inv.setItem(9+i,changemeta(Material.REDSTONE,main.texte.translaterole.get(RoleLG.values()[i]),1));
		}
	}
	
	public void updateselectiontimer(Inventory inv,int j){
		
		String c= main.conversion(main.config.value.get(TimerLG.values()[j]));
		
		inv.setItem(1,changemeta(Material.STONE_BUTTON,"-10m ("+c+")",1));
		inv.setItem(2,changemeta(Material.STONE_BUTTON,"-1m ("+c+")",1));
		inv.setItem(3,changemeta(Material.STONE_BUTTON,"-10s ("+c+")",1));
		inv.setItem(4,changemeta(Material.BEACON,main.texte.translatetimer.get(TimerLG.values()[j])+" ("+c+")",1));
		inv.setItem(5,changemeta(Material.STONE_BUTTON,"+10s ("+c+")",1));
		inv.setItem(6,changemeta(Material.STONE_BUTTON,"+1m ("+c+")",1));
		inv.setItem(7,changemeta(Material.STONE_BUTTON,"+10m ("+c+")",1));
		
		for (int i=0;i<TimerLG.values().length;i++) {
			if(i==j) {
				inv.setItem(9+i,changemeta(Material.FEATHER,main.texte.translatetimer.get(TimerLG.values()[i])+" ("+c+")",1));
			}
			else inv.setItem(9+i,changemeta(Material.ANVIL,main.texte.translatetimer.get(TimerLG.values()[i])+" ("+main.conversion(main.config.value.get(TimerLG.values()[i]))+")",1));
		}
	}
	
	public void updateselectionbordure(Inventory inv, int j) {
		
		inv.setItem(3,changemeta(Material.STONE_BUTTON,"- ("+main.config.bordurevalue.get(BordureLG.values()[j])+")",1));
		inv.setItem(4,changemeta(Material.BEACON,main.texte.translatebordure.get(BordureLG.values()[j])+" ("+main.config.bordurevalue.get(BordureLG.values()[j])+")",1));
		inv.setItem(5,changemeta(Material.STONE_BUTTON,"+ ("+main.config.bordurevalue.get(BordureLG.values()[j])+")",1));
		
		for (int i=0;i<BordureLG.values().length;i++) {
			
			if(i==j) {
				inv.setItem(9+i,changemeta(Material.FEATHER,main.texte.translatebordure.get(BordureLG.values()[i])+" ("+main.config.bordurevalue.get(BordureLG.values()[i])+")",1));

			}
			else inv.setItem(9+i,changemeta(Material.GLASS,main.texte.translatebordure.get(BordureLG.values()[i])+" ("+main.config.bordurevalue.get(BordureLG.values()[i])+")",1));

		}
		
	}
	public void updateselectiontool(Inventory inv){
		
		for (int i = 0; i< ToolLG.values().length; i++) {
			
			if (main.config.tool_switch.get(ToolLG.values()[i])) {
				
				inv.setItem(9+i,changemeta(Material.EMERALD,main.texte.getText(168)+main.texte.translatetool.get(ToolLG.values()[i]),1));
			}
			else inv.setItem(9+i,changemeta(Material.REDSTONE,main.texte.getText(169)+main.texte.translatetool.get(ToolLG.values()[i]),1));
		}
	}

	public void resetrole(Inventory inv) {
		for (int i=0;i<RoleLG.values().length;i++) {
			main.config.rolecount.put(RoleLG.values()[i],0);
		}
		main.score.setRole(0);
		updateselection(inv, findselect(inv));
		main.score.updateBoard();
	}

	

	

	
	

	
}


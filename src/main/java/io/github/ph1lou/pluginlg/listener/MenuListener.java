package io.github.ph1lou.pluginlg.listener;




import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MenuListener implements Listener{

	private final MainLG main;
	
	public MenuListener(MainLG main) {
		this.main=main;
	}
	
	@EventHandler
    private void onSousMenu(InventoryClickEvent event) {
		
		InventoryView view = event.getView();
		Inventory invent = event.getInventory();
		Player player =(Player) event.getWhoClicked();
		ItemStack current = event.getCurrentItem();
		
		if (current!=null) {
			
			if(view.getTitle().equalsIgnoreCase(main.text.getText(175))) {

				event.setCancelled(true);

				if(current.getType()==Material.BEACON) {
					if(!main.isState(StateLG.LG)) {
						main.optionlg.chooserole(player);	
					}
					else player.sendMessage(main.text.getText(126));
				}
				if(current.getType()==Material.ANVIL) {
					main.optionlg.timertool(player);
				}
				if(current.getType()==Material.MAP) {
					main.optionlg.globaltool(player);
				}
				if(current.getType()==Material.PUMPKIN) {
					main.optionlg.globalscenario(player);
				}
				if(current.getType()==Material.CHEST  && event.getSlot()==7) {
					
					player.setGameMode(GameMode.CREATIVE);
					player.getInventory().clear();
					for(ItemStack i:main.stufflg.getStartLoot()) {
						if(i!=null) {
							player.getInventory().addItem(i);
						}		
					}
					TextComponent msg = new TextComponent(main.text.getText(127));
					msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/adminlg lootstart"));
					player.spigot().sendMessage(msg);
					player.closeInventory();
				}
				if(current.getType()==Material.GLASS  && event.getSlot()==10) {
					main.optionlg.borduretool(player);
				}
				if(current.getType()==Material.ARMOR_STAND  && event.getSlot()==12) {
					main.optionlg.savetool(player);
				}
				if(current.getType()==Material.CHEST  && event.getSlot()==16) {
					player.setGameMode(GameMode.CREATIVE);
					player.getInventory().clear();
					for(ItemStack i:main.stufflg.getDeathLoot()) {
						if(i!=null) {
							player.getInventory().addItem(i);
						}		
					}
					TextComponent msgd = new TextComponent(main.text.getText(128));
					msgd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/adminlg lootdeath"));
					player.spigot().sendMessage(msgd);	
					player.closeInventory();
				}
			}		
			else if(view.getTitle().equals(main.text.getText(178))){
				event.setCancelled(true);
				if(current.getType()==Material.EMERALD || current.getType()==Material.REDSTONE){
					main.config.tool_switch.put(ToolLG.values()[(event.getSlot()-9)],!main.config.tool_switch.get(ToolLG.values()[(event.getSlot()-9)]));
					main.optionlg.updateselectiontool(invent);
				}	
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolbar(player);
				}
			}

			else if(view.getTitle().equals(main.text.getText(76))){
				event.setCancelled(true);
				if(current.getType()==Material.EMERALD || current.getType()==Material.REDSTONE){
					main.config.scenario.put(ScenarioLG.values()[(event.getSlot()-9)],!main.config.scenario.get(ScenarioLG.values()[(event.getSlot()-9)]));
					main.optionlg.updateSelectionScenario(invent);
				}
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolbar(player);
				}
			}
			
			else if(view.getTitle().equals(main.text.getText(176))) {

				event.setCancelled(true);


				if(current.getType()==Material.EMERALD || current.getType()==Material.REDSTONE){

					if(event.getClick().isShiftClick()) {
						player.setGameMode(GameMode.CREATIVE);
						player.getInventory().clear();
						int j = (event.getSlot()-9);
						for(ItemStack i:main.stufflg.role_stuff.get(RoleLG.values()[j])) {
							if(i!=null) {
								player.getInventory().addItem(i);
							}
						}
						TextComponent msgd = new TextComponent(main.text.getText(198));
						msgd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/adminlg stuffrole "+j));
						player.spigot().sendMessage(msgd);
						player.closeInventory();
					}
					if(event.getClick().isRightClick()){
						main.optionlg.selectmoins(invent,(event.getSlot()-9));
					}
					else{
						main.optionlg.selectplus(invent,(event.getSlot()-9));
					}

					//main.optionlg.updateselection(invent);
				}
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolbar(player);
				}
				if(current.getType()==Material.BARRIER) {
					main.optionlg.resetrole(invent);
				}

			}
			else if(view.getTitle().equals(main.text.getText(179))) {

				event.setCancelled(true);

				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==3) {
					main.optionlg.selectmoinsbordure(invent);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==5) {
					main.optionlg.selectplusbordure(invent);
				}
				if(current.getType()==Material.GLASS){
					main.optionlg.updateselectionbordure(invent, (event.getSlot()-9));
				}
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolbar(player);
				}
			}
			else if(view.getTitle().equals(main.text.getText(177))) {

				event.setCancelled(true);

				if(current.getType()==Material.ANVIL || current.getType()==Material.FEATHER){
					main.optionlg.updateselectiontimer(invent, (event.getSlot()-9));
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==1) {
					main.optionlg.selectmoinstimer(invent,600);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==2) {
					main.optionlg.selectmoinstimer(invent,60);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==3) {
					main.optionlg.selectmoinstimer(invent,10);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==5) {
					main.optionlg.selectplustimer(invent,10);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==6) {
					main.optionlg.selectplustimer(invent,60);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==7) {
					main.optionlg.selectplustimer(invent,600);
				}
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolbar(player);
				}
			}
			else if(view.getTitle().equals(main.text.getText(180))) {

				event.setCancelled(true);

				if(current.getType()==Material.PAPER) {
					main.optionlg.updateselectionsave(invent,event.getSlot());
				}
				if(current.getType()==Material.EMERALD_BLOCK || current.getType()==Material.BARRIER) {
					main.optionlg.saveorerase(invent);
				}
				if(current.getType()==Material.BED ){
					main.optionlg.load(invent);
				}
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolbar(player);
				}
			}	
		}
	}
	
}

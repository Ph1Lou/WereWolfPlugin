package io.github.ph1lou.pluginlg.listener;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener{

	private final MainLG main;
	
	public MenuListener(MainLG main) {
		this.main=main;
	}



	@EventHandler
    private void onSousMenu(InventoryClickEvent event) {
		
		InventoryView view = event.getView();
		Player player =(Player) event.getWhoClicked();
		ItemStack current = event.getCurrentItem();
		
		if (current!=null) {
			
			if(view.getTitle().equalsIgnoreCase(main.text.getText(175))) {

				event.setCancelled(true);

				if(current.getType()==Material.BEACON) {
					if(!main.isState(StateLG.LG)) {
						main.optionlg.chooseRole(player);
					}
					else player.sendMessage(main.text.getText(126));
				}
				if(current.getType()==Material.ANVIL) {
					main.optionlg.timerTool(player);
				}
				if(current.getType()==Material.MAP) {
					main.optionlg.globalTool(player);
				}
				if(current.getType()==Material.PUMPKIN) {
					main.optionlg.globalScenario(player);
				}
				if(current.getType()==Material.CHEST ) {
					main.optionlg.stuffTool(player);

				}
				if(current.getType()==Material.GLASS) {
					main.optionlg.borderTool(player);
				}
				if(current.getType()==Material.ARMOR_STAND ) {
					main.optionlg.saveTool(player);
				}

			}		
			else if(view.getTitle().equals(main.text.getText(178))){
				event.setCancelled(true);
				if(current.getType()==Material.EMERALD || current.getType()==Material.REDSTONE){
					main.config.tool_switch.put(ToolLG.values()[(event.getSlot()-9)],!main.config.tool_switch.get(ToolLG.values()[(event.getSlot()-9)]));
					main.optionlg.updateSelectionTool();
				}	
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
			}

			else if(view.getTitle().equals(main.text.getText(76))){
				event.setCancelled(true);
				if(current.getType()==Material.EMERALD || current.getType()==Material.REDSTONE){
					main.config.scenario.put(ScenarioLG.values()[(event.getSlot()-9)],!main.config.scenario.get(ScenarioLG.values()[(event.getSlot()-9)]));
					main.optionlg.updateSelectionScenario();
				}
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
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
						TextComponent msg = new TextComponent(main.text.getText(198));
						msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/adminlg stuffrole "+j));
						player.spigot().sendMessage(msg);
						player.closeInventory();
					}
					else if(event.getClick().isRightClick()){
						main.optionlg.selectMinus(event.getSlot()-9);
					}
					else{
						main.optionlg.selectPlus(event.getSlot()-9);
					}
				}
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
				if(current.getType()==Material.BARRIER) {
					main.optionlg.resetRole();
				}

			}
			else if(view.getTitle().equals(main.text.getText(179))) {

				event.setCancelled(true);

				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==3) {
					main.optionlg.selectMinusBorder();
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==5) {
					main.optionlg.selectPlusBorder();
				}
				if(current.getType()==Material.GLASS){
					main.optionlg.updateSelectionBorder(event.getSlot()-9);
				}
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
			}
			else if(view.getTitle().equals(main.text.getText(177))) {

				event.setCancelled(true);

				if(current.getType()==Material.ANVIL || current.getType()==Material.FEATHER){
					main.optionlg.updateSelectionTimer(event.getSlot()-9);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==1) {
					main.optionlg.SelectMinusTimer(600);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==2) {
					main.optionlg.SelectMinusTimer(60);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==3) {
					main.optionlg.SelectMinusTimer(10);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==5) {
					main.optionlg.selectPlusTimer(10);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==6) {
					main.optionlg.selectPlusTimer(60);
				}
				if(current.getType()==Material.STONE_BUTTON && event.getSlot()==7) {
					main.optionlg.selectPlusTimer(600);
				}
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
			}
			else if(view.getTitle().equals(main.text.getText(180))) {

				event.setCancelled(true);

				if(current.getType()==Material.PAPER) {
					main.optionlg.updateSelectionSave(event.getSlot());
				}
				if(current.getType()==Material.EMERALD_BLOCK || current.getType()==Material.BARRIER) {
					main.optionlg.saveOrErase();
				}
				if(current.getType()==Material.BED ){
					main.optionlg.load();
				}
				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
			}
			else if(view.getTitle().equals(main.text.getText(77))) {

				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}

				if(current.getType()==Material.CHEST) {
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
				if(current.getType()==Material.ENDER_CHEST) {
					player.setGameMode(GameMode.CREATIVE);
					player.getInventory().clear();
					for(ItemStack i:main.stufflg.getDeathLoot()) {
						if(i!=null) {
							player.getInventory().addItem(i);
						}
					}
					TextComponent msg = new TextComponent(main.text.getText(128));
					msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/adminlg lootdeath"));
					player.spigot().sendMessage(msg);
					player.closeInventory();
				}
				if(current.getType()==Material.EGG){
					main.stufflg.loadStuff(main, -1);
				}
				if(current.getType()==Material.GOLD_SWORD){
					main.stufflg.load(main, -2);
				}
				if(current.getType()==Material.JUKEBOX){
					main.stufflg.loadStuffStartAndDeath(main, -3);
				}
				if(current.getType()==Material.BARRIER){
					main.stufflg.loadStuffStartAndDeath(main, 666);
				}

			}
		}
	}
	
}

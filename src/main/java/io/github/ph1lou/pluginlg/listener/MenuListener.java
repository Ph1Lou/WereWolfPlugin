package io.github.ph1lou.pluginlg.listener;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.wesjd.anvilgui.AnvilGUI;
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
					main.optionlg.chooseRole(player);
				}
				else if(current.getType()==Material.ANVIL) {
					main.optionlg.timerTool(player);
				}
				else if(current.getType()==Material.MAP) {
					main.optionlg.globalTool(player);
				}
				else if(current.getType()==Material.PUMPKIN) {
					main.optionlg.scenarioTool(player);
				}
				else if(current.getType()==Material.CHEST ) {
					main.optionlg.stuffTool(player);
				}
				else if(current.getType()==Material.GLASS) {
					main.optionlg.borderTool(player);
				}
				else if(current.getType()==Material.ARMOR_STAND ) {
					main.optionlg.saveTool(player);
				}
				else if(current.getType()==Material.ENCHANTMENT_TABLE){
					main.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.WORKBENCH) {
					main.optionlg.advancedTool(player);
				}
				else if(current.getType()==Material.BANNER) {
					main.optionlg.languageTool(player);
				}

			}		
			else if(view.getTitle().equals(main.text.getText(178))){
				event.setCancelled(true);
				if(current.getType()==Material.STAINED_CLAY){
					main.config.tool_switch.put(ToolLG.values()[(event.getSlot()-9)],!main.config.tool_switch.get(ToolLG.values()[(event.getSlot()-9)]));
					main.optionlg.updateSelectionTool();
				}	
				else if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
			}

			else if(view.getTitle().equals(main.text.getText(76))){
				event.setCancelled(true);
				if(current.getType()==Material.STAINED_CLAY){
					main.config.scenario.put(ScenarioLG.values()[(event.getSlot()-9)],!main.config.scenario.get(ScenarioLG.values()[(event.getSlot()-9)]));
					main.optionlg.updateSelectionScenario();
				}
				else if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
			}
			
			else if(view.getTitle().equals(main.text.getText(176))) {

				event.setCancelled(true);


				if(current.getType()==Material.STAINED_CLAY){

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
				else if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.BARRIER) {
					main.optionlg.resetRole();
				}

			}
			else if(view.getTitle().equals(main.text.getText(179))) {

				event.setCancelled(true);

				if(current.getType()==Material.STONE_BUTTON) {
					if(event.getSlot()==3){
						main.optionlg.selectMinusBorder();
					}
					else if(event.getSlot()==5) {
						main.optionlg.selectPlusBorder();
					}
				}
				else if(current.getType()==Material.GLASS){
					main.optionlg.updateSelectionBorder(event.getSlot()-9);
				}
				else if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
			}
			else if(view.getTitle().equals(main.text.getText(177))) {

				event.setCancelled(true);

				if(current.getType()==Material.ANVIL || current.getType()==Material.FEATHER){
					main.optionlg.updateSelectionTimer(event.getSlot()-9);
				}
				else if(current.getType()==Material.STONE_BUTTON) {
					if(event.getSlot()==1){
						main.optionlg.SelectMinusTimer(600);
					}
					else if(event.getSlot()==2) {
						main.optionlg.SelectMinusTimer(60);
					}
					else if(event.getSlot()==3) {
						main.optionlg.SelectMinusTimer(10);
					}
					else if(event.getSlot()==5) {
						main.optionlg.selectPlusTimer(10);
					}
					else if(event.getSlot()==6) {
						main.optionlg.selectPlusTimer(60);
					}
					else if(event.getSlot()==7) {
						main.optionlg.selectPlusTimer(600);
					}
				}
				else if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
			}
			else if(view.getTitle().equals(main.text.getText(180))) {

				event.setCancelled(true);

				if(current.getType()==Material.PAPER) {
					main.optionlg.updateSelectionSave(event.getSlot());
				}
				else if(current.getType()==Material.EMERALD_BLOCK) {
					new AnvilGUI.Builder()

							.onComplete((player2, text) -> {
								main.optionlg.save(text,player);
								return AnvilGUI.Response.close();
							})
							.preventClose()
							.text("SaveName")
							.item(new ItemStack(Material.EMERALD_BLOCK))
							.plugin(main)
							.onClose((player2)->main.optionlg.saveTool(player))
							.open(player);
				}
				else if(current.getType()==Material.BARRIER){
					main.optionlg.erase();
				}
				else if(current.getType()==Material.BED ){
					main.optionlg.load();
				}
				else if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
			}
			else if(view.getTitle().equals(main.text.getText(77))) {

				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.CHEST) {
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
				else if(current.getType()==Material.ENDER_CHEST) {
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
				else if(current.getType()==Material.EGG){
					main.stufflg.loadStuffDefault();
				}
				else if(current.getType()==Material.GOLD_SWORD){
					main.stufflg.loadStuffMeetUP();
				}
				else if(current.getType()==Material.JUKEBOX){
					main.stufflg.loadStuffChill();
				}
				else if(current.getType()==Material.BARRIER){
					main.stufflg.clearStartLoot();
					main.stufflg.clearDeathLoot();
				}

			}
			else if(view.getTitle().equals(main.text.getText(79))) {
				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.IRON_SWORD) {
					if (event.getClick().isLeftClick()) {
						main.config.setLimitSharpnessIron(main.config.getLimitSharpnessIron()+1);
					}
					else if(main.config.getLimitSharpnessIron()>0) main.config.setLimitSharpnessIron(main.config.getLimitSharpnessIron()-1);
					main.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.DIAMOND_SWORD) {
					if (event.getClick().isLeftClick()) {
						main.config.setLimitSharpnessDiamond(main.config.getLimitSharpnessDiamond()+1);
					}
					else if(main.config.getLimitSharpnessDiamond()>0) main.config.setLimitSharpnessDiamond(main.config.getLimitSharpnessDiamond()-1);
					main.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.DIAMOND_CHESTPLATE) {
					if (event.getClick().isLeftClick()) {
						main.config.setLimitProtectionDiamond(main.config.getLimitProtectionDiamond()+1);
					}
					else if(main.config.getLimitProtectionDiamond()>0) main.config.setLimitProtectionDiamond(main.config.getLimitProtectionDiamond()-1);
					main.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.IRON_CHESTPLATE) {
					if (event.getClick().isLeftClick()) {
						main.config.setLimitProtectionIron(main.config.getLimitProtectionIron()+1);
					}
					else if(main.config.getLimitProtectionIron()>0) main.config.setLimitProtectionIron(main.config.getLimitProtectionIron()-1);
					main.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.BOW) {
					if (event.getClick().isLeftClick()) {
						main.config.setLimitPowerBow(main.config.getLimitPowerBow()+1);
					}
					else if(main.config.getLimitPowerBow()>0) main.config.setLimitPowerBow(main.config.getLimitPowerBow()-1);
					main.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.STICK) {
					if (event.getClick().isLeftClick()) {
						main.config.setLimitKnockBack((main.config.getLimitKnockBack()+1)%3);
					}
					else main.config.setLimitKnockBack((main.config.getLimitKnockBack()+2)%3);
					main.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.ARROW) {
					if (event.getClick().isLeftClick()) {
						main.config.setLimitPunch((main.config.getLimitPunch()+1)%3);
					}
					else main.config.setLimitPunch((main.config.getLimitPunch()+2)%3);
					main.optionlg.enchantmentTool(player);
				}
			}
			else if(view.getTitle().equals(main.text.getText(75))) {
				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
				else if(current.getType().equals(Material.DIAMOND)){
					if (event.getClick().isLeftClick()) {
						main.config.setDiamondLimit(main.config.getDiamondLimit()+1);
					}
					else if(main.config.getDiamondLimit()>0) main.config.setDiamondLimit(main.config.getDiamondLimit()-1);

					main.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.POTION)){
					if (event.getClick().isLeftClick()) {
						main.config.setStrengthRate(main.config.getStrengthRate()+10);
					}
					else if(main.config.getStrengthRate()-10>=0) main.config.setStrengthRate(main.config.getStrengthRate()-10);
					main.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.EXP_BOTTLE)){
					if (event.getClick().isLeftClick()) {
						main.config.setXp_boost(main.config.getXp_boost()+10);
					}
					else if(main.config.getXp_boost()-10>=0) main.config.setXp_boost(main.config.getXp_boost()-10);
					main.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.APPLE)){
					if (event.getClick().isLeftClick()) {
						if(main.config.getApple_rate()+5<=100){
							main.config.setApple_rate(main.config.getApple_rate()+5);
						}
					}
					else if(main.config.getApple_rate()-5>=0) main.config.setApple_rate(main.config.getApple_rate()-5);
					main.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.FLINT)){
					if (event.getClick().isLeftClick()) {
						if(main.config.getFlint_rate()+5<=100){
							main.config.setFlint_rate(main.config.getFlint_rate()+5);
						}
					}
					else if(main.config.getFlint_rate()-5>=0) main.config.setFlint_rate(main.config.getFlint_rate()-5);
					main.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.ENDER_PEARL)){
					if (event.getClick().isLeftClick()) {
						if(main.config.getPearl_rate()+5<=100){
							main.config.setPearl_rate(main.config.getPearl_rate()+5);
						}
					}
					else if(main.config.getPearl_rate()-5>=0) main.config.setPearl_rate(main.config.getPearl_rate()-5);
					main.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.SKULL_ITEM)){
					if (event.getClick().isLeftClick()) {
						main.config.setPlayerRequiredVoteEnd(main.config.getPlayerRequiredVoteEnd()+1);
					}
					else if(main.config.getPlayerRequiredVoteEnd()>0) main.config.setPlayerRequiredVoteEnd(main.config.getPlayerRequiredVoteEnd()-1);
					main.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.CARROT_ITEM)){
					if (event.getClick().isLeftClick()) {
						main.config.setUseOfFlair(main.config.getUseOfFlair()+1);
					}
					else if(main.config.getUseOfFlair()>0) main.config.setUseOfFlair(main.config.getUseOfFlair()-1);
					main.optionlg.advancedTool(player);
				}
			}
			else if(view.getTitle().equals(main.text.getText(74))) {

				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					main.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.BANNER){
					if(event.getSlot()==2){
						main.getConfig().set("lang","en");
						main.saveConfig();
						main.lang.getLanguage();
						player.closeInventory();
					}
					else if(event.getSlot()==4){
						main.getConfig().set("lang","fr");
						main.saveConfig();
						main.lang.getLanguage();
						player.closeInventory();
					}
					else if(event.getSlot()==6){
						main.getConfig().set("lang","custom");
						main.saveConfig();
						main.lang.getLanguage();
						player.closeInventory();
					}
				}
			}
		}
	}
	
}

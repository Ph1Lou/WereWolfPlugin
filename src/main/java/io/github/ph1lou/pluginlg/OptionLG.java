package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.Arrays;
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
	private Inventory invEnchant;
	private Inventory invAdvancedTool;
	private Inventory invLanguage;
	private final MainLG main;

	public OptionLG(MainLG main) {
		this.main=main;
	}

	public void initInv(){
		invTool = Bukkit.createInventory(null, 54,main.text.getText(175));
		invRole = Bukkit.createInventory(null, 45,main.text.getText(176));
		invTimer = Bukkit.createInventory(null, 27,main.text.getText(177));
		invConfig = Bukkit.createInventory(null, 27,main.text.getText(178));
		invScenario = Bukkit.createInventory(null, 27,main.text.getText(76));
		invBorder = Bukkit.createInventory(null, 18,main.text.getText(179));
		invSave = Bukkit.createInventory(null, 18,main.text.getText(180));
		invStuff = Bukkit.createInventory(null, 18,main.text.getText(77));
		invEnchant = Bukkit.createInventory(null, 18,main.text.getText(79));
		invAdvancedTool = Bukkit.createInventory(null, 18,main.text.getText(75));
		invLanguage = Bukkit.createInventory(null, 9,main.text.getText(74));
	}

	public void toolBar(Player player) {
		invTool.setItem(13, changeMeta(Material.BEACON,main.text.getText(176),1,null));
		invTool.setItem(22, changeMeta(Material.ANVIL,main.text.getText(177),1,null));
		invTool.setItem(30, changeMeta(Material.MAP,main.text.getText(178),1,null));
		invTool.setItem(31, changeMeta(Material.CHEST,main.text.getText(77),1,null));
		invTool.setItem(32, changeMeta(Material.GLASS,main.text.getText(179),1,null));
		invTool.setItem(48, changeMeta(Material.ARMOR_STAND,main.text.getText(180),1,null));
		invTool.setItem(29, changeMeta(Material.PUMPKIN,main.text.getText(76),1,null));
		invTool.setItem(33, changeMeta(Material.ENCHANTMENT_TABLE,main.text.getText(79),1,null));
		ItemStack custom = new ItemStack(Material.BANNER, 1);
		BannerMeta customMeta = (BannerMeta) custom.getItemMeta();
		customMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.BASE));
		customMeta.addPattern(new Pattern(DyeColor.CYAN, PatternType.STRAIGHT_CROSS));
		custom.setItemMeta(customMeta);
		invTool.setItem(45, changeMeta(custom,main.text.getText(74),null));
		invTool.setItem(50, changeMeta(Material.WORKBENCH,main.text.getText(75),1,null));
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner("Ph1Lou");
		skull.setItemMeta(skullMeta);
		invTool.setItem(53,changeMeta( skull,"Dev §bPh1Lou",null));
		int[] SlotRedGlass = {0,1,2,6,7,8,9,10,16,17,18,26,27,35,36,37,43,44,46,47,51,52};
		int[] SlotBlackGlass = {3,4,5,11,12,14,15,19,20,21,23,24,25,28,34,38,39,40,41,42,49};
		for (int slotRedGlass : SlotRedGlass) {
			invTool.setItem(slotRedGlass, changeMeta(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14), null, null));
		}
		for (int slotBlackGlass : SlotBlackGlass) {
			invTool.setItem(slotBlackGlass, changeMeta(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), null, null));
		}
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
		updateSelectionTimer( Math.max(findSelect(invBorder)-9,0));
		player.openInventory(invTimer);
	}

	public void globalTool(Player player) {
		invConfig.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionTool();
		player.openInventory(invConfig);
	}

	public void scenarioTool(Player player) {
		invScenario.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionScenario();
		player.openInventory(invScenario);
	}

	public void borderTool(Player player) {
		invBorder.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionBorder( Math.max(findSelect(invBorder)-9,0));
		player.openInventory(invBorder);
	}

	public void saveTool(Player player) {

		invSave.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		updateSelectionSave(findSelect(invSave));
		player.openInventory(invSave);
	}

	public void enchantmentTool(Player player) {
		List<String> lore = Arrays.asList(main.text.getText(203), main.text.getText(204));
		invEnchant.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		invEnchant.setItem(2,changeMeta(Material.IRON_CHESTPLATE,String.format(main.text.getText(206),main.config.getLimitProtectionIron()),1,lore));
		invEnchant.setItem(4,changeMeta(Material.DIAMOND_CHESTPLATE,String.format(main.text.getText(207),main.config.getLimitProtectionDiamond()),1,lore));
		invEnchant.setItem(6,changeMeta(Material.BOW,String.format(main.text.getText(208),main.config.getLimitPowerBow()),1,lore));
		invEnchant.setItem(11,changeMeta(Material.IRON_SWORD,String.format(main.text.getText(209),main.config.getLimitSharpnessIron()),1,lore));
		invEnchant.setItem(13,changeMeta(Material.DIAMOND_SWORD,String.format(main.text.getText(210),main.config.getLimitSharpnessDiamond()),1,lore));
		invEnchant.setItem(8,changeMeta(Material.STICK,main.text.getText(211+main.config.getLimitKnockBack()),1,null));
		invEnchant.setItem(15,changeMeta(Material.ARROW,main.text.getText(214+main.config.getLimitPunch()),1,null));

		player.openInventory(invEnchant);
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

	public void advancedTool(Player player){
		List<String> lore = Arrays.asList(main.text.getText(203), main.text.getText(204));
		invAdvancedTool.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		invAdvancedTool.setItem(2, changeMeta(Material.APPLE,String.format(main.text.getText(223),main.config.getApple_rate()),1,lore));
		invAdvancedTool.setItem(4, changeMeta(Material.FLINT,String.format(main.text.getText(224),main.config.getFlint_rate()),1,lore));
		invAdvancedTool.setItem(6, changeMeta(Material.ENDER_PEARL,String.format(main.text.getText(226),main.config.getPearl_rate()),1,lore));
		invAdvancedTool.setItem(8, changeMeta(Material.CARROT_ITEM,String.format(main.text.getText(258),main.config.getUseOfFlair()),1,lore));
		invAdvancedTool.setItem(10, changeMeta(Material.POTION,String.format(main.text.getText(225),main.config.getStrengthRate()),1,lore));
		invAdvancedTool.setItem(12, changeMeta(Material.DIAMOND,String.format(main.text.getText(228),main.config.getDiamondLimit()),1,lore));
		invAdvancedTool.setItem(14, changeMeta(Material.EXP_BOTTLE,String.format(main.text.getText(229),main.config.getXp_boost()),1,lore));
		invAdvancedTool.setItem(16, changeMeta(new ItemStack(Material.SKULL_ITEM,1,(short) 3),String.format(main.text.getText(227),main.config.getPlayerRequiredVoteEnd()),lore));
		player.openInventory(invAdvancedTool);
	}

	public void languageTool(Player player){
		invLanguage.setItem(0, changeMeta(Material.COMPASS,main.text.getText(170),1,null));
		ItemStack fr = new ItemStack(Material.BANNER, 1);
		BannerMeta frMeta = (BannerMeta) fr.getItemMeta();
		frMeta.addPattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_LEFT));
		frMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_CENTER));
		frMeta.addPattern(new Pattern(DyeColor.RED, PatternType.STRIPE_RIGHT));
		fr.setItemMeta(frMeta);

		ItemStack custom = new ItemStack(Material.BANNER, 1);
		BannerMeta customMeta = (BannerMeta) custom.getItemMeta();
		customMeta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.BASE));
		custom.setItemMeta(customMeta);

		ItemStack en = new ItemStack(Material.BANNER, 1);
		BannerMeta enMeta = (BannerMeta) en.getItemMeta();
		enMeta.addPattern(new Pattern(DyeColor.BLUE,PatternType.BASE));
		enMeta.addPattern(new Pattern(DyeColor.WHITE,PatternType.STRIPE_DOWNLEFT));
		enMeta.addPattern(new Pattern(DyeColor.WHITE,PatternType.STRIPE_DOWNRIGHT));
		enMeta.addPattern(new Pattern(DyeColor.WHITE,PatternType.STRIPE_DOWNRIGHT));
		enMeta.addPattern(new Pattern(DyeColor.RED,PatternType.CROSS));
		enMeta.addPattern(new Pattern(DyeColor.WHITE,PatternType.STRIPE_CENTER));
		enMeta.addPattern(new Pattern(DyeColor.WHITE,PatternType.STRIPE_MIDDLE));
		enMeta.addPattern(new Pattern(DyeColor.RED,PatternType.STRAIGHT_CROSS));
		en.setItemMeta(enMeta);
		invLanguage.setItem(2,changeMeta(en,"English", Collections.singletonList("By Jormunth")));
		invLanguage.setItem(4,changeMeta(fr,"Français", Collections.singletonList("Par Ph1Lou")));
		invLanguage.setItem(6,changeMeta(custom,"Custom",null));
		player.openInventory(invLanguage);
	}

	public void updateSelectionSave(int j) {

		File repertoire = new File(main.getDataFolder()+"/configs/");
		File[] files=repertoire.listFiles();
		if (files==null) return;
		j--;
		if(j>=files.length ){
			j=Math.min(7, files.length-1);
		}
		if(j<0){
			j=0;
		}
		for(int i=0;i<8;i++) {

			if(i>=Math.min(files.length,8)){
				invSave.setItem(i+1,null);
			}
			else if(i==j) {
				invSave.setItem(i+1, changeMeta(Material.FEATHER,String.format(main.text.getText(174),files[i].getName()),1,null));
			}
			else invSave.setItem(i+1, changeMeta(Material.PAPER,String.format(main.text.getText(174),files[i].getName()),1,null));
		}

		invSave.setItem(9, changeMeta(Material.EMERALD_BLOCK,main.text.getText(173),1,null));
		if(files.length!=0){
			invSave.setItem(14, changeMeta(Material.BARRIER,String.format(main.text.getText(171),files[j].getName()),1,null));
			invSave.setItem(12, changeMeta(Material.BED,String.format(main.text.getText(172),files[j].getName()),1,null));
		}
		else {
			invSave.setItem(12,null);
			invSave.setItem(14,null);
		}

	}

	public void load() {
		int j= findSelect(invSave)-1;
		File repertoire = new File(main.getDataFolder()+"/configs/");
		File[] files=repertoire.listFiles();
		if (files==null) return;
		if(j<0 || j>=files.length) return;
		main.config.getConfig(main, files[j].getName().replace(".json",""));
		main.stufflg.load(files[j].getName().replace(".json",""));
		updateSelectionSave(j+1);
	}

	public void save(String saveName, Player player)  {
		File file = new File(main.getDataFolder()+"/configs/", saveName+".json");
		File repertoire = new File(main.getDataFolder()+"/configs/");
		File[] files=repertoire.listFiles();
		if(files==null || files.length<8){
			main.filelg.save(file, main.serialize.serialize(main.config));
			main.stufflg.save(saveName);
			player.sendMessage(main.text.getText(56));
			updateSelectionSave(findSelect(invSave));
		}
		else player.sendMessage(main.text.getText(57));
	}

	public void erase()  {
		File repertoire = new File(main.getDataFolder()+"/configs/");
		File[] files=repertoire.listFiles();
		if(files==null) return;
		int i=findSelect(invSave)-1;
		if(i<0 || i>=files.length) return;

		File file = new File(main.getDataFolder()+"/configs/", files[i].getName());
		if(!file.delete()){
			Bukkit.getConsoleSender().sendMessage(String.format(main.text.getText(58),files[i].getName()));
		}
		file = new File(main.getDataFolder()+"/stuffs/", files[i].getName().replaceFirst(".json",".yml"));
		if(!file.delete()){
			Bukkit.getConsoleSender().sendMessage(String.format(main.text.getText(58),files[i].getName().replaceFirst(".json",".yml")));
		}
		updateSelectionSave(findSelect(invSave));
	}

	public ItemStack changeMeta(ItemStack item, String item_name, List<String> lore) {
		ItemMeta meta1 = item.getItemMeta();
		meta1.setDisplayName(item_name);
		meta1.setLore(lore);
		item.setItemMeta(meta1);
		return item;
	}

	public ItemStack changeMeta(Material m, String item_name, int i, List<String> lore) {
		ItemStack item = new ItemStack(m,i);
		return changeMeta(item,item_name,lore);
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
			i=0;
		}
		return i;
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
		int i = Math.max(findSelect(invBorder)-9,0) ;
		int j = main.config.border_value.get(BorderLG.values()[i]);
		if(j>=100) {
			main.config.border_value.put(BorderLG.values()[i],j-100);
			updateSelectionBorder(i);
		}
	}

	public void selectPlusBorder() {
		int i =  Math.max(findSelect(invBorder)-9,0) ;
		int j = main.config.border_value.get(BorderLG.values()[i]);
		main.config.border_value.put(BorderLG.values()[i],j+100);
		updateSelectionBorder(i);
	}

	public void SelectMinusTimer(int v) {
		int i =  Math.max(findSelect(invTimer)-9,0);
		int j = main.config.value.get(TimerLG.values()[i]);
		if(j>=v) {
			main.config.value.put(TimerLG.values()[i],j-v);
			updateSelectionTimer(i);
		}
	}

	public void selectPlusTimer(int v) {
		int i =  Math.max(findSelect(invTimer)-9,0);
		main.config.value.put(TimerLG.values()[i],main.config.value.get(TimerLG.values()[i])+v);
		updateSelectionTimer(i);
	}



	public void updateSelection(){

		List<String> lore = Arrays.asList(main.text.getText(203), main.text.getText(204), main.text.getText(205));
		for (int i=0;i<RoleLG.values().length;i++) {

			if (main.config.role_count.get(RoleLG.values()[i])>0) {
				invRole.setItem(9+i, changeMeta(new ItemStack(Material.STAINED_CLAY, main.config.role_count.get(RoleLG.values()[i]), (short)5),main.text.translateRole.get(RoleLG.values()[i]),lore));
			}
			else invRole.setItem(9+i, changeMeta(new ItemStack(Material.STAINED_CLAY,1, (short)6),main.text.translateRole.get(RoleLG.values()[i]),lore));
		}
	}

	public void updateSelectionTimer(){
		updateSelectionTimer( Math.max(findSelect(invTimer)-9,0));
	}

	public void updateSelectionTimer(int j){

		String c= main.score.conversion(main.config.value.get(TimerLG.values()[j]));

		invTimer.setItem(1, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"-10m",c),1,null));
		invTimer.setItem(2, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"-1m",c),1,null));
		invTimer.setItem(3, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"-10s",c),1,null));
		invTimer.setItem(4, changeMeta(Material.BEACON,String.format(main.text.translateTimer.get(TimerLG.values()[j]),c),1,null));
		invTimer.setItem(5, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"+10s",c),1,null));
		invTimer.setItem(6, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"+1m",c),1,null));
		invTimer.setItem(7, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"+10m",c),1,null));

		for (int i=0;i<TimerLG.values().length;i++) {
			if(i==j) {
				invTimer.setItem(9+i, changeMeta(Material.FEATHER,String.format(main.text.translateTimer.get(TimerLG.values()[i]),c),1,null));
			}
			else invTimer.setItem(9+i, changeMeta(Material.ANVIL,String.format(main.text.translateTimer.get(TimerLG.values()[i]),main.score.conversion(main.config.value.get(TimerLG.values()[i]))),1,null));
		}
	}

	public void updateSelectionBorder(){
		updateSelectionBorder( Math.max(findSelect(invBorder)-9,0));
	}

	public void updateSelectionBorder(int j) {
		invBorder.setItem(3, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"-",main.config.border_value.get(BorderLG.values()[j])),1,null));
		invBorder.setItem(4, changeMeta(Material.BEACON,String.format(main.text.translateBorder.get(BorderLG.values()[j]),main.config.border_value.get(BorderLG.values()[j])),1,null));
		invBorder.setItem(5, changeMeta(Material.STONE_BUTTON,String.format(main.text.getText(88),"+",main.config.border_value.get(BorderLG.values()[j])),1,null));
		for (int i = 0; i< BorderLG.values().length; i++) {
			if(i==j) {
				invBorder.setItem(9+i, changeMeta(Material.FEATHER,String.format(main.text.translateBorder.get(BorderLG.values()[i]),main.config.border_value.get(BorderLG.values()[i])),1,null));
			}
			else invBorder.setItem(9+i, changeMeta(Material.GLASS,String.format(main.text.translateBorder.get(BorderLG.values()[i]),main.config.border_value.get(BorderLG.values()[i])),1,null));
		}
	}

	public void updateSelectionTool(){
		for (int i = 0; i< ToolLG.values().length; i++) {
			if (main.config.tool_switch.get(ToolLG.values()[i])) {
				invConfig.setItem(9+i, changeMeta(new ItemStack(Material.STAINED_CLAY,1, (short)5),main.text.translateTool.get(ToolLG.values()[i]), Collections.singletonList(String.format(main.text.getText(169), ""))));
			}
			else invConfig.setItem(9+i, changeMeta(new ItemStack(Material.STAINED_CLAY,1, (short)6),main.text.translateTool.get(ToolLG.values()[i]),Collections.singletonList(String.format(main.text.getText(168), ""))));
		}
		updateCompass();
	}

	public void updateSelectionScenario() {
		for (int i = 0; i< ScenarioLG.values().length; i++) {
			if (main.config.scenario.get(ScenarioLG.values()[i])) {
				invScenario.setItem(9+i, changeMeta(new ItemStack(Material.STAINED_CLAY,1, (short)5),main.text.translateScenario.get(ScenarioLG.values()[i]), Collections.singletonList(String.format(main.text.getText(169), ""))));
			}
			else invScenario.setItem(9+i, changeMeta(new ItemStack(Material.STAINED_CLAY,1, (short)6),main.text.translateScenario.get(ScenarioLG.values()[i]), Collections.singletonList(String.format(main.text.getText(168), ""))));
		}
		updateNameTag();
	}
	public void updateCompass(){

		for(Player player:Bukkit.getOnlinePlayers()){
			if(main.config.tool_switch.get(ToolLG.COMPASS_MIDDLE)){
				player.setCompassTarget(player.getWorld().getSpawnLocation());
			}
			else if(main.playerLG.containsKey(player.getName())){
				player.setCompassTarget(main.playerLG.get(player.getName()).getSpawn());
			}
		}
	}

	public void updateNameTag() {

		for(String playername : main.playerLG.keySet()) {

			Scoreboard board = main.playerLG.get(playername).getScoreBoard();

			if(!board.equals(main.board)){

				for(String players : main.playerLG.keySet()) {

					if(board.getTeam(players)==null){
						board.registerNewTeam(players);
						board.getTeam(players).addEntry(players);
					}
					if(main.config.scenario.get(ScenarioLG.NO_NAME_TAG)){
						board.getTeam(players).setNameTagVisibility(NameTagVisibility.NEVER);
					}
					else {
						if(!main.playerLG.get(players).hasPower() && (main.playerLG.get(players).isRole(RoleLG.LOUP_PERFIDE) || main.playerLG.get(players).isRole(RoleLG.PETITE_FILLE))){
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
				if(!main.playerLG.get(playername).hasPower() && (main.playerLG.get(playername).isRole(RoleLG.LOUP_PERFIDE) || main.playerLG.get(playername).isRole(RoleLG.PETITE_FILLE))){
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


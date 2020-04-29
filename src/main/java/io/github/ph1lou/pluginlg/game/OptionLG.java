package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.enumlg.*;
import io.github.ph1lou.pluginlg.savelg.FileLG;
import io.github.ph1lou.pluginlg.savelg.SerializerLG;
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
import java.util.UUID;

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
	private Inventory invWhiteList;
	private final GameManager game;

	public OptionLG(GameManager game) {
		this.game=game;
	}

	public void initInv(){
		invTool = Bukkit.createInventory(null, 54, game.text.getText(175));
		invRole = Bukkit.createInventory(null, 45, game.text.getText(176));
		invTimer = Bukkit.createInventory(null, 27, game.text.getText(177));
		invConfig = Bukkit.createInventory(null, 27, game.text.getText(178));
		invScenario = Bukkit.createInventory(null, 36, game.text.getText(76));
		invBorder = Bukkit.createInventory(null, 18, game.text.getText(179));
		invSave = Bukkit.createInventory(null, 18, game.text.getText(180));
		invStuff = Bukkit.createInventory(null, 18, game.text.getText(77));
		invEnchant = Bukkit.createInventory(null, 18, game.text.getText(79));
		invAdvancedTool = Bukkit.createInventory(null, 36, game.text.getText(75));
		invLanguage = Bukkit.createInventory(null, 9, game.text.getText(74));
		invWhiteList = Bukkit.createInventory(null, 18, game.text.getText(70));
	}

	public void toolBar(Player player) {

		invTool.setItem(0, changeMeta(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), game.text.getText(70), null));

		invTool.setItem(13, changeMeta(Material.BEACON,game.text.getText(176),1,null));
		invTool.setItem(22, changeMeta(Material.ANVIL,game.text.getText(177),1,null));
		invTool.setItem(30, changeMeta(Material.MAP,game.text.getText(178),1,null));
		invTool.setItem(31, changeMeta(Material.CHEST,game.text.getText(77),1,null));
		invTool.setItem(32, changeMeta(Material.GLASS,game.text.getText(179),1,null));
		invTool.setItem(48, changeMeta(Material.ARMOR_STAND,game.text.getText(180),1,null));
		invTool.setItem(29, changeMeta(Material.PUMPKIN,game.text.getText(76),1,null));
		invTool.setItem(33, changeMeta(Material.ENCHANTMENT_TABLE,game.text.getText(79),1,null));
		ItemStack custom = new ItemStack(Material.BANNER, 1);
		BannerMeta customMeta = (BannerMeta) custom.getItemMeta();
		customMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.BASE));
		customMeta.addPattern(new Pattern(DyeColor.CYAN, PatternType.STRAIGHT_CROSS));
		custom.setItemMeta(customMeta);
		invTool.setItem(45, changeMeta(custom,game.text.getText(74),null));
		invTool.setItem(50, changeMeta(Material.WORKBENCH,game.text.getText(75),1,null));
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner("Ph1Lou");
		skull.setItemMeta(skullMeta);
		invTool.setItem(53,changeMeta( skull,"Dev §bPh1Lou",null));
		int[] SlotRedGlass = {1,2,6,7,8,9,10,16,17,18,26,27,35,36,37,43,44,46,47,51,52};
		int[] SlotBlackGlass = {3,4,5,11,12,14,15,19,20,21,23,24,25,28,34,38,39,40,41,42,49};
		for (int slotRedGlass : SlotRedGlass) {
			invTool.setItem(slotRedGlass, changeMeta(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14), null, null));
		}
		for (int slotBlackGlass : SlotBlackGlass) {
			invTool.setItem(slotBlackGlass, changeMeta(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), null, null));
		}
		player.openInventory(invTool);
	}

	public void whiteListTool(Player player) {

		invWhiteList.setItem(0, changeMeta(Material.COMPASS, game.text.getText(170), 1, null));
		invWhiteList.setItem(2, changeMeta(new ItemStack(Material.EMPTY_MAP, 1), game.text.getText(63), Collections.singletonList(String.format(game.text.getText(game.isWhiteList() ? 312 : 313), ""))));
		invWhiteList.setItem(10, changeMeta(new ItemStack(Material.SKULL_ITEM, 1), game.text.getText(64), Collections.singletonList(game.text.getText(309 + game.getSpectatorMode()))));
		invWhiteList.setItem(12, changeMeta(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), String.format(game.text.getText(69), game.getPlayerMax()), null));
		player.openInventory(invWhiteList);

	}


	public void chooseRole(Player player) {
		invRole.setItem(0, changeMeta(Material.COMPASS,game.text.getText(170),1,null));
		invRole.setItem(8, changeMeta(Material.BARRIER,game.text.getText(183),1,null));
		updateSelection();
		player.openInventory(invRole);
	}

	public void timerTool(Player player) {
		invTimer.setItem(0, changeMeta(Material.COMPASS,game.text.getText(170),1,null));
		updateSelectionTimer( Math.max(findSelect(invBorder)-9,0));
		player.openInventory(invTimer);
	}

	public void globalTool(Player player) {
		invConfig.setItem(0, changeMeta(Material.COMPASS,game.text.getText(170),1,null));
		updateSelectionTool();
		player.openInventory(invConfig);
	}

	public void scenarioTool(Player player) {
		invScenario.setItem(0, changeMeta(Material.COMPASS,game.text.getText(170),1,null));
		updateSelectionScenario();
		player.openInventory(invScenario);
	}

	public void borderTool(Player player) {
		invBorder.setItem(0, changeMeta(Material.COMPASS,game.text.getText(170),1,null));
		updateSelectionBorder( Math.max(findSelect(invBorder)-9,0));
		player.openInventory(invBorder);
	}

	public void saveTool(Player player) {

		invSave.setItem(0, changeMeta(Material.COMPASS,game.text.getText(170),1,null));
		updateSelectionSave(findSelect(invSave));
		player.openInventory(invSave);
	}

	public void enchantmentTool(Player player) {
		List<String> lore = Arrays.asList(game.text.getText(203), game.text.getText(204));
		invEnchant.setItem(0, changeMeta(Material.COMPASS,game.text.getText(170),1,null));
		invEnchant.setItem(2,changeMeta(Material.IRON_CHESTPLATE,String.format(game.text.getText(206),game.config.getLimitProtectionIron()),1,lore));
		invEnchant.setItem(4,changeMeta(Material.DIAMOND_CHESTPLATE,String.format(game.text.getText(207),game.config.getLimitProtectionDiamond()),1,lore));
		invEnchant.setItem(6,changeMeta(Material.BOW,String.format(game.text.getText(208),game.config.getLimitPowerBow()),1,lore));
		invEnchant.setItem(11,changeMeta(Material.IRON_SWORD,String.format(game.text.getText(209),game.config.getLimitSharpnessIron()),1,lore));
		invEnchant.setItem(13,changeMeta(Material.DIAMOND_SWORD,String.format(game.text.getText(210),game.config.getLimitSharpnessDiamond()),1,lore));
		invEnchant.setItem(8,changeMeta(Material.STICK,game.text.getText(211+game.config.getLimitKnockBack()),1,null));
		invEnchant.setItem(15,changeMeta(Material.ARROW,game.text.getText(214+game.config.getLimitPunch()),1,null));

		player.openInventory(invEnchant);
	}

	public void stuffTool(Player player) {
		invStuff.setItem(0, changeMeta(Material.COMPASS,game.text.getText(170),1,null));
		invStuff.setItem(2, changeMeta(Material.EGG,game.text.getText(83),1,null));
		invStuff.setItem(4, changeMeta(Material.GOLD_SWORD,game.text.getText(84),1,null));
		invStuff.setItem(6, changeMeta(Material.JUKEBOX,game.text.getText(85),1,null));
		invStuff.setItem(10, changeMeta(Material.BARRIER,game.text.getText(86),1,null));
		invStuff.setItem(13, changeMeta(Material.CHEST,game.text.getText(182),1,null));
		invStuff.setItem(16, changeMeta(Material.ENDER_CHEST,game.text.getText(181),1,null));
		player.openInventory(invStuff);
	}

	public void advancedTool(Player player) {
		List<String> lore = Arrays.asList(game.text.getText(203), game.text.getText(204));
		invAdvancedTool.setItem(0, changeMeta(Material.COMPASS, game.text.getText(170), 1, null));
		invAdvancedTool.setItem(2, changeMeta(Material.APPLE, String.format(game.text.getText(223), game.config.getAppleRate()), 1, lore));
		invAdvancedTool.setItem(4, changeMeta(Material.FLINT, String.format(game.text.getText(224), game.config.getFlintRate()), 1, lore));
		invAdvancedTool.setItem(6, changeMeta(Material.ENDER_PEARL, String.format(game.text.getText(226), game.config.getPearlRate()), 1, lore));
		invAdvancedTool.setItem(8, changeMeta(Material.CARROT_ITEM, String.format(game.text.getText(258), game.config.getUseOfFlair()), 1, lore));
		invAdvancedTool.setItem(10, changeMeta(new ItemStack(Material.POTION, 1, (short) 8201), String.format(game.text.getText(225), game.config.getStrengthRate()), lore));
		invAdvancedTool.setItem(12, changeMeta(Material.DIAMOND, String.format(game.text.getText(228), game.config.getDiamondLimit()), 1, lore));
		invAdvancedTool.setItem(14, changeMeta(Material.EXP_BOTTLE, String.format(game.text.getText(229), game.config.getXpBoost()), 1, lore));
		invAdvancedTool.setItem(16, changeMeta(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), String.format(game.text.getText(227), game.config.getPlayerRequiredVoteEnd()), lore));
		invAdvancedTool.setItem(18, changeMeta(Material.GOLD_NUGGET, game.text.getText(261), 1, Collections.singletonList(game.text.getText(262 + game.config.getGoldenAppleParticles()))));
		invAdvancedTool.setItem(20, changeMeta(new ItemStack(Material.WOOL, 1, (short) 1), String.format(game.text.getText(270), game.config.getDistanceFox()), lore));
		invAdvancedTool.setItem(22, changeMeta(new ItemStack(Material.WOOL, 1, (short) 12), String.format(game.text.getText(271), game.config.getDistanceBearTrainer()), lore));
		invAdvancedTool.setItem(24, changeMeta(new ItemStack(Material.POTION, 1, (short) 8227), String.format(game.text.getText(115), game.config.getResistanceRate()), lore));
		invAdvancedTool.setItem(26, changeMeta(new ItemStack(Material.BREAD, 1), String.format(game.config.isTrollSV() ? game.text.getText(169) : game.text.getText(168), game.text.getText(73)), null));
		invAdvancedTool.setItem(28, changeMeta(new ItemStack(Material.WOOL, 1, (short) 6), String.format(game.text.getText(316), game.config.getDistanceSuccubus()), lore));
		player.openInventory(invAdvancedTool);
	}

	public void languageTool(Player player){
		invLanguage.setItem(0, changeMeta(Material.COMPASS,game.text.getText(170),1,null));
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

		File repertoire = new File(game.getDataFolder()+"/configs/");
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
				invSave.setItem(i+1, changeMeta(Material.FEATHER,String.format(game.text.getText(174),files[i].getName()),1,null));
			}
			else invSave.setItem(i+1, changeMeta(Material.PAPER,String.format(game.text.getText(174),files[i].getName()),1,null));
		}

		invSave.setItem(9, changeMeta(Material.EMERALD_BLOCK,game.text.getText(173),1,null));
		if(files.length!=0){
			invSave.setItem(14, changeMeta(Material.BARRIER,String.format(game.text.getText(171),files[j].getName()),1,null));
			invSave.setItem(12, changeMeta(Material.BED,String.format(game.text.getText(172),files[j].getName()),1,null));
		}
		else {
			invSave.setItem(12,null);
			invSave.setItem(14,null);
		}

	}

	public void load() {
		int j= findSelect(invSave)-1;
		File repertoire = new File(game.getDataFolder()+"/configs/");
		File[] files=repertoire.listFiles();
		if (files==null) return;
		if(j<0 || j>=files.length) return;
		game.config.getConfig(game, files[j].getName().replace(".json",""));
		game.stufflg.load(files[j].getName().replace(".json",""));
		updateSelectionSave(j+1);
	}

	public void save(String saveName, Player player)  {
		File file = new File(game.getDataFolder()+"/configs/", saveName+".json");
		File repertoire = new File(game.getDataFolder()+"/configs/");
		File[] files=repertoire.listFiles();
		if(files==null || files.length<8){
			FileLG.save(file, SerializerLG.serialize(game.config));
			game.stufflg.save(saveName);
			player.sendMessage(game.text.getText(56));
			updateSelectionSave(findSelect(invSave));
		}
		else player.sendMessage(game.text.getText(57));
	}

	public void erase()  {
		File repertoire = new File(game.getDataFolder()+"/configs/");
		File[] files=repertoire.listFiles();
		if(files==null) return;
		int i=findSelect(invSave)-1;
		if(i<0 || i>=files.length) return;

		File file = new File(game.getDataFolder()+"/configs/", files[i].getName());
		if(!file.delete()){
			Bukkit.getConsoleSender().sendMessage(String.format(game.text.getText(58),files[i].getName()));
		}
		file = new File(game.getDataFolder()+"/stuffs/", files[i].getName().replaceFirst(".json",".yml"));
		if(!file.delete()){
			Bukkit.getConsoleSender().sendMessage(String.format(game.text.getText(58),files[i].getName().replaceFirst(".json",".yml")));
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

		if(!game.isState(StateLG.LG)) {
			int j = game.config.roleCount.get(RoleLG.values()[i]);
			if (j > 0) {
				if (RoleLG.values()[i].getCamp() != null) {
					game.score.setRole(game.score.getRole() - 1);
				}
				game.config.roleCount.put(RoleLG.values()[i], j - 1);
				game.score.updateBoard();
				updateSelection();
			}
		}
	}

	public void selectPlus(int i) {

		if(!game.isState(StateLG.LG)) {
			int j = game.config.roleCount.get(RoleLG.values()[i]);
			game.config.roleCount.put(RoleLG.values()[i], j + 1);
			if (RoleLG.values()[i].getCamp() != null) {
				game.score.setRole(game.score.getRole() + 1);
			}
			game.score.updateBoard();
			updateSelection();
		}
	}

	public void selectMinusBorder() {
		int i = Math.max(findSelect(invBorder) - 9, 0);
		int j = game.config.borderValues.get(BorderLG.values()[i]);
		if (j >= 100) {
			game.config.borderValues.put(BorderLG.values()[i], j - 100);
			updateSelectionBorder(i);
		}
	}

	public void selectPlusBorder() {
		int i = Math.max(findSelect(invBorder) - 9, 0);
		int j = game.config.borderValues.get(BorderLG.values()[i]);
		game.config.borderValues.put(BorderLG.values()[i], j + 100);
		updateSelectionBorder(i);
	}

	public void SelectMinusTimer(int v) {
		int i = Math.max(findSelect(invTimer) - 9, 0);
		int j = game.config.timerValues.get(TimerLG.values()[i]);
		if (j >= v) {
			game.config.timerValues.put(TimerLG.values()[i], j - v);
			updateSelectionTimer(i);
		}
	}

	public void selectPlusTimer(int v) {
		int i = Math.max(findSelect(invTimer) - 9, 0);
		game.config.timerValues.put(TimerLG.values()[i], game.config.timerValues.get(TimerLG.values()[i]) + v);
		updateSelectionTimer(i);
	}



	public void updateSelection(){

		List<String> lore = Arrays.asList(game.text.getText(203), game.text.getText(204), game.text.getText(205));
		for (int i=0;i<RoleLG.values().length;i++) {

			if (game.config.roleCount.get(RoleLG.values()[i]) > 0) {
				invRole.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, game.config.roleCount.get(RoleLG.values()[i]), (short) 5), game.text.translateRole.get(RoleLG.values()[i]), lore));
			} else
				invRole.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, 1, (short) 6), game.text.translateRole.get(RoleLG.values()[i]), lore));
		}
	}

	public void updateSelectionTimer(){
		updateSelectionTimer( Math.max(findSelect(invTimer)-9,0));
	}

	public void updateSelectionTimer(int j) {

		String c = game.score.conversion(game.config.timerValues.get(TimerLG.values()[j]));

		invTimer.setItem(1, changeMeta(Material.STONE_BUTTON, String.format(game.text.getText(88), "-10m", c), 1, null));
		invTimer.setItem(2, changeMeta(Material.STONE_BUTTON, String.format(game.text.getText(88), "-1m", c), 1, null));
		invTimer.setItem(3, changeMeta(Material.STONE_BUTTON, String.format(game.text.getText(88), "-10s", c), 1, null));
		invTimer.setItem(4, changeMeta(Material.BEACON, String.format(game.text.translateTimer.get(TimerLG.values()[j]), c), 1, null));
		invTimer.setItem(5, changeMeta(Material.STONE_BUTTON, String.format(game.text.getText(88), "+10s", c), 1, null));
		invTimer.setItem(6, changeMeta(Material.STONE_BUTTON, String.format(game.text.getText(88), "+1m", c), 1, null));
		invTimer.setItem(7, changeMeta(Material.STONE_BUTTON, String.format(game.text.getText(88), "+10m", c), 1, null));

		for (int i = 0; i < TimerLG.values().length; i++) {
			if (i == j) {
				invTimer.setItem(9 + i, changeMeta(Material.FEATHER, String.format(game.text.translateTimer.get(TimerLG.values()[i]), c), 1, null));
			} else
				invTimer.setItem(9 + i, changeMeta(Material.ANVIL, String.format(game.text.translateTimer.get(TimerLG.values()[i]), game.score.conversion(game.config.timerValues.get(TimerLG.values()[i]))), 1, null));
		}
	}

	public void updateSelectionBorder(){
		updateSelectionBorder( Math.max(findSelect(invBorder)-9,0));
	}

	public void updateSelectionBorder(int j) {
		invBorder.setItem(3, changeMeta(Material.STONE_BUTTON, String.format(game.text.getText(88), "-", game.config.borderValues.get(BorderLG.values()[j])), 1, null));
		invBorder.setItem(4, changeMeta(Material.BEACON, String.format(game.text.translateBorder.get(BorderLG.values()[j]), game.config.borderValues.get(BorderLG.values()[j])), 1, null));
		invBorder.setItem(5, changeMeta(Material.STONE_BUTTON, String.format(game.text.getText(88), "+", game.config.borderValues.get(BorderLG.values()[j])), 1, null));
		for (int i = 0; i < BorderLG.values().length; i++) {
			if (i == j) {
				invBorder.setItem(9 + i, changeMeta(Material.FEATHER, String.format(game.text.translateBorder.get(BorderLG.values()[i]), game.config.borderValues.get(BorderLG.values()[i])), 1, null));
			} else
				invBorder.setItem(9 + i, changeMeta(Material.GLASS, String.format(game.text.translateBorder.get(BorderLG.values()[i]), game.config.borderValues.get(BorderLG.values()[i])), 1, null));
		}
	}

	public void updateSelectionTool() {
		for (int i = 0; i < ToolLG.values().length; i++) {
			if (game.config.configValues.get(ToolLG.values()[i])) {
				invConfig.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, 1, (short) 5), game.text.translateTool.get(ToolLG.values()[i]), Collections.singletonList(String.format(game.text.getText(169), ""))));
			} else
				invConfig.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, 1, (short) 6), game.text.translateTool.get(ToolLG.values()[i]), Collections.singletonList(String.format(game.text.getText(168), ""))));
		}
		updateCompass();
		if (game.config.timerValues.get(TimerLG.LG_LIST) < 0) {
			for (String playerName : game.playerLG.keySet()) {
				if (game.playerLG.get(playerName).isCamp(Camp.LG) || game.playerLG.get(playerName).isRole(RoleLG.LOUP_GAROU_BLANC)) {
					if (game.config.configValues.get(ToolLG.RED_NAME_TAG)) {
						game.board.getTeam(playerName).setPrefix("§4");
					} else game.board.getTeam(playerName).setPrefix("");
				}
			}
		}
	}

	public void updateSelectionScenario() {
		for (int i = 0; i < ScenarioLG.values().length; i++) {
			if (game.config.scenarioValues.get(ScenarioLG.values()[i])) {
				invScenario.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, 1, (short) 5), game.text.translateScenario.get(ScenarioLG.values()[i]), Collections.singletonList(String.format(game.text.getText(169), ""))));
			} else
				invScenario.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, 1, (short) 6), game.text.translateScenario.get(ScenarioLG.values()[i]), Collections.singletonList(String.format(game.text.getText(168), ""))));
		}
		updateNameTag();
		game.scenarios.update();
	}
	public void updateCompass(){

		for(Player player:Bukkit.getOnlinePlayers()) {
			if(game.playerLG.containsKey(player.getName())){
				if (game.config.configValues.get(ToolLG.COMPASS_MIDDLE)) {
					player.setCompassTarget(player.getWorld().getSpawnLocation());
				} else {
					player.setCompassTarget(game.playerLG.get(player.getName()).getSpawn());
				}
			}
		}
	}

	public void updateNameTag() {

		for (String playerName : game.playerLG.keySet()) {

			Scoreboard board = game.playerLG.get(playerName).getScoreBoard();

			if (!board.equals(game.board)) {

				if (board.getTeam("moderators") == null) {
					board.registerNewTeam("moderators");
					board.getTeam("moderators").setPrefix("§1[Modo]§r ");
				}
				for (String players : game.playerLG.keySet()) {

					if (board.getTeam(players) == null) {
						board.registerNewTeam(players);
						board.getTeam(players).addEntry(players);
					}
					if (game.config.scenarioValues.get(ScenarioLG.NO_NAME_TAG)) {
						board.getTeam(players).setNameTagVisibility(NameTagVisibility.NEVER);
					} else {
						if (!game.playerLG.get(players).hasPower() && (game.playerLG.get(players).isRole(RoleLG.LOUP_PERFIDE) || game.playerLG.get(players).isRole(RoleLG.PETITE_FILLE))) {
							board.getTeam(players).setNameTagVisibility(NameTagVisibility.NEVER);
						} else board.getTeam(players).setNameTagVisibility(NameTagVisibility.ALWAYS);
					}
					if(board.getTeam("moderators").hasEntry(players)){
						board.getTeam("moderators").removeEntry(players);
					}
				}
				for (UUID uuid: game.getModerators()) {
					if(Bukkit.getPlayer(uuid)!=null){
						String players = Bukkit.getPlayer(uuid).getName();
						if(!board.getTeam("moderators").hasEntry(players)){
							board.getTeam("moderators").addEntry(players);
						}
					}
				}

			}

			if (game.config.scenarioValues.get(ScenarioLG.NO_NAME_TAG)) {
				game.board.getTeam(playerName).setNameTagVisibility(NameTagVisibility.NEVER);
			} else {
				if (!game.playerLG.get(playerName).hasPower() && (game.playerLG.get(playerName).isRole(RoleLG.LOUP_PERFIDE) || game.playerLG.get(playerName).isRole(RoleLG.PETITE_FILLE))) {
					game.board.getTeam(playerName).setNameTagVisibility(NameTagVisibility.NEVER);
				} else {
					game.board.getTeam(playerName).setNameTagVisibility(NameTagVisibility.ALWAYS);
				}
			}
			if(game.board.getTeam("moderators").hasEntry(playerName)){
				game.board.getTeam("moderators").removeEntry(playerName);
			}
		}
		for (UUID uuid: game.getModerators()) {
			if(Bukkit.getPlayer(uuid)!=null){
				String players = Bukkit.getPlayer(uuid).getName();
				if(!game.board.getTeam("moderators").hasEntry(players)){
					game.board.getTeam("moderators").addEntry(players);
				}
			}
		}
	}

	public void resetRole() {
		for (int i=0;i<RoleLG.values().length;i++) {
			game.config.roleCount.put(RoleLG.values()[i], 0);
		}
		game.score.setRole(0);
		updateSelection();
		game.score.updateBoard();
	}


}


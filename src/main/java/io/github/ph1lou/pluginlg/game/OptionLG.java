package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.classesroles.InvisibleState;
import io.github.ph1lou.pluginlg.classesroles.villageroles.LittleGirl;
import io.github.ph1lou.pluginlg.classesroles.werewolfroles.MischievousWereWolf;
import io.github.ph1lou.pluginlg.savelg.FileLG;
import io.github.ph1lou.pluginlg.savelg.SerializerLG;
import io.github.ph1lou.pluginlgapi.enumlg.*;
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
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.*;

public class OptionLG {

	private final Inventory invTool;
	private final Inventory invRole;
	private final Inventory invTimer;
	private final Inventory invBorder;
	private final Inventory invScenario;
	private final Inventory invStuff;
	private final Inventory invSave;
	private final Inventory invConfig;
	private final Inventory invEnchant;
	private final Inventory invAdvancedTool;
	private final Inventory invLanguage;
	private final Inventory invWhiteList;
	private final GameManager game;

	public OptionLG(GameManager game) {
		this.game=game;
		invTool = Bukkit.createInventory(null, 54, game.translate("werewolf.menu.name"));
		invRole = Bukkit.createInventory(null, 54, game.translate("werewolf.menu.roles.name"));
		invTimer = Bukkit.createInventory(null, 27, game.translate("werewolf.menu.timers.name"));
		invConfig = Bukkit.createInventory(null, 27, game.translate("werewolf.menu.global.name"));
		invScenario = Bukkit.createInventory(null, 36, game.translate("werewolf.menu.scenarios.name"));
		invBorder = Bukkit.createInventory(null, 18, game.translate("werewolf.menu.border.name"));
		invSave = Bukkit.createInventory(null, 18, game.translate("werewolf.menu.save.name"));
		invStuff = Bukkit.createInventory(null, 18, game.translate("werewolf.menu.stuff.name"));
		invEnchant = Bukkit.createInventory(null, 18, game.translate("werewolf.menu.enchantments.name"));
		invAdvancedTool = Bukkit.createInventory(null, 36, game.translate("werewolf.menu.advanced_tool.name"));
		invLanguage = Bukkit.createInventory(null, 9, game.translate("werewolf.menu.languages.name"));
		invWhiteList = Bukkit.createInventory(null, 18, game.translate("werewolf.menu.whitelist.name"));
	}

	public boolean isConfigInventory(Inventory inventory){
		List<Inventory> inventoryList= new ArrayList<>(Arrays.asList(invTool,invRole,invTimer,invConfig,invScenario,invBorder,invSave,invStuff,invEnchant,invAdvancedTool,invLanguage,invWhiteList));
		return inventoryList.contains(inventory);
	}

	public void toolBar(Player player) {

		invTool.setItem(0, changeMeta(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), game.translate("werewolf.menu.whitelist.name"), null));

		invTool.setItem(13, changeMeta(Material.BEACON,game.translate("werewolf.menu.roles.name"),1,null));
		invTool.setItem(22, changeMeta(Material.ANVIL,game.translate("werewolf.menu.timers.name"),1,null));
		invTool.setItem(30, changeMeta(Material.MAP,game.translate("werewolf.menu.global.name"),1,null));
		invTool.setItem(31, changeMeta(Material.CHEST,game.translate("werewolf.menu.stuff.name"),1,null));
		invTool.setItem(32, changeMeta(Material.GLASS,game.translate("werewolf.menu.border.name"),1,null));
		invTool.setItem(48, changeMeta(Material.ARMOR_STAND,game.translate("werewolf.menu.save.name"),1,null));
		invTool.setItem(29, changeMeta(Material.PUMPKIN,game.translate("werewolf.menu.scenarios.name"),1,null));
		invTool.setItem(33, changeMeta(Material.ENCHANTMENT_TABLE,game.translate("werewolf.menu.enchantments.name"),1,null));
		ItemStack custom = new ItemStack(Material.BANNER, 1);
		BannerMeta customMeta = (BannerMeta) custom.getItemMeta();
		customMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.BASE));
		customMeta.addPattern(new Pattern(DyeColor.CYAN, PatternType.STRAIGHT_CROSS));
		custom.setItemMeta(customMeta);
		invTool.setItem(45, changeMeta(custom,game.translate("werewolf.menu.languages.name"),null));
		invTool.setItem(50, changeMeta(Material.WORKBENCH,game.translate("werewolf.menu.advanced_tool.name"),1,null));
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

		invWhiteList.setItem(0, changeMeta(Material.COMPASS, game.translate("werewolf.menu.return"), 1, null));
		invWhiteList.setItem(2, changeMeta(new ItemStack(Material.EMPTY_MAP, 1), game.isWhiteList()?game.translate("werewolf.menu.whitelist.close"):game.translate("werewolf.menu.whitelist.open"), null));
		invWhiteList.setItem(10, changeMeta(new ItemStack(Material.SKULL_ITEM, 1), game.translate("werewolf.menu.whitelist.spectator_mode"), Collections.singletonList(Arrays.asList(game.translate("werewolf.menu.whitelist.disable"),game.translate("werewolf.menu.whitelist.death_only"),game.translate("werewolf.menu.whitelist.enable")).get(game.getSpectatorMode()))));
		invWhiteList.setItem(12, changeMeta(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), game.translate("werewolf.menu.whitelist.max", game.getPlayerMax()), null));
		player.openInventory(invWhiteList);

	}


	public void chooseRole(Player player) {
		invRole.setItem(0, changeMeta(Material.COMPASS,game.translate("werewolf.menu.return"),1,null));
		invRole.setItem(8, changeMeta(Material.BARRIER,game.translate("werewolf.menu.roles.zero"),1,null));
		updateSelection();
		player.openInventory(invRole);
	}

	public void timerTool(Player player) {
		invTimer.setItem(0, changeMeta(Material.COMPASS,game.translate("werewolf.menu.return"),1,null));
		updateSelectionTimer( Math.max(findSelect(invBorder)-9,0));
		player.openInventory(invTimer);
	}

	public void globalTool(Player player) {
		invConfig.setItem(0, changeMeta(Material.COMPASS,game.translate("werewolf.menu.return"),1,null));
		updateSelectionTool();
		player.openInventory(invConfig);
	}

	public void scenarioTool(Player player) {
		invScenario.setItem(0, changeMeta(Material.COMPASS,game.translate("werewolf.menu.return"),1,null));
		updateSelectionScenario();
		player.openInventory(invScenario);
	}

	public void borderTool(Player player) {
		invBorder.setItem(0, changeMeta(Material.COMPASS,game.translate("werewolf.menu.return"),1,null));
		updateSelectionBorder();
		player.openInventory(invBorder);
	}

	public void saveTool(Player player) {

		invSave.setItem(0, changeMeta(Material.COMPASS,game.translate("werewolf.menu.return"),1,null));
		updateSelectionSave(findSelect(invSave));
		player.openInventory(invSave);
	}

	public void enchantmentTool(Player player) {
		List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"), game.translate("werewolf.menu.right"));
		invEnchant.setItem(0, changeMeta(Material.COMPASS,game.translate("werewolf.menu.return"),1,null));
		invEnchant.setItem(2,changeMeta(Material.IRON_CHESTPLATE,game.translate("werewolf.menu.enchantments.iron_protection",game.config.getLimitProtectionIron()),1,lore));
		invEnchant.setItem(4,changeMeta(Material.DIAMOND_CHESTPLATE,game.translate("werewolf.menu.enchantments.diamond_protection",game.config.getLimitProtectionDiamond()),1,lore));
		invEnchant.setItem(6,changeMeta(Material.BOW,game.translate("werewolf.menu.enchantments.power",game.config.getLimitPowerBow()),1,lore));
		invEnchant.setItem(11,changeMeta(Material.IRON_SWORD,game.translate("werewolf.menu.enchantments.sharpness_iron",game.config.getLimitSharpnessIron()),1,lore));
		invEnchant.setItem(13,changeMeta(Material.DIAMOND_SWORD,game.translate("werewolf.menu.enchantments.sharpness_diamond",game.config.getLimitSharpnessDiamond()),1,lore));
		invEnchant.setItem(8,changeMeta(Material.STICK,Arrays.asList(game.translate("werewolf.menu.enchantments.knock_back_disable"),game.translate("werewolf.menu.enchantments.knock_back_invisible"),game.translate("werewolf.menu.enchantments.knock_back_enable")).get(game.config.getLimitKnockBack()),1,null));
		invEnchant.setItem(15,changeMeta(Material.ARROW,Arrays.asList(game.translate("werewolf.menu.enchantments.punch_disable"),game.translate("werewolf.menu.enchantments.punch_cupid"),game.translate("werewolf.menu.enchantments.punch_enable")).get(game.config.getLimitPunch()),1,null));

		player.openInventory(invEnchant);
	}

	public void stuffTool(Player player) {
		invStuff.setItem(0, changeMeta(Material.COMPASS,game.translate("werewolf.menu.return"),1,null));
		invStuff.setItem(2, changeMeta(Material.EGG,game.translate("werewolf.menu.stuff.normal"),1,null));
		invStuff.setItem(4, changeMeta(Material.GOLD_SWORD,game.translate("werewolf.menu.stuff.meet_up"),1,null));
		invStuff.setItem(6, changeMeta(Material.JUKEBOX,game.translate("werewolf.menu.stuff.chill"),1,null));
		invStuff.setItem(10, changeMeta(Material.BARRIER,game.translate("werewolf.menu.stuff.delete"),1,null));
		invStuff.setItem(13, changeMeta(Material.CHEST,game.translate("werewolf.menu.stuff.start"),1,null));
		invStuff.setItem(16, changeMeta(Material.ENDER_CHEST,game.translate("werewolf.menu.stuff.death"),1,null));
		player.openInventory(invStuff);
	}

	public void advancedTool(Player player) {
		List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"), game.translate("werewolf.menu.right"));
		invAdvancedTool.setItem(0, changeMeta(Material.COMPASS, game.translate("werewolf.menu.return"), 1, null));
		invAdvancedTool.setItem(2, changeMeta(Material.APPLE, game.translate("werewolf.menu.advanced_tool.apple", game.config.getAppleRate()), 1, lore));
		invAdvancedTool.setItem(4, changeMeta(Material.FLINT, game.translate("werewolf.menu.advanced_tool.flint", game.config.getFlintRate()), 1, lore));
		invAdvancedTool.setItem(6, changeMeta(Material.ENDER_PEARL, game.translate("werewolf.menu.advanced_tool.ender_pearl", game.config.getPearlRate()), 1, lore));
		invAdvancedTool.setItem(8, changeMeta(Material.CARROT_ITEM, game.translate("werewolf.menu.advanced_tool.fox_smell_number", game.config.getUseOfFlair()), 1, lore));
		invAdvancedTool.setItem(10, changeMeta(new ItemStack(Material.POTION, 1, (short) 8201), game.translate("werewolf.menu.advanced_tool.strength", game.config.getStrengthRate()), lore));
		invAdvancedTool.setItem(12, changeMeta(Material.DIAMOND, game.translate("werewolf.menu.advanced_tool.diamond", game.config.getDiamondLimit()), 1, lore));
		invAdvancedTool.setItem(14, changeMeta(Material.EXP_BOTTLE, game.translate("werewolf.menu.advanced_tool.xp", game.config.getXpBoost()), 1, lore));
		invAdvancedTool.setItem(16, changeMeta(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), game.translate("werewolf.menu.advanced_tool.vote", game.config.getPlayerRequiredVoteEnd()), lore));
		invAdvancedTool.setItem(18, changeMeta(Material.GOLD_NUGGET, game.translate("werewolf.menu.advanced_tool.particles"), 1, Collections.singletonList(Arrays.asList(game.translate("werewolf.menu.advanced_tool.particles_off"),game.translate("werewolf.menu.advanced_tool.exception"),game.translate("werewolf.menu.advanced_tool.particles_on")).get(game.config.getGoldenAppleParticles()))));
		invAdvancedTool.setItem(20, changeMeta(new ItemStack(Material.WOOL, 1, (short) 1), game.translate("werewolf.menu.advanced_tool.fox", game.config.getDistanceFox()), lore));
		invAdvancedTool.setItem(22, changeMeta(new ItemStack(Material.WOOL, 1, (short) 12), game.translate("werewolf.menu.advanced_tool.bear_trainer", game.config.getDistanceBearTrainer()), lore));
		invAdvancedTool.setItem(24, changeMeta(new ItemStack(Material.POTION, 1, (short) 8227), game.translate("werewolf.menu.advanced_tool.resistance", game.config.getResistanceRate()), lore));
		invAdvancedTool.setItem(26, changeMeta(new ItemStack(Material.BREAD, 1), game.translate(game.config.isTrollSV() ?"werewolf.menu.advanced_tool.troll_on" : "werewolf.menu.advanced_tool.troll_off"), null));
		invAdvancedTool.setItem(28, changeMeta(new ItemStack(Material.WOOL, 1, (short) 6), game.translate("werewolf.menu.advanced_tool.succubus", game.config.getDistanceSuccubus()), lore));
		player.openInventory(invAdvancedTool);
	}

	public void languageTool(Player player){
		invLanguage.setItem(0, changeMeta(Material.COMPASS,game.translate("werewolf.menu.return"),1,null));
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
				invSave.setItem(i+1, changeMeta(Material.FEATHER,game.translate("werewolf.menu.save.configuration",files[i].getName()),1,null));
			}
			else invSave.setItem(i+1, changeMeta(Material.PAPER,game.translate("werewolf.menu.save.configuration",files[i].getName()),1,null));
		}

		invSave.setItem(9, changeMeta(Material.EMERALD_BLOCK,game.translate("werewolf.menu.save.new"),1,null));
		if(files.length!=0){
			invSave.setItem(14, changeMeta(Material.BARRIER,game.translate("werewolf.menu.save.delete",files[j].getName()),1,null));
			invSave.setItem(12, changeMeta(Material.BED, game.translate("werewolf.menu.save.load",files[j].getName()),1,null));
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
			player.sendMessage(game.translate("werewolf.menu.save.success"));
			updateSelectionSave(findSelect(invSave));
		}
		else player.sendMessage(game.translate("werewolf.menu.save.failure"));
	}

	public void erase()  {
		File repertoire = new File(game.getDataFolder()+"/configs/");
		File[] files=repertoire.listFiles();
		if(files==null) return;
		int i=findSelect(invSave)-1;
		if(i<0 || i>=files.length) return;

		File file = new File(game.getDataFolder()+"/configs/", files[i].getName());
		if(!file.delete()){
			Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.menu.save.delete_failed",files[i].getName()));
		}
		file = new File(game.getDataFolder()+"/stuffs/", files[i].getName().replaceFirst(".json",".yml"));
		if(!file.delete()){
			Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.menu.save.delete_failed",files[i].getName().replaceFirst(".json",".yml")));
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

		if(!game.isState(StateLG.GAME)) {
			int j = game.config.getRoleCount().get(RoleLG.values()[i]);
			if (j > 0) {
				if (!RoleLG.values()[i].equals(RoleLG.CURSED_LOVER) && !RoleLG.values()[i].equals(RoleLG.LOVER) && !RoleLG.values()[i].equals(RoleLG.AMNESIAC_LOVER)) {
					game.score.setRole(game.score.getRole() - 1);
				}
				game.config.getRoleCount().put(RoleLG.values()[i], j - 1);
				game.score.updateBoard();
				updateSelection();
			}
		}
	}

	public void selectPlus(int i) {

		if(!game.isState(StateLG.GAME)) {
			int j = game.config.getRoleCount().get(RoleLG.values()[i]);
			game.config.getRoleCount().put(RoleLG.values()[i], j + 1);
			if (!RoleLG.values()[i].equals(RoleLG.CURSED_LOVER) && !RoleLG.values()[i].equals(RoleLG.LOVER) && !RoleLG.values()[i].equals(RoleLG.AMNESIAC_LOVER)) {
				game.score.setRole(game.score.getRole() + 1);
			}
			game.score.updateBoard();
			updateSelection();
		}
	}

	public void SelectMinusTimer(int v) {
		int i = Math.max(findSelect(invTimer) - 9, 0);
		int j = game.config.getTimerValues().get(TimerLG.values()[i]);
		if (j >= v) {
			game.config.getTimerValues().put(TimerLG.values()[i], j - v);
			updateSelectionTimer(i);
		}
	}

	public void selectPlusTimer(int v) {
		int i = Math.max(findSelect(invTimer) - 9, 0);
		game.config.getTimerValues().put(TimerLG.values()[i], game.config.getTimerValues().get(TimerLG.values()[i]) + v);
		updateSelectionTimer(i);
	}



	public void updateSelection(){

		List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"), game.translate("werewolf.menu.right"),game.translate("werewolf.menu.shift"));
		for (int i=0;i<RoleLG.values().length;i++) {

			if (game.config.getRoleCount().get(RoleLG.values()[i]) > 0) {
				invRole.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, game.config.getRoleCount().get(RoleLG.values()[i]), (short) 5), game.translate(RoleLG.values()[i].getKey()), lore));
			} else
				invRole.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, 1, (short) 6), game.translate(RoleLG.values()[i].getKey()), lore));
		}
	}

	public void updateSelectionTimer(){
		updateSelectionTimer( Math.max(findSelect(invTimer)-9,0));
	}

	public void updateSelectionTimer(int j) {

		String c = game.score.conversion(game.config.getTimerValues().get(TimerLG.values()[j]));

		invTimer.setItem(1, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "-10m", c), 1, null));
		invTimer.setItem(2, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "-1m", c), 1, null));
		invTimer.setItem(3, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "-10s", c), 1, null));
		invTimer.setItem(4, changeMeta(Material.BEACON, game.translate(TimerLG.values()[j].getKey(), c), 1, null));
		invTimer.setItem(5, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "+10s", c), 1, null));
		invTimer.setItem(6, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "+1m", c), 1, null));
		invTimer.setItem(7, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "+10m", c), 1, null));

		for (int i = 0; i < TimerLG.values().length; i++) {
			if (i == j) {
				invTimer.setItem(9 + i, changeMeta(Material.FEATHER, game.translate(TimerLG.values()[i].getKey(), c), 1, null));
			} else
				invTimer.setItem(9 + i, changeMeta(Material.ANVIL, game.translate(TimerLG.values()[i].getKey(), game.score.conversion(game.config.getTimerValues().get(TimerLG.values()[i]))), 1, null));
		}
	}


	public void updateSelectionTool() {
		for (int i = 0; i < ToolLG.values().length; i++) {
			if (game.config.getConfigValues().get(ToolLG.values()[i])) {
				invConfig.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, 1, (short) 5), game.translate(ToolLG.values()[i].getKey()), Collections.singletonList(game.translate("werewolf.utils.enable", ""))));
			} else
				invConfig.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, 1, (short) 6), game.translate(ToolLG.values()[i].getKey()), Collections.singletonList(game.translate("werewolf.utils.disable", ""))));
		}
		updateCompass();
		if (game.config.getTimerValues().get(TimerLG.WEREWOLF_LIST) < 0) {
			for (UUID uuid : game.playerLG.keySet()) {
				PlayerLG plg = game.playerLG.get(uuid);
				if (game.roleManage.isWereWolf(plg)) {
					if (game.config.getConfigValues().get(ToolLG.RED_NAME_TAG)) {
						game.board.getTeam(plg.getName()).setPrefix("§4");
					} else game.board.getTeam(plg.getName()).setPrefix("");
				}
			}
		}
	}

	public void updateSelectionScenario() {
		for (int i = 0; i < ScenarioLG.values().length; i++) {
			if (game.config.getScenarioValues().get(ScenarioLG.values()[i])) {
				invScenario.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, 1, (short) 5), game.translate(ScenarioLG.values()[i].getKey()), Collections.singletonList(game.translate("werewolf.utils.enable", ""))));
			} else
				invScenario.setItem(9 + i, changeMeta(new ItemStack(Material.STAINED_CLAY, 1, (short) 6), game.translate(ScenarioLG.values()[i].getKey()), Collections.singletonList(game.translate("werewolf.utils.disable", ""))));
		}
		updateNameTag();
		game.scenarios.update();
	}
	public void updateCompass(){

		for(Player player:Bukkit.getOnlinePlayers()) {
			if(game.playerLG.containsKey(player.getUniqueId())){
				if (game.config.getConfigValues().get(ToolLG.COMPASS_MIDDLE)) {
					player.setCompassTarget(player.getWorld().getSpawnLocation());
				} else {
					player.setCompassTarget(game.playerLG.get(player.getUniqueId()).getSpawn());
				}
			}
		}
	}

	public void updateNameTag() {

		for (UUID playerUUID : game.playerLG.keySet()) {

			PlayerLG plg = game.playerLG.get(playerUUID);
			Scoreboard board = plg.getScoreBoard();
			String name = plg.getName();
			if(game.board.getTeam(name)==null){
				game.board.registerNewTeam(name);
				game.board.getTeam(name).addEntry(name);
			}
			Team team = game.board.getTeam(name);

			if (!board.equals(game.board)) {

				for (UUID uuid2 : game.playerLG.keySet()) {

					PlayerLG plg2 = game.playerLG.get(uuid2);
					String name2 =plg2.getName();

					if (board.getTeam(name2) == null) {
						board.registerNewTeam(name2);
						board.getTeam(name2).addEntry(name2);
					}

					Team team2 = board.getTeam(name2);

					if (game.config.getScenarioValues().get(ScenarioLG.NO_NAME_TAG)) {
						team2.setNameTagVisibility(NameTagVisibility.NEVER);
					} else {
						if ((plg2.getRole() instanceof MischievousWereWolf || plg2.getRole() instanceof LittleGirl) && ((InvisibleState)plg2.getRole()).isInvisible()) {
							team2.setNameTagVisibility(NameTagVisibility.NEVER);
						} else team2.setNameTagVisibility(NameTagVisibility.ALWAYS);
					}
				}

				for (UUID uuid: game.getModerators()) {
					if(Bukkit.getPlayer(uuid)!=null){
						String name3 = Bukkit.getPlayer(uuid).getName();
						if(board.getTeam(name3)==null){
							board.registerNewTeam(name3);
							board.getTeam(name3).addEntry(name3);
						}
					}
				}

				for(Team t:board.getTeams()){

					for(String e:t.getEntries()){
						if(Bukkit.getPlayer(e)!=null){
							UUID uuid=Bukkit.getPlayer(e).getUniqueId();
							if(game.getHosts().contains(uuid)){
								t.setPrefix(game.translate("werewolf.commands.admin.host.tag"));
							}
							else if (game.getModerators().contains(uuid)){
								t.setPrefix(game.translate("werewolf.commands.admin.moderator.tag"));
							}
							else t.setPrefix("");
						}
					}
				}
			}

			if (game.config.getScenarioValues().get(ScenarioLG.NO_NAME_TAG)) {
				team.setNameTagVisibility(NameTagVisibility.NEVER);
			} else {
				if ((plg.getRole() instanceof MischievousWereWolf || plg.getRole() instanceof LittleGirl) && ((InvisibleState)plg.getRole()).isInvisible()) {
					team.setNameTagVisibility(NameTagVisibility.NEVER);
				} else {
					team.setNameTagVisibility(NameTagVisibility.ALWAYS);
				}
			}
		}

		for (UUID uuid: game.getModerators()) {
			if(Bukkit.getPlayer(uuid)!=null){
				String name3 = Bukkit.getPlayer(uuid).getName();
				if(game.board.getTeam(name3)==null){
					game.board.registerNewTeam(name3);
					game.board.getTeam(name3).addEntry(name3);
				}
			}
		}

		for(Team t:game.board.getTeams()){

			for(String e:t.getEntries()){
				if(Bukkit.getPlayer(e)!=null){
					UUID uuid=Bukkit.getPlayer(e).getUniqueId();
					if(game.getHosts().contains(uuid)){
						if(game.roleManage.isWereWolf(uuid) && game.config.getTimerValues().get(TimerLG.WEREWOLF_LIST) < 0 && game.config.getConfigValues().get(ToolLG.RED_NAME_TAG)){
							t.setPrefix(game.translate("werewolf.commands.admin.host.tag")+"§4");
						}
						else t.setPrefix(game.translate("werewolf.commands.admin.host.tag"));
					}
					else if (game.getModerators().contains(uuid)){
						t.setPrefix(game.translate("werewolf.commands.admin.moderator.tag"));
					}
					else if(game.roleManage.isWereWolf(uuid) && game.config.getTimerValues().get(TimerLG.WEREWOLF_LIST) < 0 && game.config.getConfigValues().get(ToolLG.RED_NAME_TAG)){
						t.setPrefix("§4");
					}
					else t.setPrefix("");
				}
			}
		}
	}



	public void resetRole() {
		for (int i=0;i<RoleLG.values().length;i++) {
			game.config.getRoleCount().put(RoleLG.values()[i], 0);
		}
		game.score.setRole(0);
		updateSelection();
		game.score.updateBoard();
	}


	public void updateSelectionBorder() {
		invBorder.setItem(3, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "-", game.config.getBorderMax()), 1, null));
		invBorder.setItem(4, changeMeta(Material.GLASS,game.translate("werewolf.menu.border.radius_border_max",game.config.getBorderMax()), 1, null));
		invBorder.setItem(5, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "+", game.config.getBorderMax()), 1, null));
		invBorder.setItem(12, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "-", game.config.getBorderMin()), 1, null));
		invBorder.setItem(13, changeMeta(Material.GLASS,game.translate("werewolf.menu.border.radius_border_min",game.config.getBorderMin()), 1, null));
		invBorder.setItem(14, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "+", game.config.getBorderMin()), 1, null));
	}
}


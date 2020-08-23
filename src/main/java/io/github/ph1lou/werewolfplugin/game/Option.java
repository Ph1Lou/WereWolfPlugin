package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.ConfigRegister;
import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfapi.ScenarioRegister;
import io.github.ph1lou.werewolfapi.TimerRegister;
import io.github.ph1lou.werewolfapi.enumlg.Category;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.save.FileUtils_;
import io.github.ph1lou.werewolfplugin.save.Serializer;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Option {

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
	private final Main main;

	public void setCategory(Category category) {
		this.category = category;
	}

	private Category category = Category.VILLAGER;

	public Option(Main main) {
		this.main = main;
		this.game = main.getCurrentGame();
		invTool = Bukkit.createInventory(null, 54, game.translate("werewolf.menu.name"));
		invRole = Bukkit.createInventory(null, 54, game.translate("werewolf.menu.roles.name"));
		invTimer = Bukkit.createInventory(null, Math.min(54, (main.getRegisterTimers().size() / 9 + 2) * 9), game.translate("werewolf.menu.timers.name"));
		invConfig = Bukkit.createInventory(null, Math.min(54, (main.getRegisterConfigs().size() / 9 + 2) * 9), game.translate("werewolf.menu.global.name"));
		invScenario = Bukkit.createInventory(null, Math.min(54, (main.getRegisterScenarios().size() / 9 + 2) * 9), game.translate("werewolf.menu.scenarios.name"));
		invBorder = Bukkit.createInventory(null, 18, game.translate("werewolf.menu.border.name"));
		invSave = Bukkit.createInventory(null, 18, game.translate("werewolf.menu.save.name"));
		invStuff = Bukkit.createInventory(null, 18, game.translate("werewolf.menu.stuff.name"));
		invEnchant = Bukkit.createInventory(null, 18, game.translate("werewolf.menu.enchantments.name"));
		invAdvancedTool = Bukkit.createInventory(null, 36, game.translate("werewolf.menu.advanced_tool.name"));
		invLanguage = Bukkit.createInventory(null, 9, game.translate("werewolf.menu.languages.name"));
		invWhiteList = Bukkit.createInventory(null, 18, game.translate("werewolf.menu.whitelist.name"));
	}

	public boolean isConfigInventory(Inventory inventory){
		List<Inventory> inventoryList= Arrays.asList(invTool,invRole,invTimer,invConfig,invScenario,invBorder,invSave,invStuff,invEnchant,invAdvancedTool,invLanguage,invWhiteList);
		return inventoryList.contains(inventory);
	}

	public void toolBar(Player player) {

		invTool.setItem(0, changeMeta(UniversalMaterial.PLAYER_HEAD.getStack(), game.translate("werewolf.menu.whitelist.name"), null));
		invTool.setItem(13, changeMeta(Material.BEACON, game.translate("werewolf.menu.roles.name"), 1, null));
		invTool.setItem(22, changeMeta(Material.ANVIL, game.translate("werewolf.menu.timers.name"), 1, null));
		invTool.setItem(30, changeMeta(Material.MAP, game.translate("werewolf.menu.global.name"), 1, null));
		invTool.setItem(31, changeMeta(Material.CHEST, game.translate("werewolf.menu.stuff.name"), 1, null));
		invTool.setItem(32, changeMeta(Material.GLASS, game.translate("werewolf.menu.border.name"), 1, null));
		invTool.setItem(48, changeMeta(Material.ARMOR_STAND, game.translate("werewolf.menu.save.name"), 1, null));
		invTool.setItem(29, changeMeta(Material.PUMPKIN, game.translate("werewolf.menu.scenarios.name"), 1, null));
		invTool.setItem(33, changeMeta(UniversalMaterial.ENCHANTING_TABLE.getType(), game.translate("werewolf.menu.enchantments.name"), 1, null));
		ItemStack custom = UniversalMaterial.WHITE_BANNER.getStack();
		BannerMeta customMeta = (BannerMeta) custom.getItemMeta();
		if (customMeta != null) {
			customMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.BASE));
			customMeta.addPattern(new Pattern(DyeColor.CYAN, PatternType.STRAIGHT_CROSS));
			custom.setItemMeta(customMeta);
		}

		invTool.setItem(45, changeMeta(custom, game.translate("werewolf.menu.languages.name"), null));
		invTool.setItem(50, changeMeta(UniversalMaterial.CRAFTING_TABLE.getType(), game.translate("werewolf.menu.advanced_tool.name"), 1, null));
		ItemStack skull = UniversalMaterial.PLAYER_HEAD.getStack();
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		VersionUtils.getVersionUtils().setSkullOwner(skullMeta, Bukkit.getOfflinePlayer(UUID.fromString("056be797-2a0b-4807-9af5-37faf5384396")), "Ph1Lou");
		skull.setItemMeta(skullMeta);
		invTool.setItem(53, changeMeta(skull, "Dev §bPh1Lou", null));
		int[] SlotRedGlass = {1, 2, 6, 7, 8, 9, 10, 16, 17, 18, 26, 27, 35, 36, 37, 43, 44, 46, 47, 51, 52};
		int[] SlotBlackGlass = {3, 4, 5, 11, 12, 14, 15, 19, 20, 21, 23, 24, 25, 28, 34, 38, 39, 40, 41, 42, 49};
		for (int slotRedGlass : SlotRedGlass) {
			invTool.setItem(slotRedGlass, changeMeta(UniversalMaterial.RED_STAINED_GLASS_PANE.getStack(), null, null));
		}
		for (int slotBlackGlass : SlotBlackGlass) {
			invTool.setItem(slotBlackGlass, changeMeta(UniversalMaterial.BLACK_STAINED_GLASS_PANE.getStack(), null, null));
		}
		player.openInventory(invTool);
	}

	public void whiteListTool(Player player) {

		invWhiteList.setItem(0, changeMeta(Material.COMPASS, game.translate("werewolf.menu.return"), 1, null));
		invWhiteList.setItem(2, changeMeta(UniversalMaterial.MAP.getType(), game.isWhiteList() ? game.translate("werewolf.menu.whitelist.close") : game.translate("werewolf.menu.whitelist.open"), 1, null));
		invWhiteList.setItem(10, changeMeta(UniversalMaterial.SKELETON_SKULL.getStack(), game.translate("werewolf.menu.whitelist.spectator_mode"), Collections.singletonList(Arrays.asList(game.translate("werewolf.menu.whitelist.disable"), game.translate("werewolf.menu.whitelist.death_only"), game.translate("werewolf.menu.whitelist.enable")).get(game.getSpectatorMode()))));
		invWhiteList.setItem(12, changeMeta(UniversalMaterial.PLAYER_HEAD.getStack(), game.translate("werewolf.menu.whitelist.max", game.getPlayerMax()), null));
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
		updateSelectionTimer(Math.max(findSelect(invTimer)-9,0));
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
		invEnchant.setItem(2,changeMeta(Material.IRON_CHESTPLATE,game.translate("werewolf.menu.enchantments.iron_protection",game.getConfig().getLimitProtectionIron()),1,lore));
		invEnchant.setItem(4,changeMeta(Material.DIAMOND_CHESTPLATE,game.translate("werewolf.menu.enchantments.diamond_protection",game.getConfig().getLimitProtectionDiamond()),1,lore));
		invEnchant.setItem(6,changeMeta(Material.BOW,game.translate("werewolf.menu.enchantments.power",game.getConfig().getLimitPowerBow()),1,lore));
		invEnchant.setItem(11,changeMeta(Material.IRON_SWORD,game.translate("werewolf.menu.enchantments.sharpness_iron",game.getConfig().getLimitSharpnessIron()),1,lore));
		invEnchant.setItem(13,changeMeta(Material.DIAMOND_SWORD,game.translate("werewolf.menu.enchantments.sharpness_diamond",game.getConfig().getLimitSharpnessDiamond()),1,lore));
		invEnchant.setItem(8,changeMeta(Material.STICK,Arrays.asList(game.translate("werewolf.menu.enchantments.knock_back_disable"),game.translate("werewolf.menu.enchantments.knock_back_invisible"),game.translate("werewolf.menu.enchantments.knock_back_enable")).get(game.getConfig().getLimitKnockBack()),1,null));
		invEnchant.setItem(15,changeMeta(Material.ARROW,Arrays.asList(game.translate("werewolf.menu.enchantments.punch_disable"),game.translate("werewolf.menu.enchantments.punch_cupid"),game.translate("werewolf.menu.enchantments.punch_enable")).get(game.getConfig().getLimitPunch()),1,null));
		invEnchant.setItem(17,changeMeta(UniversalMaterial.OAK_BOAT.getType(),game.translate("werewolf.menu.enchantments.depth_rider",game.getConfig().getLimitDepthStrider()),1,lore));

		player.openInventory(invEnchant);
	}

	public void stuffTool(Player player) {
		invStuff.setItem(0, changeMeta(Material.COMPASS, game.translate("werewolf.menu.return"), 1, null));
		invStuff.setItem(2, changeMeta(Material.EGG, game.translate("werewolf.menu.stuff.normal"), 1, null));
		invStuff.setItem(4, changeMeta(UniversalMaterial.GOLDEN_SWORD.getType(), game.translate("werewolf.menu.stuff.meet_up"), 1, null));
		invStuff.setItem(6, changeMeta(Material.JUKEBOX, game.translate("werewolf.menu.stuff.chill"), 1, null));
		invStuff.setItem(10, changeMeta(Material.BARRIER, game.translate("werewolf.menu.stuff.delete"), 1, null));
		invStuff.setItem(13, changeMeta(Material.CHEST, game.translate("werewolf.menu.stuff.start"), 1, null));
		invStuff.setItem(16, changeMeta(Material.ENDER_CHEST, game.translate("werewolf.menu.stuff.death"), 1, null));
		player.openInventory(invStuff);
	}

	public void advancedTool(Player player) {
		List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"), game.translate("werewolf.menu.right"));
		invAdvancedTool.setItem(0, changeMeta(Material.COMPASS, game.translate("werewolf.menu.return"), 1, null));
		invAdvancedTool.setItem(2, changeMeta(Material.APPLE, game.translate("werewolf.menu.advanced_tool.apple", game.getConfig().getAppleRate()), 1, lore));
		invAdvancedTool.setItem(4, changeMeta(Material.FLINT, game.translate("werewolf.menu.advanced_tool.flint", game.getConfig().getFlintRate()), 1, lore));
		invAdvancedTool.setItem(6, changeMeta(Material.ENDER_PEARL, game.translate("werewolf.menu.advanced_tool.ender_pearl", game.getConfig().getPearlRate()), 1, lore));
		invAdvancedTool.setItem(8, changeMeta(UniversalMaterial.CARROT.getType(), game.translate("werewolf.menu.advanced_tool.fox_smell_number", game.getConfig().getUseOfFlair()), 1, lore));
		invAdvancedTool.setItem(10, changeMeta(new ItemStack(Material.POTION, 1, (short) 8201), game.translate("werewolf.menu.advanced_tool.strength", game.getConfig().getStrengthRate()), lore));
		invAdvancedTool.setItem(12, changeMeta(Material.DIAMOND, game.translate("werewolf.menu.advanced_tool.diamond", game.getConfig().getDiamondLimit()), 1, lore));
		invAdvancedTool.setItem(14, changeMeta(UniversalMaterial.EXPERIENCE_BOTTLE.getType(), game.translate("werewolf.menu.advanced_tool.xp", game.getConfig().getXpBoost()), 1, lore));
		invAdvancedTool.setItem(16, changeMeta(UniversalMaterial.PLAYER_HEAD.getStack(), game.translate("werewolf.menu.advanced_tool.vote", game.getConfig().getPlayerRequiredVoteEnd()), lore));
		invAdvancedTool.setItem(18, changeMeta(Material.GOLD_NUGGET, game.translate("werewolf.menu.advanced_tool.particles"), 1, Collections.singletonList(Arrays.asList(game.translate("werewolf.menu.advanced_tool.particles_off"), game.translate("werewolf.menu.advanced_tool.exception"), game.translate("werewolf.menu.advanced_tool.particles_on")).get(game.getConfig().getGoldenAppleParticles()))));
		invAdvancedTool.setItem(20, changeMeta(UniversalMaterial.ORANGE_WOOL.getStack(), game.translate("werewolf.menu.advanced_tool.fox", game.getConfig().getDistanceFox()), lore));
		invAdvancedTool.setItem(22, changeMeta(UniversalMaterial.BROWN_WOOL.getStack(), game.translate("werewolf.menu.advanced_tool.bear_trainer", game.getConfig().getDistanceBearTrainer()), lore));
		invAdvancedTool.setItem(24, changeMeta(new ItemStack(Material.POTION, 1, (short) 8227), game.translate("werewolf.menu.advanced_tool.resistance", game.getConfig().getResistanceRate()), lore));
		invAdvancedTool.setItem(26, changeMeta(new ItemStack(Material.BREAD, 1), game.translate(game.getConfig().isTrollSV() ? "werewolf.menu.advanced_tool.troll_on" : "werewolf.menu.advanced_tool.troll_off"), null));
		invAdvancedTool.setItem(28, changeMeta(UniversalMaterial.PURPLE_WOOL.getStack(), game.translate("werewolf.menu.advanced_tool.succubus", game.getConfig().getDistanceSuccubus()), lore));
		player.openInventory(invAdvancedTool);
	}

	public void languageTool(Player player) {
		invLanguage.setItem(0, changeMeta(Material.COMPASS, game.translate("werewolf.menu.return"), 1, null));
		ItemStack fr = UniversalMaterial.WHITE_BANNER.getStack();
		BannerMeta frMeta = (BannerMeta) fr.getItemMeta();
		if (frMeta != null) {
			frMeta.addPattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_LEFT));
			frMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_CENTER));
			frMeta.addPattern(new Pattern(DyeColor.RED, PatternType.STRIPE_RIGHT));
			fr.setItemMeta(frMeta);
		}


		ItemStack en = UniversalMaterial.WHITE_BANNER.getStack();
		BannerMeta enMeta = (BannerMeta) en.getItemMeta();
		if (enMeta != null) {
			enMeta.addPattern(new Pattern(DyeColor.BLUE, PatternType.BASE));
			enMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT));
			enMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNRIGHT));
			enMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNRIGHT));
			enMeta.addPattern(new Pattern(DyeColor.RED, PatternType.CROSS));
			enMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_CENTER));
			enMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE));
			enMeta.addPattern(new Pattern(DyeColor.RED, PatternType.STRAIGHT_CROSS));
			en.setItemMeta(enMeta);
		}

		invLanguage.setItem(2, changeMeta(en, "English", Collections.singletonList("By Jormunth")));
		invLanguage.setItem(4, changeMeta(fr, "Français", Collections.singletonList("Par Ph1Lou")));
		player.openInventory(invLanguage);
	}

	public void updateSelectionSave(int j) {

		java.io.File repertoire = new java.io.File(main.getDataFolder()+"/configs/");
		java.io.File[] files=repertoire.listFiles();
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
		if(files.length!=0) {
			invSave.setItem(14, changeMeta(Material.BARRIER, game.translate("werewolf.menu.save.delete", files[j].getName()), 1, null));
			invSave.setItem(12, changeMeta(UniversalMaterial.BED.getType(), game.translate("werewolf.menu.save.load", files[j].getName()), 1, null));
		}
		else {
			invSave.setItem(12,null);
			invSave.setItem(14,null);
		}

	}

	public void load() {
		int j= findSelect(invSave)-1;
		java.io.File repertoire = new java.io.File(main.getDataFolder()+"/configs/");
		java.io.File[] files=repertoire.listFiles();
		if (files==null) return;
		if(j<0 || j>=files.length) return;
		game.getConfig().getConfig(game,files[j].getName().replace(".json",""));
		game.getStuffs().load(files[j].getName().replace(".json",""));
		updateSelectionSave(j+1);
	}

	public void save(String saveName, Player player)  {
		java.io.File file = new java.io.File(main.getDataFolder()+"/configs/", saveName+".json");
		java.io.File repertoire = new java.io.File(main.getDataFolder()+"/configs/");
		java.io.File[] files=repertoire.listFiles();
		if(files==null || files.length<8) {
			FileUtils_.save(file, Serializer.serialize(game.getConfig()));
			game.getStuffs().save(saveName);
			player.sendMessage(game.translate("werewolf.menu.save.success"));
			updateSelectionSave(findSelect(invSave));
		}
		else player.sendMessage(game.translate("werewolf.menu.save.failure"));
	}

	public void erase()  {
		java.io.File repertoire = new java.io.File(main.getDataFolder()+"/configs/");
		java.io.File[] files=repertoire.listFiles();
		if(files==null) return;
		int i=findSelect(invSave)-1;
		if(i<0 || i>=files.length) return;

		java.io.File file = new java.io.File(main.getDataFolder()+"/configs/", files[i].getName());
		if(!file.delete()){
			Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.menu.save.delete_failed",files[i].getName()));
		}
		file = new java.io.File(main.getDataFolder()+"/stuffs/", files[i].getName().replaceFirst(".json",".yml"));
		if(!file.delete()){
			Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.menu.save.delete_failed",files[i].getName().replaceFirst(".json",".yml")));
		}
		updateSelectionSave(findSelect(invSave));
	}

	public ItemStack changeMeta(ItemStack item, String item_name, List<String> lore) {
		ItemMeta meta1 = item.getItemMeta();
		if (meta1 == null) return new ItemStack(Material.DIAMOND);
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
			ItemStack itemStack = inv.getItem(i);
			if (itemStack != null && itemStack.getType() == Material.FEATHER) {
				find = true;
			} else i++;
		}
		if(!find){
			i=0;
		}
		return i;
	}

	public void selectMinus(int i) {

		if(!game.isState(StateLG.GAME)) {
			ItemStack itemStack = this.invRole.getItem(i);
			if (itemStack == null) return;
			String key = getKeyFromLore(itemStack);
			int j = game.getConfig().getRoleCount().get(key);
			if (j > 0) {
				game.getScore().setRole(game.getScore().getRole() - 1);
				game.getConfig().getRoleCount().put(key, j - 1);
			}
		}
	}

	public void selectPlus(int i) {

		if(!game.isState(StateLG.GAME)) {
			ItemStack itemStack = this.invRole.getItem(i);
			if (itemStack == null) return;
			String key = getKeyFromLore(itemStack);

			int j = game.getConfig().getRoleCount().get(key);

			game.getConfig().getRoleCount().put(key, j + 1);
			game.getScore().setRole(game.getScore().getRole() + 1);
		}
	}

	public void SelectMinusTimer(int v) {

		int i = findSelect(invTimer);
		ItemStack itemStack = this.invTimer.getItem(i);
		if (itemStack == null) return;
		String key = getKeyFromLore(itemStack);
		int j = game.getConfig().getTimerValues().get(key);

		if (j >= v) {
			game.getConfig().getTimerValues().put(key, j - v);
			updateSelectionTimer(i-9);
		}
	}

	public void selectPlusTimer(int v) {
		int i = findSelect(invTimer);
		ItemStack itemStack = this.invTimer.getItem(i);
		if (itemStack == null) return;
		String key = getKeyFromLore(itemStack);
		int j = game.getConfig().getTimerValues().get(key);
		game.getConfig().getTimerValues().put(key, j + v);
		updateSelectionTimer(i-9);
	}



	public void updateSelection(){

		List<String> loreLover = Arrays.asList(game.translate("werewolf.menu.left"), game.translate("werewolf.menu.right"));
		List<String> lore = new ArrayList<>(loreLover);

		int i=9;

		if (game.getConfig().getLoverSize() > 0) {
			invRole.setItem(2, changeMeta(UniversalMaterial.GREEN_TERRACOTTA.getStack(game.getConfig().getLoverSize()), game.translate("werewolf.role.lover.display"), lore));
		} else
			invRole.setItem(2, changeMeta(UniversalMaterial.RED_TERRACOTTA.getStack(), game.translate("werewolf.role.lover.display"), lore));

		if (game.getConfig().getAmnesiacLoverSize() > 0) {
			invRole.setItem(4, changeMeta(UniversalMaterial.GREEN_TERRACOTTA.getStack(game.getConfig().getAmnesiacLoverSize()), game.translate("werewolf.role.amnesiac_lover.display"), lore));
		} else
			invRole.setItem(4, changeMeta(UniversalMaterial.RED_TERRACOTTA.getStack(), game.translate("werewolf.role.amnesiac_lover.display"), lore));

		if (game.getConfig().getCursedLoverSize() > 0) {
			invRole.setItem(6, changeMeta(UniversalMaterial.GREEN_TERRACOTTA.getStack(game.getConfig().getCursedLoverSize()), game.translate("werewolf.role.cursed_lover.display"), lore));
		} else
			invRole.setItem(6, changeMeta(UniversalMaterial.RED_TERRACOTTA.getStack(), game.translate("werewolf.role.cursed_lover.display"), lore));

		for(Category category:Category.values()) {
			invRole.setItem(category.ordinal() * 2 + 46, changeMeta(this.category.equals(category) ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, game.translate(category.getKey()), Math.max(1, count(category)), null));
		}

		lore.add(game.translate("werewolf.menu.shift"));

		for (RoleRegister roleRegister:game.getRolesRegister()) {

			if(roleRegister.getCategories().contains(this.category)){
				List<String> lore2 = new ArrayList<>(lore);
				lore2.addAll(roleRegister.getLore());
				String key = roleRegister.getKey();
				lore2.add(ChatColor.BLACK+key);

				if (game.getConfig().getRoleCount().get(key) > 0) {
					invRole.setItem(i, changeMeta(UniversalMaterial.GREEN_TERRACOTTA.getStack(game.getConfig().getRoleCount().get(key)), roleRegister.getName(), lore2));
				} else
					invRole.setItem(i, changeMeta(UniversalMaterial.RED_TERRACOTTA.getStack(), roleRegister.getName(), lore2));
				i++;
			}
		}
		for(int j=i;j<45;j++){
			invRole.setItem(j,null);
		}
	}

	private int count(Category category) {
		int i=0;
		for (RoleRegister roleRegister:game.getRolesRegister()) {
			if(roleRegister.getCategories().contains(category)){
				i+=game.getConfig().getRoleCount().get(roleRegister.getKey());
			}

		}
		return i;
	}

	public void updateSelectionTimer(){
		updateSelectionTimer( Math.max(findSelect(invTimer)-9,0));
	}

	public void updateSelectionTimer(int j) {

		int i =0;
		String key = null;
		
		for (TimerRegister timer:main.getRegisterTimers()) {

			List<String> lore = new ArrayList<>(timer.getLore());
			lore.add(ChatColor.BLACK+timer.getKey());
			if (i==j) {
				key=timer.getKey();
				invTimer.setItem(9 + i, changeMeta(Material.FEATHER, game.translate(timer.getKey(), game.getScore().conversion(game.getConfig().getTimerValues().get(key))), 1, lore));
				
			} else {
				invTimer.setItem(9 + i, changeMeta(Material.ANVIL, game.translate(timer.getKey(), game.getScore().conversion(game.getConfig().getTimerValues().get(timer.getKey()))), 1, lore));
			}

			i++;
		}
		
		
		if(key==null) return;
		String c = game.getScore().conversion(game.getConfig().getTimerValues().get(key));

		invTimer.setItem(1, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "-10m", c), 1, null));
		invTimer.setItem(2, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "-1m", c), 1, null));
		invTimer.setItem(3, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "-10s", c), 1, null));
		invTimer.setItem(4, changeMeta(Material.BEACON, game.translate(key, c), 1, null));
		invTimer.setItem(5, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "+10s", c), 1, null));
		invTimer.setItem(6, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "+1m", c), 1, null));
		invTimer.setItem(7, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "+10m", c), 1, null));


	}


	public void updateSelectionTool() {
		int i=0;
		for (ConfigRegister ConfigRegister:main.getRegisterConfigs()) {

			List<String> lore = new ArrayList<>(ConfigRegister.getLore());
			lore.add(ChatColor.BLACK+ConfigRegister.getKey());
			if (game.getConfig().getConfigValues().get(ConfigRegister.getKey())) {
				lore.add(0, game.translate("werewolf.utils.enable", ""));
				invConfig.setItem(9 + i, changeMeta(UniversalMaterial.GREEN_TERRACOTTA.getStack(), game.translate(ConfigRegister.getKey()), lore));
			} else {
				lore.add(0, game.translate("werewolf.utils.disable", ""));
				invConfig.setItem(9 + i, changeMeta(UniversalMaterial.RED_TERRACOTTA.getStack(), game.translate(ConfigRegister.getKey()), lore));
			}

			i++;
		}
	}

	public void updateSelectionScenario() {
		int i=0;
		for (ScenarioRegister scenarioRegister:main.getRegisterScenarios()) {

			List<String> lore = new ArrayList<>(scenarioRegister.getLore());
			lore.add(ChatColor.BLACK+scenarioRegister.getKey());
			if (game.getConfig().getScenarioValues().get(scenarioRegister.getKey())) {
				lore.add(0, game.translate("werewolf.utils.enable", ""));
				invScenario.setItem(9 + i, changeMeta(UniversalMaterial.GREEN_TERRACOTTA.getStack(), game.translate(scenarioRegister.getKey()), lore));
			} else {
				lore.add(0, game.translate("werewolf.utils.disable", ""));
				invScenario.setItem(9 + i, changeMeta(UniversalMaterial.RED_TERRACOTTA.getStack(), game.translate(scenarioRegister.getKey()), lore));
			}

			i++;
		}
		game.updateNameTag();
		game.updateScenarios();
	}

	public void resetRole() {
		for (RoleRegister roleRegister : game.getRolesRegister()) {
			game.getConfig().getRoleCount().put(roleRegister.getKey(), 0);
		}
		game.getConfig().setAmnesiacLoverSize(0);
		game.getConfig().setLoverSize(0);
		game.getConfig().setCursedLoverSize(0);
		game.getScore().setRole(0);
		updateSelection();
	}


	public void updateSelectionBorder() {
		invBorder.setItem(3, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "-", game.getConfig().getBorderMax()), 1, null));
		invBorder.setItem(4, changeMeta(Material.GLASS,game.translate("werewolf.menu.border.radius_border_max",game.getConfig().getBorderMax()), 1, null));
		invBorder.setItem(5, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "+", game.getConfig().getBorderMax()), 1, null));
		invBorder.setItem(12, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "-", game.getConfig().getBorderMin()), 1, null));
		invBorder.setItem(13, changeMeta(Material.GLASS,game.translate("werewolf.menu.border.radius_border_min",game.getConfig().getBorderMin()), 1, null));
		invBorder.setItem(14, changeMeta(Material.STONE_BUTTON, game.translate("werewolf.utils.display", "+", game.getConfig().getBorderMin()), 1, null));
	}

	@Nullable
	public String getKeyFromLore(@NotNull ItemStack item){

		ItemMeta itemMeta = item.getItemMeta();
		if (itemMeta == null) return null;
		List<String> lore = itemMeta.getLore();
		if (lore == null) return null;
		if (lore.isEmpty()) return null;
		String key = lore.get(lore.size() - 1);

		return key.replace(ChatColor.BLACK.toString(),"");
	}
}


package io.github.ph1lou.pluginlg.listener;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.Category;
import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import io.github.ph1lou.pluginlgapi.events.UpdateConfigEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;


public class MenuListener implements Listener{

	private final GameManager game;
	private final MainLG main;
	
	public MenuListener(MainLG main, GameManager game) {
		this.game=game;
		this.main=main;
	}

	@EventHandler
	private void onConfigClose(InventoryCloseEvent event) {
		Inventory inventory=event.getInventory();

		if(game.optionlg.isConfigInventory(inventory)){
			Bukkit.getPluginManager().callEvent(new UpdateConfigEvent(event.getPlayer().getUniqueId()));
		}
	}


	@EventHandler
    private void onSousMenu(InventoryClickEvent event) {

		InventoryView view = event.getView();
		Player player = (Player) event.getWhoClicked();
		ItemStack current = event.getCurrentItem();

		if (current != null) {

			if (view.getTitle().equalsIgnoreCase(game.translate("werewolf.menu.name"))) {

				event.setCancelled(true);

				if (current.getType() == Material.BEACON) {
					game.optionlg.chooseRole(player);
				} else if (current.getType() == Material.ANVIL) {
					game.optionlg.timerTool(player);
				} else if (current.getType() == Material.MAP) {
					game.optionlg.globalTool(player);
				} else if (current.getType() == Material.PUMPKIN) {
					game.optionlg.scenarioTool(player);
				}
				else if(current.getType()==Material.CHEST ) {
					game.optionlg.stuffTool(player);
				}
				else if(current.getType()==Material.GLASS) {
					game.optionlg.borderTool(player);
				}
				else if(current.getType()==Material.ARMOR_STAND ) {
					game.optionlg.saveTool(player);
				}
				else if(current.getType()==Material.ENCHANTMENT_TABLE){
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.WORKBENCH) {
					game.optionlg.advancedTool(player);
				}
				else if(current.getType()==Material.BANNER) {
					game.optionlg.languageTool(player);
				}
				else if(current.getType()==Material.SKULL_ITEM && event.getSlot()==0) {
					game.optionlg.whiteListTool(player);
				}

			}		
			else if(view.getTitle().equals(game.translate("werewolf.menu.global.name"))){
				event.setCancelled(true);
				if(current.getType()==Material.STAINED_CLAY){
                    game.getConfig().getConfigValues().put(ToolLG.values()[(event.getSlot() - 9)], !game.getConfig().getConfigValues().get(ToolLG.values()[(event.getSlot() - 9)]));
                    game.optionlg.updateSelectionTool();
                }
				else if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
			}

			else if(view.getTitle().equals(game.translate("werewolf.menu.scenarios.name"))){
				event.setCancelled(true);
				if(current.getType()==Material.STAINED_CLAY) {
                    game.getConfig().getScenarioValues().put(ScenarioLG.values()[(event.getSlot() - 9)], !game.getConfig().getScenarioValues().get(ScenarioLG.values()[(event.getSlot() - 9)]));
                    game.optionlg.updateSelectionScenario();
                }
				else if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
			}
			
			else if(view.getTitle().equals(game.translate("werewolf.menu.roles.name"))) {

				event.setCancelled(true);

				if(current.getType()==Material.STAINED_CLAY){

					if(event.getClick().isShiftClick()) {
                        player.setGameMode(GameMode.CREATIVE);
                        player.getInventory().clear();

						List<String> lore = current.getItemMeta().getLore();
						String key = lore.get(lore.size()-1);

                        for (ItemStack i : game.getStuffs().getStuffRoles().get(key)) {
                            if (i != null) {
                                player.getInventory().addItem(i);
                            }
                        }
                        TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.loot_role.valid"));
						msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a stuffRole " + key));
						player.spigot().sendMessage(msg);
                        player.closeInventory();
                    }
					else if(event.getClick().isRightClick()){
						if(event.getSlot()==2){
							if(game.getConfig().getLoverSize()>0){
								game.getConfig().setLoverSize(game.getConfig().getLoverSize()-1);
							}
						}
						else if(event.getSlot()==4){
							if(game.getConfig().getAmnesiacLoverSize()>0){
								game.getConfig().setAmnesiacLoverSize(game.getConfig().getAmnesiacLoverSize()-1);
							}
						}
						else if(event.getSlot()==6){
							if(game.getConfig().getCursedLoverSize()>0){
								game.getConfig().setCursedLoverSize(game.getConfig().getCursedLoverSize()-1);
							}
						}
						else game.optionlg.selectMinus(event.getSlot());
					}
					else{
						if(event.getSlot()==2){
							game.getConfig().setLoverSize(game.getConfig().getLoverSize()+1);
						}
						else if(event.getSlot()==4){
							game.getConfig().setAmnesiacLoverSize(game.getConfig().getAmnesiacLoverSize()+1);
						}
						else if(event.getSlot()==6){
							game.getConfig().setCursedLoverSize(game.getConfig().getCursedLoverSize()+1);
						}
						else game.optionlg.selectPlus(event.getSlot());

					}
					game.optionlg.updateSelection();
				}
				else if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.BARRIER) {
					game.optionlg.resetRole();
				}
				else if(current.getType()==Material.REDSTONE_BLOCK){
					game.optionlg.setCategory(Category.values()[(event.getSlot()-46)/2]);
					game.optionlg.updateSelection();
				}
			}
			else if(view.getTitle().equals(game.translate("werewolf.menu.border.name"))) {

				event.setCancelled(true);

				if(current.getType()==Material.STONE_BUTTON) {
					if(event.getSlot()==3){
						if(game.getConfig().getBorderMax()>=100){
							game.getConfig().setBorderMax(game.getConfig().getBorderMax()-100);
						}
					}
					else if(event.getSlot()==5) {
						game.getConfig().setBorderMax(game.getConfig().getBorderMax()+100);
					}
					else if(event.getSlot()==12) {
						if(game.getConfig().getBorderMin()>=100){
							game.getConfig().setBorderMin(game.getConfig().getBorderMin()-100);
						}
					}
					else if(event.getSlot()==14) {
						game.getConfig().setBorderMin(game.getConfig().getBorderMin()+100);
					}
					game.optionlg.updateSelectionBorder();
				}
				else if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
			}
			else if(view.getTitle().equals(game.translate("werewolf.menu.timers.name"))) {

				event.setCancelled(true);

				if(current.getType()==Material.ANVIL || current.getType()==Material.FEATHER){
					game.optionlg.updateSelectionTimer(event.getSlot()-9);
				}
				else if(current.getType()==Material.STONE_BUTTON) {
					if(event.getSlot()==1){
						game.optionlg.SelectMinusTimer(600);
					}
					else if(event.getSlot()==2) {
						game.optionlg.SelectMinusTimer(60);
					}
					else if(event.getSlot()==3) {
						game.optionlg.SelectMinusTimer(10);
					}
					else if(event.getSlot()==5) {
						game.optionlg.selectPlusTimer(10);
					}
					else if(event.getSlot()==6) {
						game.optionlg.selectPlusTimer(60);
					}
					else if(event.getSlot()==7) {
						game.optionlg.selectPlusTimer(600);
					}
				}
				else if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
			}
			else if(view.getTitle().equals(game.translate("werewolf.menu.save.name"))) {

				event.setCancelled(true);

				if(current.getType()==Material.PAPER) {
					game.optionlg.updateSelectionSave(event.getSlot());
				}
				else if(current.getType()==Material.EMERALD_BLOCK) {
					new AnvilGUI.Builder()

							.onComplete((player2, text) -> {
								game.optionlg.save(text,player);
								return AnvilGUI.Response.close();
							})
							.preventClose()
							.text("SaveName")
							.item(new ItemStack(Material.EMERALD_BLOCK))
							.plugin(main)
							.onClose((player2)->game.optionlg.saveTool(player))
							.open(player);
				}
				else if(current.getType()==Material.BARRIER){
					game.optionlg.erase();
				}
				else if(current.getType()==Material.BED ){
					game.optionlg.load();
				}
				else if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
			}
			else if(view.getTitle().equals(game.translate("werewolf.menu.stuff.name"))) {

				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.CHEST) {
					player.setGameMode(GameMode.CREATIVE);
					PlayerInventory inventory = player.getInventory();

					for (int j = 0; j < 40; j++) {
						inventory.setItem(j, game.getStuffs().getStartLoot().getItem(j));
					}

					TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.stuff_start.valid"));
					msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a lootStart"));
					player.spigot().sendMessage(msg);
					player.closeInventory();
				}
				else if(current.getType()==Material.ENDER_CHEST) {

					player.setGameMode(GameMode.CREATIVE);
					PlayerInventory inventory = player.getInventory();
					inventory.clear();

					for (ItemStack i : game.getStuffs().getDeathLoot()) {
						if (i != null) {
							inventory.addItem(i);
						}
					}

					TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.loot_death.valid"));
					msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a lootDeath"));
					player.spigot().sendMessage(msg);
                    player.closeInventory();
                }
				else if(current.getType()==Material.EGG){
					game.getStuffs().loadAllStuffDefault();
				}
				else if(current.getType()==Material.GOLD_SWORD){
					game.getStuffs().loadAllStuffMeetUP();
				}
				else if(current.getType()==Material.JUKEBOX){
					game.getStuffs().loadStuffChill();
				}
				else if(current.getType()==Material.BARRIER){
					game.getStuffs().clearStartLoot();
					game.getStuffs().clearDeathLoot();
				}

			}
			else if(view.getTitle().equals(game.translate("werewolf.menu.enchantments.name"))) {
				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.IRON_SWORD) {
					if (event.getClick().isLeftClick()) {
						game.getConfig().setLimitSharpnessIron(game.getConfig().getLimitSharpnessIron()+1);
					}
					else if(game.getConfig().getLimitSharpnessIron()>0) game.getConfig().setLimitSharpnessIron(game.getConfig().getLimitSharpnessIron()-1);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.DIAMOND_SWORD) {
					if (event.getClick().isLeftClick()) {
						game.getConfig().setLimitSharpnessDiamond(game.getConfig().getLimitSharpnessDiamond()+1);
					}
					else if(game.getConfig().getLimitSharpnessDiamond()>0) game.getConfig().setLimitSharpnessDiamond(game.getConfig().getLimitSharpnessDiamond()-1);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.DIAMOND_CHESTPLATE) {
					if (event.getClick().isLeftClick()) {
						game.getConfig().setLimitProtectionDiamond(game.getConfig().getLimitProtectionDiamond()+1);
					}
					else if(game.getConfig().getLimitProtectionDiamond()>0) game.getConfig().setLimitProtectionDiamond(game.getConfig().getLimitProtectionDiamond()-1);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.IRON_CHESTPLATE) {
					if (event.getClick().isLeftClick()) {
						game.getConfig().setLimitProtectionIron(game.getConfig().getLimitProtectionIron()+1);
					}
					else if(game.getConfig().getLimitProtectionIron()>0) game.getConfig().setLimitProtectionIron(game.getConfig().getLimitProtectionIron()-1);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.BOW) {
					if (event.getClick().isLeftClick()) {
						game.getConfig().setLimitPowerBow(game.getConfig().getLimitPowerBow()+1);
					}
					else if(game.getConfig().getLimitPowerBow()>0) game.getConfig().setLimitPowerBow(game.getConfig().getLimitPowerBow()-1);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.STICK) {
					if (event.getClick().isLeftClick()) {
						game.getConfig().setLimitKnockBack((game.getConfig().getLimitKnockBack()+1)%3);
					}
					else game.getConfig().setLimitKnockBack((game.getConfig().getLimitKnockBack()+2)%3);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.ARROW) {
					if (event.getClick().isLeftClick()) {
						game.getConfig().setLimitPunch((game.getConfig().getLimitPunch()+1)%3);
					}
					else game.getConfig().setLimitPunch((game.getConfig().getLimitPunch()+2)%3);
					game.optionlg.enchantmentTool(player);
				}
			}
			else if(view.getTitle().equals(game.translate("werewolf.menu.advanced_tool.name"))) {
				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if(current.getType().equals(Material.DIAMOND)){
					if (event.getClick().isLeftClick()) {
						game.getConfig().setDiamondLimit(game.getConfig().getDiamondLimit()+1);
					}
					else if(game.getConfig().getDiamondLimit()>0) game.getConfig().setDiamondLimit(game.getConfig().getDiamondLimit()-1);

					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.POTION)) {

                    if (current.getDurability() == 8201) {
                        if (event.getClick().isLeftClick()) {
                            game.getConfig().setStrengthRate(game.getConfig().getStrengthRate() + 10);
                        } else if (game.getConfig().getStrengthRate() - 10 >= 0)
                            game.getConfig().setStrengthRate(game.getConfig().getStrengthRate() - 10);
                    } else if (current.getDurability() == 8227) {
                        if (event.getClick().isLeftClick()) {
                            game.getConfig().setResistanceRate(game.getConfig().getResistanceRate() + 2);
                        } else if (game.getConfig().getResistanceRate() - 2 >= 0)
                            game.getConfig().setResistanceRate(game.getConfig().getResistanceRate() - 2);
                    }
                    game.optionlg.advancedTool(player);
                }
				else if(current.getType().equals(Material.EXP_BOTTLE)){
                    if (event.getClick().isLeftClick()) {
                        game.getConfig().setXpBoost(game.getConfig().getXpBoost() + 10);
                    } else if (game.getConfig().getXpBoost() - 10 >= 0)
                        game.getConfig().setXpBoost(game.getConfig().getXpBoost() - 10);
					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.APPLE)){
                    if (event.getClick().isLeftClick()) {
                        if (game.getConfig().getAppleRate() + 5 <= 100) {
                            game.getConfig().setAppleRate(game.getConfig().getAppleRate() + 5);
                        }
                    } else if (game.getConfig().getAppleRate() - 5 >= 0)
                        game.getConfig().setAppleRate(game.getConfig().getAppleRate() - 5);
					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.FLINT)){
                    if (event.getClick().isLeftClick()) {
                        if (game.getConfig().getFlintRate() + 5 <= 100) {
                            game.getConfig().setFlintRate(game.getConfig().getFlintRate() + 5);
                        }
                    } else if (game.getConfig().getFlintRate() - 5 >= 0)
                        game.getConfig().setFlintRate(game.getConfig().getFlintRate() - 5);
					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.ENDER_PEARL)){
                    if (event.getClick().isLeftClick()) {
                        if (game.getConfig().getPearlRate() + 5 <= 100) {
                            game.getConfig().setPearlRate(game.getConfig().getPearlRate() + 5);
                        }
                    } else if (game.getConfig().getPearlRate() - 5 >= 0)
                        game.getConfig().setPearlRate(game.getConfig().getPearlRate() - 5);
					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.SKULL_ITEM)){
					if (event.getClick().isLeftClick()) {
						game.getConfig().setPlayerRequiredVoteEnd(game.getConfig().getPlayerRequiredVoteEnd()+1);
					}
					else if(game.getConfig().getPlayerRequiredVoteEnd()>0) game.getConfig().setPlayerRequiredVoteEnd(game.getConfig().getPlayerRequiredVoteEnd()-1);
					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.CARROT_ITEM)){
					if (event.getClick().isLeftClick()) {
						game.getConfig().setUseOfFlair(game.getConfig().getUseOfFlair()+1);
					}
					else if(game.getConfig().getUseOfFlair()>0) game.getConfig().setUseOfFlair(game.getConfig().getUseOfFlair()-1);
					game.optionlg.advancedTool(player);
				} else if (current.getType().equals(Material.GOLD_NUGGET)) {
                    if (event.getClick().isLeftClick()) {
                        game.getConfig().setGoldenAppleParticles((game.getConfig().getGoldenAppleParticles() + 1) % 3);
                    } else game.getConfig().setGoldenAppleParticles((game.getConfig().getGoldenAppleParticles() + 2) % 3);
                    game.optionlg.advancedTool(player);
                } else if (current.getType().equals(Material.WOOL)) {

                    if (current.getDurability() == 1) {
                        if (event.getClick().isLeftClick()) {
                            game.getConfig().setDistanceFox((game.getConfig().getDistanceFox() + 5));
                        } else if (game.getConfig().getDistanceFox() - 5 > 0)
                            game.getConfig().setDistanceFox(game.getConfig().getDistanceFox() - 5);
                    } else if (current.getDurability() == 12) {
						if (event.getClick().isLeftClick()) {
							game.getConfig().setDistanceBearTrainer((game.getConfig().getDistanceBearTrainer() + 5));
						} else if (game.getConfig().getDistanceBearTrainer() - 5 > 0)
							game.getConfig().setDistanceBearTrainer(game.getConfig().getDistanceBearTrainer() - 5);
					} else if (current.getDurability() == 6) {
						if (event.getClick().isLeftClick()) {
							game.getConfig().setDistanceSuccubus((game.getConfig().getDistanceSuccubus() + 5));
						} else if (game.getConfig().getDistanceSuccubus() - 5 > 0)
							game.getConfig().setDistanceSuccubus(game.getConfig().getDistanceSuccubus() - 5);
					}
                    game.optionlg.advancedTool(player);
                } else if (current.getType().equals(Material.BREAD)) {
					game.getConfig().setTrollSV(!game.getConfig().isTrollSV());
					game.optionlg.advancedTool(player);
				}
			}
			else if(view.getTitle().equals(game.translate("werewolf.menu.languages.name"))) {

				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.BANNER){

					if(event.getSlot()==4){
						main.getConfig().set("lang","fr");
					}
					else if(event.getSlot()==2){
						main.getConfig().set("lang","en");
					}

					main.lang.updateLanguage(game);

				}
			}
			else if(view.getTitle().equals(game.translate("werewolf.menu.whitelist.name"))) {
				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if (current.getType().equals(Material.EMPTY_MAP)) {
					game.setWhiteList(!game.isWhiteList());
					game.checkQueue();
					game.optionlg.whiteListTool(player);
				}

				else if (current.getType().equals(Material.SKULL_ITEM)) {
					if(event.getSlot()==10){
						if (event.getClick().isLeftClick()) {
							game.setSpectatorMode((game.getSpectatorMode()+1)%3);
						} else game.setSpectatorMode((game.getSpectatorMode()+2)%3);
					}
					else if(event.getSlot()==12){
						if (event.getClick().isLeftClick()) {
							game.setPlayerMax(game.getPlayerMax() + 1);
							game.checkQueue();
						} else if (game.getPlayerMax() - 1 > 0)
							game.setPlayerMax(game.getPlayerMax() - 1);
					}
					game.optionlg.whiteListTool(player);
				}
			}
		}
	}
	
}

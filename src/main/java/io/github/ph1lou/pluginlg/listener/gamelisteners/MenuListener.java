package io.github.ph1lou.pluginlg.listener.gamelisteners;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import io.github.ph1lou.pluginlg.game.GameManager;
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
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;


public class MenuListener implements Listener{

	private final GameManager game;
	private final MainLG main;
	
	public MenuListener(MainLG main, GameManager game) {
		this.game=game;
		this.main=main;
	}

	@EventHandler
    private void onSousMenu(InventoryClickEvent event) {

		if(!event.getWhoClicked().getWorld().equals(game.getWorld())) return;

		InventoryView view = event.getView();
		Player player =(Player) event.getWhoClicked();
		ItemStack current = event.getCurrentItem();
		
		if (current!=null) {
			
			if(view.getTitle().equalsIgnoreCase(game.text.getText(175))) {

				event.setCancelled(true);

				if(current.getType()==Material.BEACON) {
					game.optionlg.chooseRole(player);
				}
				else if(current.getType()==Material.ANVIL) {
					game.optionlg.timerTool(player);
				}
				else if(current.getType()==Material.MAP) {
					game.optionlg.globalTool(player);
				}
				else if(current.getType()==Material.PUMPKIN) {
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
			else if(view.getTitle().equals(game.text.getText(178))){
				event.setCancelled(true);
				if(current.getType()==Material.STAINED_CLAY){
                    game.config.configValues.put(ToolLG.values()[(event.getSlot() - 9)], !game.config.configValues.get(ToolLG.values()[(event.getSlot() - 9)]));
                    game.optionlg.updateSelectionTool();
                }
				else if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
			}

			else if(view.getTitle().equals(game.text.getText(76))){
				event.setCancelled(true);
				if(current.getType()==Material.STAINED_CLAY) {
                    game.config.scenarioValues.put(ScenarioLG.values()[(event.getSlot() - 9)], !game.config.scenarioValues.get(ScenarioLG.values()[(event.getSlot() - 9)]));
                    game.optionlg.updateSelectionScenario();
                }
				else if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
			}
			
			else if(view.getTitle().equals(game.text.getText(176))) {

				event.setCancelled(true);


				if(current.getType()==Material.STAINED_CLAY){

					if(event.getClick().isShiftClick()) {
                        player.setGameMode(GameMode.CREATIVE);
                        player.getInventory().clear();
                        int j = (event.getSlot() - 9);
                        for (ItemStack i : game.stufflg.role_stuff.get(RoleLG.values()[j])) {
                            if (i != null) {
                                player.getInventory().addItem(i);
                            }
                        }
                        TextComponent msg = new TextComponent(game.text.getText(198));
                        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/adminWW stuffRole " + j));
                        player.spigot().sendMessage(msg);
                        player.closeInventory();
                    }
					else if(event.getClick().isRightClick()){
						game.optionlg.selectMinus(event.getSlot()-9);
					}
					else{
						game.optionlg.selectPlus(event.getSlot()-9);
					}
				}
				else if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.BARRIER) {
					game.optionlg.resetRole();
				}

			}
			else if(view.getTitle().equals(game.text.getText(179))) {

				event.setCancelled(true);

				if(current.getType()==Material.STONE_BUTTON) {
					if(event.getSlot()==3){
						game.optionlg.selectMinusBorder();
					}
					else if(event.getSlot()==5) {
						game.optionlg.selectPlusBorder();
					}
				}
				else if(current.getType()==Material.GLASS){
					game.optionlg.updateSelectionBorder(event.getSlot()-9);
				}
				else if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
			}
			else if(view.getTitle().equals(game.text.getText(177))) {

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
			else if(view.getTitle().equals(game.text.getText(180))) {

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
			else if(view.getTitle().equals(game.text.getText(77))) {

				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.CHEST) {
                    player.setGameMode(GameMode.CREATIVE);
                    player.getInventory().clear();
                    for (ItemStack i : game.stufflg.getStartLoot()) {
                        if (i != null) {
                            player.getInventory().addItem(i);
                        }
                    }
                    TextComponent msg = new TextComponent(game.text.getText(127));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/adminWW lootStart"));
                    player.spigot().sendMessage(msg);
                    player.closeInventory();
                }
				else if(current.getType()==Material.ENDER_CHEST) {
                    player.setGameMode(GameMode.CREATIVE);
                    player.getInventory().clear();
                    for (ItemStack i : game.stufflg.getDeathLoot()) {
                        if (i != null) {
                            player.getInventory().addItem(i);
                        }
                    }
                    TextComponent msg = new TextComponent(game.text.getText(128));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/adminWW lootDeath"));
                    player.spigot().sendMessage(msg);
                    player.closeInventory();
                }
				else if(current.getType()==Material.EGG){
					game.stufflg.loadStuffDefault();
				}
				else if(current.getType()==Material.GOLD_SWORD){
					game.stufflg.loadStuffMeetUP();
				}
				else if(current.getType()==Material.JUKEBOX){
					game.stufflg.loadStuffChill();
				}
				else if(current.getType()==Material.BARRIER){
					game.stufflg.clearStartLoot();
					game.stufflg.clearDeathLoot();
				}

			}
			else if(view.getTitle().equals(game.text.getText(79))) {
				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.IRON_SWORD) {
					if (event.getClick().isLeftClick()) {
						game.config.setLimitSharpnessIron(game.config.getLimitSharpnessIron()+1);
					}
					else if(game.config.getLimitSharpnessIron()>0) game.config.setLimitSharpnessIron(game.config.getLimitSharpnessIron()-1);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.DIAMOND_SWORD) {
					if (event.getClick().isLeftClick()) {
						game.config.setLimitSharpnessDiamond(game.config.getLimitSharpnessDiamond()+1);
					}
					else if(game.config.getLimitSharpnessDiamond()>0) game.config.setLimitSharpnessDiamond(game.config.getLimitSharpnessDiamond()-1);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.DIAMOND_CHESTPLATE) {
					if (event.getClick().isLeftClick()) {
						game.config.setLimitProtectionDiamond(game.config.getLimitProtectionDiamond()+1);
					}
					else if(game.config.getLimitProtectionDiamond()>0) game.config.setLimitProtectionDiamond(game.config.getLimitProtectionDiamond()-1);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.IRON_CHESTPLATE) {
					if (event.getClick().isLeftClick()) {
						game.config.setLimitProtectionIron(game.config.getLimitProtectionIron()+1);
					}
					else if(game.config.getLimitProtectionIron()>0) game.config.setLimitProtectionIron(game.config.getLimitProtectionIron()-1);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.BOW) {
					if (event.getClick().isLeftClick()) {
						game.config.setLimitPowerBow(game.config.getLimitPowerBow()+1);
					}
					else if(game.config.getLimitPowerBow()>0) game.config.setLimitPowerBow(game.config.getLimitPowerBow()-1);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.STICK) {
					if (event.getClick().isLeftClick()) {
						game.config.setLimitKnockBack((game.config.getLimitKnockBack()+1)%3);
					}
					else game.config.setLimitKnockBack((game.config.getLimitKnockBack()+2)%3);
					game.optionlg.enchantmentTool(player);
				}
				else if(current.getType()==Material.ARROW) {
					if (event.getClick().isLeftClick()) {
						game.config.setLimitPunch((game.config.getLimitPunch()+1)%3);
					}
					else game.config.setLimitPunch((game.config.getLimitPunch()+2)%3);
					game.optionlg.enchantmentTool(player);
				}
			}
			else if(view.getTitle().equals(game.text.getText(75))) {
				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if(current.getType().equals(Material.DIAMOND)){
					if (event.getClick().isLeftClick()) {
						game.config.setDiamondLimit(game.config.getDiamondLimit()+1);
					}
					else if(game.config.getDiamondLimit()>0) game.config.setDiamondLimit(game.config.getDiamondLimit()-1);

					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.POTION)) {

                    if (current.getDurability() == 8201) {
                        if (event.getClick().isLeftClick()) {
                            game.config.setStrengthRate(game.config.getStrengthRate() + 10);
                        } else if (game.config.getStrengthRate() - 10 >= 0)
                            game.config.setStrengthRate(game.config.getStrengthRate() - 10);
                    } else if (current.getDurability() == 8227) {
                        if (event.getClick().isLeftClick()) {
                            game.config.setResistanceRate(game.config.getResistanceRate() + 2);
                        } else if (game.config.getResistanceRate() - 2 >= 0)
                            game.config.setResistanceRate(game.config.getResistanceRate() - 2);
                    }
                    game.optionlg.advancedTool(player);
                }
				else if(current.getType().equals(Material.EXP_BOTTLE)){
                    if (event.getClick().isLeftClick()) {
                        game.config.setXpBoost(game.config.getXpBoost() + 10);
                    } else if (game.config.getXpBoost() - 10 >= 0)
                        game.config.setXpBoost(game.config.getXpBoost() - 10);
					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.APPLE)){
                    if (event.getClick().isLeftClick()) {
                        if (game.config.getAppleRate() + 5 <= 100) {
                            game.config.setAppleRate(game.config.getAppleRate() + 5);
                        }
                    } else if (game.config.getAppleRate() - 5 >= 0)
                        game.config.setAppleRate(game.config.getAppleRate() - 5);
					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.FLINT)){
                    if (event.getClick().isLeftClick()) {
                        if (game.config.getFlintRate() + 5 <= 100) {
                            game.config.setFlintRate(game.config.getFlintRate() + 5);
                        }
                    } else if (game.config.getFlintRate() - 5 >= 0)
                        game.config.setFlintRate(game.config.getFlintRate() - 5);
					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.ENDER_PEARL)){
                    if (event.getClick().isLeftClick()) {
                        if (game.config.getPearlRate() + 5 <= 100) {
                            game.config.setPearlRate(game.config.getPearlRate() + 5);
                        }
                    } else if (game.config.getPearlRate() - 5 >= 0)
                        game.config.setPearlRate(game.config.getPearlRate() - 5);
					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.SKULL_ITEM)){
					if (event.getClick().isLeftClick()) {
						game.config.setPlayerRequiredVoteEnd(game.config.getPlayerRequiredVoteEnd()+1);
					}
					else if(game.config.getPlayerRequiredVoteEnd()>0) game.config.setPlayerRequiredVoteEnd(game.config.getPlayerRequiredVoteEnd()-1);
					game.optionlg.advancedTool(player);
				}
				else if(current.getType().equals(Material.CARROT_ITEM)){
					if (event.getClick().isLeftClick()) {
						game.config.setUseOfFlair(game.config.getUseOfFlair()+1);
					}
					else if(game.config.getUseOfFlair()>0) game.config.setUseOfFlair(game.config.getUseOfFlair()-1);
					game.optionlg.advancedTool(player);
				} else if (current.getType().equals(Material.GOLD_NUGGET)) {
                    if (event.getClick().isLeftClick()) {
                        game.config.setGoldenAppleParticles((game.config.getGoldenAppleParticles() + 1) % 3);
                    } else game.config.setGoldenAppleParticles((game.config.getGoldenAppleParticles() + 2) % 3);
                    game.optionlg.advancedTool(player);
                } else if (current.getType().equals(Material.WOOL)) {

                    if (current.getDurability() == 1) {
                        if (event.getClick().isLeftClick()) {
                            game.config.setDistanceFox((game.config.getDistanceFox() + 5));
                        } else if (game.config.getDistanceFox() - 5 > 0)
                            game.config.setDistanceFox(game.config.getDistanceFox() - 5);
                    } else if (current.getDurability() == 12) {
						if (event.getClick().isLeftClick()) {
							game.config.setDistanceBearTrainer((game.config.getDistanceBearTrainer() + 5));
						} else if (game.config.getDistanceBearTrainer() - 5 > 0)
							game.config.setDistanceBearTrainer(game.config.getDistanceBearTrainer() - 5);
					} else if (current.getDurability() == 6) {
						if (event.getClick().isLeftClick()) {
							game.config.setDistanceSuccubus((game.config.getDistanceSuccubus() + 5));
						} else if (game.config.getDistanceSuccubus() - 5 > 0)
							game.config.setDistanceSuccubus(game.config.getDistanceSuccubus() - 5);
					}
                    game.optionlg.advancedTool(player);
                } else if (current.getType().equals(Material.BREAD)) {
					game.config.setTrollSV(!game.config.isTrollSV());
					game.optionlg.advancedTool(player);
				} else if (current.getType().equals(Material.PUMPKIN)) {
					if (event.getClick().isLeftClick()) {
						game.config.setUseOfCharmed(game.config.getUseOfCharmed() + 1);
					} else if (game.config.getUseOfCharmed() > 0)
						game.config.setUseOfCharmed(game.config.getUseOfCharmed() - 1);
					game.optionlg.advancedTool(player);
				}
			}
			else if(view.getTitle().equals(game.text.getText(74))) {

				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if(current.getType()==Material.BANNER){
					if(event.getSlot()==2){
						game.setLang("en");
					}
					else if(event.getSlot()==4){
						game.setLang("fr");
					}
					else if(event.getSlot()==6){
						game.setLang("custom");
					}
					main.lang.changeLanguage(game);
					player.closeInventory();
				}
			}
			else if(view.getTitle().equals(game.text.getText(70))) {
				event.setCancelled(true);

				if(current.getType()==Material.COMPASS) {
					game.optionlg.toolBar(player);
				}
				else if (current.getType().equals(Material.EMPTY_MAP)) {
					game.setWhiteList(!game.isWhiteList());
					game.optionlg.whiteListTool(player);
					if(!game.isWhiteList()){
						for(Player p: Bukkit.getOnlinePlayers()){
							if(p.getWorld().equals(Bukkit.getWorlds().get(0))){
								game.sendMessage(p);
							}
						}
					}
				}

				else if (current.getType().equals(Material.SKULL_ITEM)) {
					if(event.getSlot()==10){
						if (event.getClick().isLeftClick()) {
							game.setSpectatorMode((game.getSpectatorMode()+1)%3);
						} else game.setSpectatorMode((game.getSpectatorMode()+2)%3);
					}
					else if(event.getSlot()==12){
						if (event.getClick().isLeftClick()) {
							game.setPlayerMax(game.getPlayerMax()+1);
						} else if (game.getPlayerMax() - 1 > 0)
							game.setPlayerMax(game.getPlayerMax()-1);
						game.checkQueue();
					}
					game.optionlg.whiteListTool(player);
				}
			}
		}
	}
	
}

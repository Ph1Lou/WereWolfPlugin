package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdvancedConfig implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("advancedConfig")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new AdvancedConfig())
            .size(5, 9)
            .title(JavaPlugin.getPlugin(Main.class)
                    .getWereWolfAPI().translate("werewolf.menu.advanced_tool.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(
                UniversalMaterial.COMPASS.getType())
                .setDisplayName(game.translate("werewolf.menu.return"))
                .build()), e -> Config.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        IConfiguration config = game.getConfig();

        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));

        contents.set(0, 2, ClickableItem.of((new ItemBuilder(Material.APPLE)
                .setLore(lore)
                .setDisplayName(game.translate("werewolf.menu.advanced_tool.apple",
                        config.getAppleRate()))
                .build()), e -> {

            if (e.isLeftClick()) {
                if (config.getAppleRate() + 5 <= 100) {
                    config.setAppleRate(config.getAppleRate() + 5);
                }
            } else if (config.getAppleRate() - 5 >= 0) {
                config.setAppleRate(config.getAppleRate() - 5);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.apple",
                            config.getAppleRate()))
                    .build());

        }));
        contents.set(0, 4, ClickableItem.of((
                new ItemBuilder(Material.FLINT)
                        .setLore(lore)
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.flint",
                                config.getFlintRate()))
                        .build()), e -> {

            if (e.isLeftClick()) {
                if (config.getFlintRate() + 5 <= 100) {
                    config.setFlintRate(config.getFlintRate() + 5);
                }
            } else if (config.getFlintRate() - 5 >= 0) {
                config.setFlintRate(config.getFlintRate() - 5);
            }

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.flint",
                            config.getFlintRate()))
                    .build());


        }));
        contents.set(0, 6, ClickableItem.of((
                new ItemBuilder(Material.ENDER_PEARL)
                        .setLore(lore)
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.ender_pearl",
                                config.getPearlRate())).build()), e -> {

            if (e.isLeftClick()) {
                if (config.getPearlRate() + 5 <= 100) {
                    config.setPearlRate(config.getPearlRate() + 5);
                }
            } else if (config.getPearlRate() - 5 >= 0) {
                config.setPearlRate(config.getPearlRate() - 5);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.ender_pearl",
                            config.getPearlRate()))
                    .build());

        }));
        contents.set(1, 1, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.CARROT.getType())
                        .setLore(lore)
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox_smell_number",
                                config.getUseOfFlair()))
                        .build()), e -> {
            if (e.isLeftClick()) {
                config.setUseOfFlair(config.getUseOfFlair() + 1);
            } else if (config.getUseOfFlair() > 0) {
                config.setUseOfFlair(config.getUseOfFlair() - 1);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox_smell_number",
                            config.getUseOfFlair()))
                    .build());

        }));
        contents.set(1, 3, ClickableItem.of((
                new ItemBuilder(Material.DIAMOND)
                        .setLore(lore)
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.diamond",
                                config.getDiamondLimit()))
                        .build()), e -> {
            if (e.isLeftClick()) {
                config.setDiamondLimit(config.getDiamondLimit() + 1);
            } else if (config.getDiamondLimit() > 0) {
                config.setDiamondLimit(config.getDiamondLimit() - 1);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.diamond",
                            config.getDiamondLimit()))
                    .build());


        }));
        contents.set(1, 5, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.EXPERIENCE_BOTTLE.getType())
                        .setLore(lore)
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.xp",
                                config.getXpBoost()))
                        .build()), e -> {

            if (e.isLeftClick()) {
                config.setXpBoost(config.getXpBoost() + 10);
            } else if (config.getXpBoost() - 10 >= 0) {
                config.setXpBoost(config.getXpBoost() - 10);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.xp",
                            config.getXpBoost()))
                    .build());

        }));
        contents.set(1, 7, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                        .setLore(lore).setDisplayName(
                        game.translate("werewolf.menu.advanced_tool.vote",
                                config.getPlayerRequiredVoteEnd()))
                        .build()), e -> {
            if (e.isLeftClick()) {
                config.setPlayerRequiredVoteEnd(config.getPlayerRequiredVoteEnd() + 1);
            } else if (config.getPlayerRequiredVoteEnd() > 0) {
                config.setPlayerRequiredVoteEnd(config.getPlayerRequiredVoteEnd() - 1);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore).setDisplayName(
                            game.translate("werewolf.menu.advanced_tool.vote",
                                    config.getPlayerRequiredVoteEnd()))
                    .build());

        }));

        contents.set(2, 0, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.BLUE_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.priestess",
                                config.getDistancePriestess()))
                        .setLore(lore).build()), e -> {

            if (e.isLeftClick()) {
                config.setDistancePriestess((config.getDistancePriestess() + 2));
            } else if (config.getDistancePriestess() - 2 > 0) {
                config.setDistancePriestess(config.getDistancePriestess() - 2);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.priestess",
                            config.getDistancePriestess()))
                    .build());

        }));


        contents.set(2, 2, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.GRAY_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.sister",
                                config.getDistanceSister()))
                        .setLore(lore).build()), e -> {

            if (e.isLeftClick()) {
                config.setDistanceSister((config.getDistanceSister() + 2));
            } else if (config.getDistanceSister() - 2 > 0) {
                config.setDistanceSister(config.getDistanceSister() - 2);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.sister",
                            config.getDistanceSister()))
                    .build());

        }));

        contents.set(2, 4, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.ORANGE_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox",
                                config.getDistanceFox()))
                        .setLore(lore).build()), e -> {

            if (e.isLeftClick()) {
                config.setDistanceFox((config.getDistanceFox() + 5));
            } else if (config.getDistanceFox() - 5 > 0) {
                config.setDistanceFox(config.getDistanceFox() - 5);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox",
                            config.getDistanceFox()))
                    .build());

        }));
        contents.set(2, 6, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.BROWN_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.bear_trainer",
                                config.getDistanceBearTrainer()))
                        .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceBearTrainer((config.getDistanceBearTrainer() + 5));
            } else if (config.getDistanceBearTrainer() - 5 > 0) {
                config.setDistanceBearTrainer(config.getDistanceBearTrainer() - 5);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.bear_trainer",
                            config.getDistanceBearTrainer()))
                    .build());

        }));
        contents.set(2, 8, ClickableItem.of((new ItemBuilder(
                UniversalMaterial.PURPLE_WOOL.getStack())
                .setDisplayName(game.translate("werewolf.menu.advanced_tool.succubus",
                        config.getDistanceSuccubus()))
                .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceSuccubus((config.getDistanceSuccubus() + 5));
            } else if (config.getDistanceSuccubus() - 5 > 0) {
                config.setDistanceSuccubus(config.getDistanceSuccubus() - 5);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.succubus",
                            config.getDistanceSuccubus()))
                    .build());

        }));

        contents.set(3, 1, ClickableItem.of((new ItemBuilder(
                UniversalMaterial.RED_WOOL.getStack())
                .setDisplayName(game.translate("werewolf.menu.advanced_tool.avenger_werewolf",
                        config.getDistanceAvengerWerewolf()))
                .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceAvengerWerewolf((config.getDistanceAvengerWerewolf() + 2));
            } else if (config.getDistanceAvengerWerewolf() - 2 > 0) {
                config.setDistanceAvengerWerewolf(config.getDistanceAvengerWerewolf() - 2);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.avenger_werewolf",
                            config.getDistanceAvengerWerewolf()))
                    .build());

        }));

        contents.set(3, 3, ClickableItem.of((
                new ItemBuilder(Material.POTION)
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.strength",
                                config.getStrengthRate()))
                        .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setStrengthRate(config.getStrengthRate() + 10);
            } else if (config.getStrengthRate() - 10 >= 0) {
                config.setStrengthRate(config.getStrengthRate() - 10);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.strength",
                            config.getStrengthRate()))
                    .build());

        }));
        contents.set(3, 5, ClickableItem.of((new ItemBuilder(Material.POTION)
                .setDisplayName(game.translate("werewolf.menu.advanced_tool.resistance",
                        config.getResistanceRate()))
                .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setResistanceRate(config.getResistanceRate() + 2);
            } else if (config.getResistanceRate() - 2 >= 0) {
                config.setResistanceRate(config.getResistanceRate() - 2);
            }

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.resistance",
                            config.getResistanceRate()))
                    .build());

        }));
        contents.set(3, 7, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.BOOK.getStack())
                        .setLore(lore).setDisplayName(
                        game.translate("werewolf.commands.admin.ww_chat.number",
                                config.getWereWolfChatMaxMessage()))
                        .build()), e -> {
            if (e.isLeftClick()) {
                config.setWereWolfChatMaxMessage(config.getWereWolfChatMaxMessage() + 1);
            } else if (config.getWereWolfChatMaxMessage() > 1) {
                config.setWereWolfChatMaxMessage(config.getWereWolfChatMaxMessage() - 1);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore).setDisplayName(
                            game.translate("werewolf.commands.admin.ww_chat.number",
                                    config.getWereWolfChatMaxMessage()))
                    .build());

        }));

        contents.set(4, 0, ClickableItem.of((
                new ItemBuilder(Material.BREAD)
                        .setDisplayName(game.translate(config.isTrollSV() ?
                                "werewolf.menu.advanced_tool.troll_on" :
                                "werewolf.menu.advanced_tool.troll_off"))
                        .setLore(Arrays.asList(game.translate(config.getTrollKey()),
                                game.translate("werewolf.menu.advanced_tool.troll_set")))
                        .build()), e -> {

            if (e.isShiftClick()) {
                TrollChoice.INVENTORY.open(player);
            } else {
                config.setTrollSV(!config.isTrollSV());
            }

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate(config.isTrollSV() ?
                            "werewolf.menu.advanced_tool.troll_on" :
                            "werewolf.menu.advanced_tool.troll_off"))
                    .setLore(Arrays.asList(game.translate(config.getTrollKey()),
                            game.translate("werewolf.menu.advanced_tool.troll_set")))
                    .build());


        }));

        contents.set(4, 2, ClickableItem.of((
                new ItemBuilder(Material.GOLD_NUGGET)
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.particles"))
                        .setLore(Collections.singletonList(
                                Arrays.asList(
                                        game.translate("werewolf.menu.advanced_tool.particles_off"),
                                        game.translate("werewolf.menu.advanced_tool.exception"),
                                        game.translate("werewolf.menu.advanced_tool.particles_on"))
                                        .get(config.getGoldenAppleParticles()))).build()), e -> {

            if (e.isLeftClick()) {
                config.setGoldenAppleParticles((config.getGoldenAppleParticles() + 1) % 3);
            } else {
                config.setGoldenAppleParticles((config.getGoldenAppleParticles() + 2) % 3);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.particles"))
                    .setLore(Collections.singletonList(
                            Arrays.asList(
                                    game.translate("werewolf.menu.advanced_tool.particles_off"),
                                    game.translate("werewolf.menu.advanced_tool.exception"),
                                    game.translate("werewolf.menu.advanced_tool.particles_on"))
                                    .get(config.getGoldenAppleParticles())))
                    .build());

        }));

        contents.set(4, 4, ClickableItem.of((
                        new ItemBuilder(Material.ARROW)
                                .setDisplayName(game.translate(config.isTrollLover() ?
                                        "werewolf.menu.advanced_tool.troll_lover_on" :
                                        "werewolf.menu.advanced_tool.troll_lover_off"))
                                .build())
                , e -> {

                    config.setTrollLover(!config.isTrollLover());

                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                            .setDisplayName(game.translate(config.isTrollLover() ?
                                    "werewolf.menu.advanced_tool.troll_lover_on" :
                                    "werewolf.menu.advanced_tool.troll_lover_off"))
                            .build());

                }));
    }




}


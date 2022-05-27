package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
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
            .size(2, 9)
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


        contents.set(0, 2, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                        .setLore(lore).setDisplayName(
                        game.translate("werewolf.menu.advanced_tool.vote",
                                        Formatter.number(config.getPlayerRequiredVoteEnd())))
                        .build()), e -> {
            if (e.isLeftClick()) {
                config.setPlayerRequiredVoteEnd(config.getPlayerRequiredVoteEnd() + 1);
            } else if (config.getPlayerRequiredVoteEnd() > 0) {
                config.setPlayerRequiredVoteEnd(config.getPlayerRequiredVoteEnd() - 1);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore).setDisplayName(
                            game.translate("werewolf.menu.advanced_tool.vote",
                                            Formatter.number(config.getPlayerRequiredVoteEnd())))
                    .build());

        }));


        contents.set(0, 4, ClickableItem.of((
                new ItemBuilder(Material.POTION)
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.strength",
                                        Formatter.number(config.getStrengthRate())))
                        .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setStrengthRate(config.getStrengthRate() + 10);
            } else if (config.getStrengthRate() - 10 >= 0) {
                config.setStrengthRate(config.getStrengthRate() - 10);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.strength",
                                    Formatter.number(config.getStrengthRate())))
                    .build());

        }));
        contents.set(0, 6, ClickableItem.of((new ItemBuilder(Material.POTION)
                .setDisplayName(game.translate("werewolf.menu.advanced_tool.resistance",
                                Formatter.number(config.getResistanceRate())))
                .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setResistanceRate(config.getResistanceRate() + 2);
            } else if (config.getResistanceRate() - 2 >= 0) {
                config.setResistanceRate(config.getResistanceRate() - 2);
            }

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.resistance",
                                    Formatter.number(config.getResistanceRate())))
                    .build());

        }));
        contents.set(0, 8, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.BOOK.getStack())
                        .setLore(lore).setDisplayName(
                        game.translate("werewolf.commands.ww_chat.number",
                                        Formatter.number(config.getWereWolfChatMaxMessage())))
                        .build()), e -> {
            if (e.isLeftClick()) {
                config.setWereWolfChatMaxMessage(config.getWereWolfChatMaxMessage() + 1);
            } else if (config.getWereWolfChatMaxMessage() > 1) {
                config.setWereWolfChatMaxMessage(config.getWereWolfChatMaxMessage() - 1);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore).setDisplayName(
                            game.translate("werewolf.commands.ww_chat.number",
                                            Formatter.number(config.getWereWolfChatMaxMessage())))
                    .build());

        }));

        contents.set(1, 1, ClickableItem.of((
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

        contents.set(1, 3, ClickableItem.of((
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

        contents.set(1, 5, ClickableItem.of((
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


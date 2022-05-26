package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Enchantments implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("enchantments")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Enchantments())
            .size(2, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menu.enchantments.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"), game.translate("werewolf.menu.right"));

        contents.set(0, 2, ClickableItem.of((
                        new ItemBuilder(Material.IRON_CHESTPLATE)
                                .setDisplayName(game.translate("werewolf.menu.enchantments.iron_protection",
                                        Formatter.number(game.getConfig().getLimitProtectionIron()))))
                                .setLore(lore).build(),
                e -> {
                    if (e.isLeftClick()) {
                        game.getConfig().setLimitProtectionIron(game.getConfig().getLimitProtectionIron() + 1);
                    } else if (game.getConfig().getLimitProtectionIron() > 0) {
                        game.getConfig().setLimitProtectionIron(game.getConfig().getLimitProtectionIron() - 1);
                    }

                    e.setCurrentItem(new ItemBuilder(Material.IRON_CHESTPLATE)
                            .setDisplayName(game.translate("werewolf.menu.enchantments.iron_protection",
                                    Formatter.number(game.getConfig().getLimitProtectionIron())
                            )).setLore(lore).build());
                }));
        contents.set(0, 4, ClickableItem.of((
                        new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplayName(
                                game.translate("werewolf.menu.enchantments.diamond_protection",
                                        Formatter.number(game.getConfig().getLimitProtectionDiamond()))))
                        .setLore(lore)
                        .build(),
                e -> {
                    if (e.isLeftClick()) {
                        game.getConfig().setLimitProtectionDiamond(game.getConfig().getLimitProtectionDiamond() + 1);
                    } else if (game.getConfig().getLimitProtectionDiamond() > 0) {
                        game.getConfig().setLimitProtectionDiamond(game.getConfig().getLimitProtectionDiamond() - 1);
                    }

                    e.setCurrentItem(new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplayName(
                            game.translate("werewolf.menu.enchantments.diamond_protection",
                                            Formatter.number(game.getConfig().getLimitProtectionDiamond())))
                            .setLore(lore)
                            .build());
                }));
        contents.set(0, 6, ClickableItem.of((
                        new ItemBuilder(Material.BOW)
                                .setDisplayName(
                                        game.translate("werewolf.menu.enchantments.power",
                                                Formatter.number(game.getConfig().getLimitPowerBow()))))
                        .setLore(lore).build(),
                e -> {
                    if (e.isLeftClick()) {
                        game.getConfig().setLimitPowerBow(game.getConfig().getLimitPowerBow() + 1);
                    } else if (game.getConfig().getLimitPowerBow() > 0) {
                        game.getConfig().setLimitPowerBow(game.getConfig().getLimitPowerBow() - 1);
                    }

                    e.setCurrentItem(new ItemBuilder(Material.BOW)
                            .setDisplayName(
                                    game.translate("werewolf.menu.enchantments.power",
                                                    Formatter.number(game.getConfig().getLimitPowerBow())))
                            .setLore(lore).build());
                }));

        List<String> lores = new ArrayList<>(Collections.singletonList(
                game.translate("werewolf.menu.enchantments.knock_back_normal")));

        lores.addAll(lore);
        if(game.getConfig().isKnockBackForInvisibleRoleOnly()){
            lores.add(0, game.translate("werewolf.menu.enchantments.knock_back_invisible"));
        }

        contents.set(0, 8, ClickableItem.of(
                        new ItemBuilder(Material.STICK)
                                .setDisplayName(game.translate("werewolf.menu.enchantments.knock_back",
                                                Formatter.number(game.getConfig().getLimitKnockBack())))
                                .setLore(lores)
                                .build(),
                e -> {
                    if (e.isShiftClick()) {
                        game.getConfig().setKnockBackForInvisibleRoleOnly(!game.getConfig().isKnockBackForInvisibleRoleOnly());
                    } else if (e.isLeftClick()) {
                        game.getConfig().setLimitKnockBack(game.getConfig().getLimitKnockBack() + 1);
                    } else if (game.getConfig().getLimitKnockBack() > 0) {
                        game.getConfig().setLimitKnockBack(game.getConfig().getLimitKnockBack() - 1);
                    }
                    List<String> lore1 = new ArrayList<>(Collections.singletonList(
                            game.translate("werewolf.menu.enchantments.knock_back_normal")));

                    lore1.addAll(lore);
                    if(game.getConfig().isKnockBackForInvisibleRoleOnly()){
                        lore1.add(0, game.translate("werewolf.menu.enchantments.knock_back_invisible"));
                    }
                    e.setCurrentItem(new ItemBuilder(Material.STICK)
                            .setDisplayName(game.translate("werewolf.menu.enchantments.knock_back",
                                    Formatter.number(game.getConfig().getLimitKnockBack())))
                            .setLore(lore1)
                            .build());
                }));
        contents.set(1, 2, ClickableItem.of((new ItemBuilder(Material.IRON_SWORD)
                        .setDisplayName(game.translate("werewolf.menu.enchantments.sharpness_iron",
                                Formatter.number(game.getConfig().getLimitSharpnessIron()))))
                        .setLore(lore).build(),
                e -> {
                    if (e.isLeftClick()) {
                        game.getConfig().setLimitSharpnessIron(game.getConfig().getLimitSharpnessIron() + 1);
                    } else if (game.getConfig().getLimitSharpnessIron() > 0) {
                        game.getConfig().setLimitSharpnessIron(game.getConfig().getLimitSharpnessIron() - 1);
                    }
                    e.setCurrentItem(new ItemBuilder(Material.IRON_SWORD)
                            .setDisplayName(game.translate("werewolf.menu.enchantments.sharpness_iron",
                                    Formatter.number(game.getConfig().getLimitSharpnessIron())))
                            .setLore(lore).build());
                }));
        contents.set(1, 4, ClickableItem.of((
                        new ItemBuilder(Material.DIAMOND_SWORD)
                                .setDisplayName(game.translate("werewolf.menu.enchantments.sharpness_diamond",
                                        Formatter.number(game.getConfig().getLimitSharpnessDiamond()))))
                        .setLore(lore).build(),
                e -> {
                    if (e.isLeftClick()) {
                        game.getConfig().setLimitSharpnessDiamond(game.getConfig().getLimitSharpnessDiamond() + 1);
                    } else if (game.getConfig().getLimitSharpnessDiamond() > 0) {
                        game.getConfig().setLimitSharpnessDiamond(game.getConfig().getLimitSharpnessDiamond() - 1);
                    }
                    e.setCurrentItem(new ItemBuilder(Material.DIAMOND_SWORD)
                            .setDisplayName(game.translate("werewolf.menu.enchantments.sharpness_diamond",
                                    Formatter.number(game.getConfig().getLimitSharpnessDiamond())))
                            .setLore(lore).build());
                }));
        contents.set(1, 6, ClickableItem.of((
                        new ItemBuilder(Material.ARROW)
                                .setDisplayName(
                                        game.translate("werewolf.menu.enchantments.punch",
                                                        Formatter.number(game.getConfig().getLimitPunch())))
                                .setLore(lore).build()),
                e -> {
                    if (e.isLeftClick()) {
                        game.getConfig().setLimitPunch(game.getConfig().getLimitPunch() + 1);
                    } else if (game.getConfig().getLimitPunch() > 0) {
                        game.getConfig().setLimitPunch(game.getConfig().getLimitPunch() - 1);
                    }
                    e.setCurrentItem(new ItemBuilder(Material.ARROW)
                            .setDisplayName(
                                    game.translate("werewolf.menu.enchantments.punch",
                                                    Formatter.number(game.getConfig().getLimitPunch())))
                            .setLore(lore).build());
                }));
        contents.set(1, 8, ClickableItem.of((
                        new ItemBuilder(UniversalMaterial.OAK_BOAT.getType())
                                .setDisplayName(game.translate("werewolf.menu.enchantments.depth_rider",
                                        Formatter.number(game.getConfig().getLimitDepthStrider()))))
                        .setLore(lore).build(),
                e -> {
                    if (e.isLeftClick()) {
                        game.getConfig().setLimitDepthStrider(game.getConfig().getLimitDepthStrider() + 1);
                    } else if (game.getConfig().getLimitDepthStrider() > 0) {
                        game.getConfig().setLimitDepthStrider(game.getConfig().getLimitDepthStrider() - 1);
                    }


                    e.setCurrentItem(new ItemBuilder(UniversalMaterial.OAK_BOAT.getType())
                            .setDisplayName(game.translate("werewolf.menu.enchantments.depth_rider",
                                    Formatter.number(game.getConfig().getLimitDepthStrider())))
                            .setLore(lore).build());
                }));

    }

}


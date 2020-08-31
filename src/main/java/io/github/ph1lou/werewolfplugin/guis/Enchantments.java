package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class Enchantments implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("enchantments")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Enchantments())
            .size(2, 9)
            .title(JavaPlugin.getPlugin(Main.class).getCurrentGame().translate("werewolf.menu.enchantments.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"), game.translate("werewolf.menu.right"));

        contents.set(0, 2, ClickableItem.of((new ItemBuilder(Material.IRON_CHESTPLATE).setDisplayName(game.translate("werewolf.menu.enchantments.iron_protection", game.getConfig().getLimitProtectionIron()))).setLore(lore).build(), e -> {
            if (e.isLeftClick()) {
                game.getConfig().setLimitProtectionIron(game.getConfig().getLimitProtectionIron() + 1);
            } else if (game.getConfig().getLimitProtectionIron() > 0)
                game.getConfig().setLimitProtectionIron(game.getConfig().getLimitProtectionIron() - 1);
            Enchantments.INVENTORY.open(player);
        }));
        contents.set(0, 4, ClickableItem.of((new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplayName(game.translate("werewolf.menu.enchantments.diamond_protection", game.getConfig().getLimitProtectionDiamond()))).setLore(lore).build(), e -> {
            if (e.isLeftClick()) {
                game.getConfig().setLimitProtectionDiamond(game.getConfig().getLimitProtectionDiamond() + 1);
            } else if (game.getConfig().getLimitProtectionDiamond() > 0)
                game.getConfig().setLimitProtectionDiamond(game.getConfig().getLimitProtectionDiamond() - 1);
            Enchantments.INVENTORY.open(player);
        }));
        contents.set(0, 6, ClickableItem.of((new ItemBuilder(Material.BOW).setDisplayName(game.translate("werewolf.menu.enchantments.power", game.getConfig().getLimitPowerBow()))).setLore(lore).build(), e -> {
            if (e.isLeftClick()) {
                game.getConfig().setLimitPowerBow(game.getConfig().getLimitPowerBow() + 1);
            } else if (game.getConfig().getLimitPowerBow() > 0)
                game.getConfig().setLimitPowerBow(game.getConfig().getLimitPowerBow() - 1);
            Enchantments.INVENTORY.open(player);
        }));
        contents.set(0, 8, ClickableItem.of((new ItemBuilder(Material.STICK).setDisplayName(Arrays.asList(game.translate("werewolf.menu.enchantments.knock_back_disable"), game.translate("werewolf.menu.enchantments.knock_back_invisible"), game.translate("werewolf.menu.enchantments.knock_back_enable")).get(game.getConfig().getLimitKnockBack())).setLore(lore)).build(), e -> {
            if (e.isLeftClick()) {
                game.getConfig().setLimitKnockBack((game.getConfig().getLimitKnockBack() + 1) % 3);
            } else game.getConfig().setLimitKnockBack((game.getConfig().getLimitKnockBack() + 2) % 3);
            Enchantments.INVENTORY.open(player);
        }));
        contents.set(1, 2, ClickableItem.of((new ItemBuilder(Material.IRON_SWORD).setDisplayName(game.translate("werewolf.menu.enchantments.sharpness_iron", game.getConfig().getLimitSharpnessIron()))).setLore(lore).build(), e -> {
            if (e.isLeftClick()) {
                game.getConfig().setLimitSharpnessIron(game.getConfig().getLimitSharpnessIron() + 1);
            } else if (game.getConfig().getLimitSharpnessIron() > 0)
                game.getConfig().setLimitSharpnessIron(game.getConfig().getLimitSharpnessIron() - 1);
            Enchantments.INVENTORY.open(player);
        }));
        contents.set(1, 4, ClickableItem.of((new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(game.translate("werewolf.menu.enchantments.sharpness_diamond", game.getConfig().getLimitSharpnessDiamond()))).setLore(lore).build(), e -> {
            if (e.isLeftClick()) {
                game.getConfig().setLimitSharpnessDiamond(game.getConfig().getLimitSharpnessDiamond() + 1);
            } else if (game.getConfig().getLimitSharpnessDiamond() > 0)
                game.getConfig().setLimitSharpnessDiamond(game.getConfig().getLimitSharpnessDiamond() - 1);
            Enchantments.INVENTORY.open(player);
        }));
        contents.set(1, 6, ClickableItem.of((new ItemBuilder(Material.ARROW).setDisplayName(Arrays.asList(game.translate("werewolf.menu.enchantments.punch_disable"), game.translate("werewolf.menu.enchantments.punch_cupid"), game.translate("werewolf.menu.enchantments.punch_enable")).get(game.getConfig().getLimitPunch()))).setLore(lore).build(), e -> {
            if (e.isLeftClick()) {
                game.getConfig().setLimitPunch((game.getConfig().getLimitPunch() + 1) % 3);
            } else game.getConfig().setLimitPunch((game.getConfig().getLimitPunch() + 2) % 3);
            Enchantments.INVENTORY.open(player);
        }));
        contents.set(1, 8, ClickableItem.of((new ItemBuilder(UniversalMaterial.OAK_BOAT.getType()).setDisplayName(game.translate("werewolf.menu.enchantments.depth_rider", game.getConfig().getLimitDepthStrider()))).setLore(lore).build(), e -> {
            if (e.isLeftClick()) {
                game.getConfig().setLimitDepthStrider(game.getConfig().getLimitDepthStrider() + 1);
            } else if (game.getConfig().getLimitDepthStrider() > 0)
                game.getConfig().setLimitDepthStrider(game.getConfig().getLimitDepthStrider() - 1);
            Enchantments.INVENTORY.open(player);
        }));

    }

}


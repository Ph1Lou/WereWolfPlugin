package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Config implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("config")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Config())
            .size(6, 9)
            .title(JavaPlugin.getPlugin(Main.class).getCurrentGame().translate("werewolf.menu.name"))
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack()).setDisplayName(game.translate("werewolf.menu.whitelist.name")).build()), e -> WhiteList.INVENTORY.open(player)));
        contents.set(1, 4, ClickableItem.of((new ItemBuilder(Material.BEACON).setDisplayName(game.translate("werewolf.menu.roles.name")).build()), e -> Roles.INVENTORY.open(player)));
        contents.set(2, 4, ClickableItem.of((new ItemBuilder(UniversalMaterial.ANVIL.getType()).setDisplayName(game.translate("werewolf.menu.timers.name")).build()), e -> Timers.INVENTORY.open(player)));
        contents.set(3, 2, ClickableItem.of((new ItemBuilder(Material.PUMPKIN).setDisplayName(game.translate("werewolf.menu.scenarios.name")).build()), e -> Scenarios.INVENTORY.open(player)));
        contents.set(3, 3, ClickableItem.of((new ItemBuilder(UniversalMaterial.MAP.getType()).setDisplayName(game.translate("werewolf.menu.global.name")).build()), e -> GlobalConfigs.INVENTORY.open(player)));
        contents.set(3, 4, ClickableItem.of((new ItemBuilder(UniversalMaterial.CHEST.getType()).setDisplayName(game.translate("werewolf.menu.stuff.name")).build()), e -> Stuffs.INVENTORY.open(player)));
        contents.set(3, 5, ClickableItem.of((new ItemBuilder(Material.GLASS).setDisplayName(game.translate("werewolf.menu.border.name")).build()), e -> Borders.INVENTORY.open(player)));
        contents.set(3, 6, ClickableItem.of((new ItemBuilder(UniversalMaterial.ENCHANTING_TABLE.getType()).setDisplayName(game.translate("werewolf.menu.enchantments.name")).build()), e -> Enchantments.INVENTORY.open(player)));
        contents.set(5, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.WHITE_BANNER.getStack()).setDisplayName(game.translate("werewolf.menu.languages.name")).addPattern(new Pattern(DyeColor.WHITE, PatternType.BASE)).addPattern(new Pattern(DyeColor.CYAN, PatternType.STRAIGHT_CROSS)).build()), e -> Languages.INVENTORY.open(player)));

        contents.set(5, 3, ClickableItem.of((new ItemBuilder(Material.ARMOR_STAND).setDisplayName(game.translate("werewolf.menu.save.name")).build()), e -> Save.INVENTORY.open(player)));
        contents.set(5, 5, ClickableItem.of((new ItemBuilder(UniversalMaterial.CRAFTING_TABLE.getType()).setDisplayName(game.translate("werewolf.menu.advanced_tool.name")).build()), e -> AdvancedConfig.INVENTORY.open(player)));

        contents.set(5, 8, ClickableItem.empty((new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack()).setDisplayName("Dev §bPh1Lou").setHead("Ph1Lou", Bukkit.getOfflinePlayer(UUID.fromString("056be797-2a0b-4807-9af5-37faf5384396"))).build())));


        int[] SlotRedGlass = {1, 2, 6, 7, 8, 9, 10, 16, 17, 18, 26, 27, 35, 36, 37, 43, 44, 46, 47, 51, 52};
        int[] SlotBlackGlass = {3, 4, 5, 11, 12, 14, 15, 19, 20, 21, 23, 24, 25, 28, 34, 38, 39, 40, 41, 42, 49};
        for (int slotRedGlass : SlotRedGlass) {
            contents.set(slotRedGlass / 9, slotRedGlass % 9, ClickableItem.empty((new ItemBuilder(UniversalMaterial.RED_STAINED_GLASS_PANE.getStack()).build())));

        }
        for (int slotBlackGlass : SlotBlackGlass) {
            contents.set(slotBlackGlass / 9, slotBlackGlass % 9, ClickableItem.empty((new ItemBuilder(UniversalMaterial.BLACK_STAINED_GLASS_PANE.getStack()).build())));
        }

    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}


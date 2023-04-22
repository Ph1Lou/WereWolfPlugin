package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainGUI implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("config")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new MainGUI())
            .size(6, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menus.main.name"))
            .closeable(true)
            .build();
    private int surprise = 0;

    @Override
    public void init(Player player, InventoryContents contents) {
        GameManager game = (GameManager) JavaPlugin.getPlugin(Main.class).getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                .setDisplayName(game.translate("werewolf.menus.whitelist.name"))
                .build()), e -> WhiteListGUI.INVENTORY.open(player)));

        contents.set(0, 8, ClickableItem.of((new ItemBuilder(UniversalMaterial.ARROW.getStack())
                .setDisplayName(game.translate("werewolf.menus.addon.name")))
                .build(), e -> AddonsGUI.INVENTORY.open(player)));

        contents.set(1, 4, ClickableItem.of((new ItemBuilder(Material.BEACON)
                .setDisplayName(game.translate("werewolf.menus.roles.name"))
                .build()), e -> RolesGUI.getInventory(player, Category.WEREWOLF).open(player)));

        contents.set(2, 4, ClickableItem.of((new ItemBuilder(UniversalMaterial.CLOCK.getType())
                .setDisplayName(game.translate("werewolf.menus.timers.name"))
                .build()), e -> TimersGUI.getInventory(player).open(player)));

        contents.set(3, 2, ClickableItem.of((new ItemBuilder(Material.PUMPKIN)
                .setDisplayName(game.translate("werewolf.menus.scenarios.name"))
                .build()), e -> ScenariosGUI.INVENTORY.open(player)));

        contents.set(3, 3, ClickableItem.of((new ItemBuilder(UniversalMaterial.NAME_TAG.getType())
                .setDisplayName(game.translate("werewolf.menus.configurations.name"))
                .build()), e -> ConfigurationsGUI.INVENTORY.open(player)));

        contents.set(3, 4, ClickableItem.of((new ItemBuilder(UniversalMaterial.CHEST.getType())
                .setDisplayName(game.translate("werewolf.menus.stuff.name"))
                .build()), e -> StuffsGUI.INVENTORY.open(player)));

        contents.set(3, 5, ClickableItem.of((new ItemBuilder(Material.GLASS)
                .setDisplayName(game.translate("werewolf.menus.border.name"))
                .build()), e -> BordersGUI.INVENTORY.open(player)));

        contents.set(3, 6, ClickableItem.of((new ItemBuilder(UniversalMaterial.ENCHANTING_TABLE.getType())
                .setDisplayName(game.translate("werewolf.menus.enchantments.name"))
                .build()), e -> EnchantmentsGUI.INVENTORY.open(player)));

        contents.set(4, 4, ClickableItem.of((new ItemBuilder(UniversalMaterial.MAP.getType())
                .setDisplayName(game.translate("werewolf.menus.maps.name"))
                .build()), e -> MapsGUI.INVENTORY.open(player)));

        contents.set(5, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.WHITE_BANNER.getStack())
                .setDisplayName(game.translate("werewolf.menus.languages.name"))
                .addPattern(new Pattern(DyeColor.WHITE, PatternType.BASE))
                .addPattern(new Pattern(DyeColor.CYAN, PatternType.STRAIGHT_CROSS))
                .build()), e -> LanguagesGUI.INVENTORY.open(player)));

        contents.set(5, 3, ClickableItem.of((new ItemBuilder(Material.ARMOR_STAND)
                .setDisplayName(game.translate("werewolf.menus.save.name"))
                .build()), e -> SaveGUI.INVENTORY.open(player)));

        contents.set(5, 4, ClickableItem.of((new ItemBuilder(Material.EGG)
                .setDisplayName(game.translate("werewolf.menus.random_events.name"))
                .build()), e -> RandomEventsGUI.INVENTORY.open(player)));

        contents.set(5, 5, ClickableItem.of((new ItemBuilder(UniversalMaterial.CRAFTING_TABLE.getType())
                .setDisplayName(game.translate("werewolf.menus.advanced_tool.name"))
                .build()), e -> AdvancedSettingsGUI.INVENTORY.open(player)));

        List<String> lore = new ArrayList<>();
        if (game.isDebug()) {
            lore.add(game.translate("werewolf.utils.debug"));
        }

        contents.set(5, 8, ClickableItem.of((new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                .setDisplayName("Dev §bPh1Lou")
                .setLore(lore)
                .setHead("Ph1Lou",
                        Bukkit.getOfflinePlayer(UUID.fromString("056be797-2a0b-4807-9af5-37faf5384396")))
                .build()), e -> {
            if (e.isShiftClick()) {
                Sound.NOTE_BASS.play(player);
                surprise++;
                if (surprise == 10) {
                    game.setDebug(true);
                    Sound.SUCCESSFUL_HIT.play(player);
                } else if (surprise >= 20) {
                    game.setDebug(false);
                    Sound.ANVIL_BREAK.play(player);
                    surprise = 0;
                }
                List<String> lore1 = new ArrayList<>();
                if (game.isDebug()) {
                    lore1.add(game.translate("werewolf.utils.debug"));
                }
                e.setCurrentItem(new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                        .setDisplayName("Dev §bPh1Lou")
                        .setLore(lore1)
                        .setHead("Ph1Lou",
                                Bukkit.getOfflinePlayer(UUID.fromString("056be797-2a0b-4807-9af5-37faf5384396")))
                        .build());
            }
        }));


        int[] SlotRedGlass = {1, 2, 6, 7, 9, 10, 16, 17, 18, 26, 27, 35, 36, 37, 43, 44, 46, 47, 51, 52};
        int[] SlotBlackGlass = {5, 11, 12, 14, 15, 19, 20, 21, 23, 24, 25, 28, 34, 38, 39, 41, 42};
        for (int slotRedGlass : SlotRedGlass) {
            contents.set(slotRedGlass / 9, slotRedGlass % 9, ClickableItem.empty((new ItemBuilder(UniversalMaterial.RED_STAINED_GLASS_PANE.getStack()).build())));

        }
        for (int slotBlackGlass : SlotBlackGlass) {
            contents.set(slotBlackGlass / 9, slotBlackGlass % 9, ClickableItem.empty((new ItemBuilder(UniversalMaterial.BLACK_STAINED_GLASS_PANE.getStack()).build())));
        }

    }

    @Override
    public void update(Player player, InventoryContents contents) {

        WereWolfAPI game = JavaPlugin.getPlugin(Main.class).getWereWolfAPI();

        contents.set(0, 3, ClickableItem.of((new ItemBuilder(UniversalMaterial.GOLDEN_SWORD.getStack())
                .setDisplayName(game.translate("werewolf.menus.meet_up.button",
                        Formatter.format("&on&", game.translate(game.getConfig().isMeetUp() ?
                                "werewolf.utils.on" :
                                "werewolf.utils.off"))))
                .build()), e -> MeetUpGUI.INVENTORY.open(player)));

        if (game.isState(StateGame.LOBBY)) {
            contents.set(0, 4, ClickableItem.of((new ItemBuilder(UniversalMaterial.LIME_WOOL
                            .getStack())
                            .setDisplayName(game.translate("werewolf.menus.start_stop.launch"))
                            .build()),
                    e -> StartAndStopGUI.INVENTORY.open(player)));
        } else {
            contents.set(0, 4, ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_WOOL
                    .getStack())
                    .setDisplayName(game.translate("werewolf.menus.start_stop.stop"))
                    .build()), e -> StartAndStopGUI.INVENTORY.open(player)));
        }

    }
}


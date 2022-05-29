package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.UpdateLanguageEvent;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class LanguagesGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("languages")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new LanguagesGUI())
            .size(2, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menus.languages.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menus.return")).build()),
                e -> MainGUI.INVENTORY.open(player)));

        ItemBuilder fr = new ItemBuilder(UniversalMaterial.WHITE_BANNER.getStack());
        fr.addPattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_LEFT));
        fr.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_CENTER));
        fr.addPattern(new Pattern(DyeColor.RED, PatternType.STRIPE_RIGHT));
        fr.setDisplayName("Français");
        fr.setLore(Collections.singletonList("Par Ph1Lou"));

        ItemBuilder en = new ItemBuilder(UniversalMaterial.WHITE_BANNER.getStack());

        en.addPattern(new Pattern(DyeColor.BLUE, PatternType.BASE));
        en.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT));
        en.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNRIGHT));
        en.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNRIGHT));
        en.addPattern(new Pattern(DyeColor.RED, PatternType.CROSS));
        en.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_CENTER));
        en.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE));
        en.addPattern(new Pattern(DyeColor.RED, PatternType.STRAIGHT_CROSS));
        en.setDisplayName("English");
        en.setLore(Collections.singletonList("By Jormunth"));
        contents.set(0, 2, ClickableItem.of((en.build()), e -> {
            main.getConfig().set("lang", "en_EN");
            Bukkit.getPluginManager().callEvent(new UpdateLanguageEvent());
            LanguagesGUI.INVENTORY.open(player);
        }));
        contents.set(0, 4, ClickableItem.of((fr.build()), e -> {
            main.getConfig().set("lang", "fr_FR");
            Bukkit.getPluginManager().callEvent(new UpdateLanguageEvent());
            LanguagesGUI.INVENTORY.open(player);
        }));
    }



}


package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class Languages implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("languages")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Languages())
            .size(2, 9)
            .title(JavaPlugin.getPlugin(Main.class).getCurrentGame().translate("werewolf.menu.languages.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));

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
            main.getConfig().set("lang", "en");
            main.getLang().updateLanguage(game);
            Languages.INVENTORY.open(player);
        }));
        contents.set(0, 4, ClickableItem.of((fr.build()), e -> {
            main.getConfig().set("lang", "fr");
            main.getLang().updateLanguage(game);
            Languages.INVENTORY.open(player);
        }));
    }


    @Override
    public void update(Player player, InventoryContents contents) {


    }
}


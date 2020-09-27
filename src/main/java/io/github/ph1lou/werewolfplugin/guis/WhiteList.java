package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;

public class WhiteList implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("whitelist")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new WhiteList())
            .size(2, 9)
            .title(JavaPlugin.getPlugin(Main.class).getCurrentGame().translate("werewolf.menu.whitelist.name"))
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
        ConfigWereWolfAPI config = game.getConfig();

        contents.set(0, 2, ClickableItem.of((new ItemBuilder(UniversalMaterial.MAP.getType()).setDisplayName(config.isWhiteList() ? game.translate("werewolf.menu.whitelist.close") : game.translate("werewolf.menu.whitelist.open")).build()), e -> {
            config.setWhiteList(!config.isWhiteList());
            game.getModerationManager().checkQueue();
        }));
        contents.set(1, 1, ClickableItem.of((new ItemBuilder(UniversalMaterial.SKELETON_SKULL.getStack()).setDisplayName(game.translate("werewolf.menu.whitelist.spectator_mode")).setLore(Collections.singletonList(Arrays.asList(game.translate("werewolf.menu.whitelist.disable"), game.translate("werewolf.menu.whitelist.death_only"), game.translate("werewolf.menu.whitelist.enable")).get(config.getSpectatorMode()))).build()), e -> {
            if (e.isLeftClick()) {
                config.setSpectatorMode((config.getSpectatorMode() + 1) % 3);
            } else config.setSpectatorMode((config.getSpectatorMode() + 2) % 3);
        }));
        contents.set(1, 3, ClickableItem.of((new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack()).setDisplayName(game.translate("werewolf.menu.whitelist.max", config.getPlayerMax())).build()), e -> {
            if (e.isLeftClick()) {
                config.setPlayerMax(config.getPlayerMax() + 1);
                game.getModerationManager().checkQueue();
            } else if (config.getPlayerMax() - 1 > 0)
                config.setPlayerMax(config.getPlayerMax() - 1);
        }));

    }
}


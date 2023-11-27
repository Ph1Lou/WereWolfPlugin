package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;

public class WhiteListGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("whitelist")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new WhiteListGUI())
            .size(2, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menus.whitelist.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menus.return")).build()),
                e -> MainGUI.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        IConfiguration config = game.getConfig();

        contents.set(0, 2, ClickableItem.of((new ItemBuilder(UniversalMaterial.MAP.getType()).setDisplayName(config.isWhiteList() ?
                game.translate("werewolf.menus.whitelist.close") :
                game.translate("werewolf.menus.whitelist.open")).build()), e -> {
            config.setWhiteList(!config.isWhiteList());
            //todo add event in API
            game.getModerationManager().checkQueue();

            e.setCurrentItem(new ItemBuilder(UniversalMaterial.MAP.getType())
                    .setDisplayName(config.isWhiteList() ?
                            game.translate("werewolf.menus.whitelist.close") :
                            game.translate("werewolf.menus.whitelist.open"))
                    .build());

        }));
        contents.set(1, 1, ClickableItem.of((new ItemBuilder(UniversalMaterial.SKELETON_SKULL.getStack())
                .setDisplayName(game.translate("werewolf.menus.whitelist.spectator_mode"))
                .setLore(Collections.singletonList(Arrays.asList(game.translate("werewolf.menus.whitelist.disable"),
                        game.translate("werewolf.menus.whitelist.death_only"),
                        game.translate("werewolf.menus.whitelist.enable")).get(config.getSpectatorMode())))
                .build()), e -> {
            if (e.isLeftClick()) {
                config.setSpectatorMode((config.getSpectatorMode() + 1) % 3);
            } else config.setSpectatorMode((config.getSpectatorMode() + 2) % 3);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.menus.whitelist.spectator_mode"))
                    .setLore(Collections.singletonList(Arrays.asList(game.translate("werewolf.menus.whitelist.disable"),
                            game.translate("werewolf.menus.whitelist.death_only"),
                            game.translate("werewolf.menus.whitelist.enable")).get(config.getSpectatorMode())))
                    .build());
        }));
        contents.set(1, 3, ClickableItem.of((new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                .setDisplayName(game.translate("werewolf.menus.whitelist.max",
                        Formatter.number(config.getPlayerMax())))
                .build()), e -> {
            if (e.isLeftClick()) {
                config.setPlayerMax(config.getPlayerMax() + 1);
                game.getModerationManager().checkQueue();
            } else if (config.getPlayerMax() - 1 > 0) {
                config.setPlayerMax(config.getPlayerMax() - 1);
            }

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.menus.whitelist.max",
                            Formatter.number(config.getPlayerMax())))
                    .build());
        }));

    }
}


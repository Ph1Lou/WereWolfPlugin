package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.ItemBuilder;
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

        contents.set(0, 2, ClickableItem.of((new ItemBuilder(UniversalMaterial.MAP.getType()).setDisplayName(game.isWhiteList() ? game.translate("werewolf.menu.whitelist.close") : game.translate("werewolf.menu.whitelist.open")).build()), e -> {
            game.setWhiteList(!game.isWhiteList());
            game.checkQueue();
        }));
        contents.set(1, 1, ClickableItem.of((new ItemBuilder(UniversalMaterial.SKELETON_SKULL.getStack()).setDisplayName(game.translate("werewolf.menu.whitelist.spectator_mode")).setLore(Collections.singletonList(Arrays.asList(game.translate("werewolf.menu.whitelist.disable"), game.translate("werewolf.menu.whitelist.death_only"), game.translate("werewolf.menu.whitelist.enable")).get(game.getSpectatorMode()))).build()), e -> {
            if (e.isLeftClick()) {
                game.setSpectatorMode((game.getSpectatorMode() + 1) % 3);
            } else game.setSpectatorMode((game.getSpectatorMode() + 2) % 3);
        }));
        contents.set(1, 3, ClickableItem.of((new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack()).setDisplayName(game.translate("werewolf.menu.whitelist.max", game.getPlayerMax())).build()), e -> {
            if (e.isLeftClick()) {
                game.setPlayerMax(game.getPlayerMax() + 1);
                game.checkQueue();
            } else if (game.getPlayerMax() - 1 > 0)
                game.setPlayerMax(game.getPlayerMax() - 1);
        }));

    }
}


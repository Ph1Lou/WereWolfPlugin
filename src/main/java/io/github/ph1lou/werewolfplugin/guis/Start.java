package io.github.ph1lou.werewolfplugin.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Start implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("startgame")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Start())
            .size(3, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI()
                    .translate("werewolf.announcement.start.launch"))
            .closeable(true)
            .parent(Config.INVENTORY)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = (GameManager) JavaPlugin.getPlugin(Main.class).getWereWolfAPI();

        contents.fillBorders(ClickableItem.empty(new ItemStack(UniversalMaterial.ORANGE_STAINED_GLASS_PANE.getStack())));

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = (GameManager) JavaPlugin.getPlugin(Main.class).getWereWolfAPI();

        if (game.isState(StateGame.LOBBY)) {
            contents.set(1, 4, ClickableItem.of((
                    new ItemBuilder(UniversalMaterial.LIME_WOOL
                            .getStack())
                            .setDisplayName(game.translate("werewolf.announcement.start.launch"))
                            .build()), e -> player.performCommand(
                    String.format("a %s",
                            game.translate(
                                    "werewolf.commands.admin.start.command")))));
        } else {

            contents.set(1, 4, ClickableItem.of((
                    new ItemBuilder(UniversalMaterial.RED_WOOL
                            .getStack())
                            .setDisplayName(game.translate("werewolf.commands.admin.stop.message"))
                            .build()), e -> player.performCommand(String.format("a %s", game.translate("werewolf.commands.admin.stop.command")))));
        }
    }
}

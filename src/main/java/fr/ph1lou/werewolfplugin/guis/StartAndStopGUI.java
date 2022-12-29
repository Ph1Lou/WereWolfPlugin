package fr.ph1lou.werewolfplugin.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class StartAndStopGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("startGame")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new StartAndStopGUI())
            .size(3, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI()
                    .translate("werewolf.menus.start_stop.launch"))
            .closeable(true)
            .parent(MainGUI.INVENTORY)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = (GameManager) JavaPlugin.getPlugin(Main.class).getWereWolfAPI();

        contents.fillBorders(ClickableItem.empty(new ItemStack(UniversalMaterial.ORANGE_STAINED_GLASS_PANE.getStack())));

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menus.return")).build()), e -> MainGUI.INVENTORY.open(player)));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = (GameManager) JavaPlugin.getPlugin(Main.class).getWereWolfAPI();

        if (game.isState(StateGame.LOBBY)) {
            contents.set(1, 4, ClickableItem.of((
                    new ItemBuilder(UniversalMaterial.LIME_WOOL
                            .getStack())
                            .setDisplayName(game.translate("werewolf.menus.start_stop.launch"))
                            .build()), e -> player.performCommand(
                    String.format("a %s",
                            game.translate(
                                    "werewolf.commands.admin.start.command")))));
        } else {

            contents.set(1, 4, ClickableItem.of((
                    new ItemBuilder(UniversalMaterial.RED_WOOL
                            .getStack())
                            .setDisplayName(game.translate("werewolf.menus.start_stop.stop"))
                            .build()), e -> player.performCommand(String.format("a %s", game.translate("werewolf.commands.admin.stop.command")))));
        }
    }
}

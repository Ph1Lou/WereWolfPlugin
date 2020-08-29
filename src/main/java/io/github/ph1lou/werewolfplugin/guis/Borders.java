package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Borders implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("borders")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Borders())
            .size(2, 9)
            .title(JavaPlugin.getPlugin(Main.class).getCurrentGame().translate("werewolf.menu.border.name"))
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

        contents.set(0, 3, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "-", config.getBorderMax())).build()), e -> {
            if (game.getConfig().getBorderMax() >= 100) {
                game.getConfig().setBorderMax(game.getConfig().getBorderMax() - 100);
            }
        }));
        contents.set(0, 4, ClickableItem.of((new ItemBuilder(Material.GLASS).setDisplayName(game.translate("werewolf.menu.border.radius_border_max", config.getBorderMax())).build()), null));
        contents.set(0, 5, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "+", config.getBorderMax())).build()), e -> game.getConfig().setBorderMax(game.getConfig().getBorderMax() + 100)));
        contents.set(1, 3, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "-", config.getBorderMin())).build()), e -> {
            if (game.getConfig().getBorderMin() >= 100) {
                game.getConfig().setBorderMin(game.getConfig().getBorderMin() - 100);
            }
        }));
        contents.set(1, 4, ClickableItem.of((new ItemBuilder(Material.GLASS).setDisplayName(game.translate("werewolf.menu.border.radius_border_min", config.getBorderMin())).build()), null));
        contents.set(1, 5, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "+", config.getBorderMin())).build()), e -> game.getConfig().setBorderMin(game.getConfig().getBorderMin() + 100)));


    }

}


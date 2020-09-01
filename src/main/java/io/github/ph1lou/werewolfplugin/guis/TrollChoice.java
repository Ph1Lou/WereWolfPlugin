package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfapi.enumlg.Category;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TrollChoice implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("troll")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new TrollChoice())
            .size(6, 9)
            .title(JavaPlugin.getPlugin(Main.class).getCurrentGame().translate("werewolf.menu.roles.name"))
            .closeable(true)
            .build();


    private Category category = Category.WEREWOLF;


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> AdvancedConfig.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();

        int i = 9;

        contents.set(5, 1, ClickableItem.of((new ItemBuilder(Category.WEREWOLF == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.werewolf")).build()), e -> this.category = Category.WEREWOLF));
        contents.set(5, 3, ClickableItem.of((new ItemBuilder(Category.VILLAGER == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.villager")).build()), e -> this.category = Category.VILLAGER));
        contents.set(5, 5, ClickableItem.of((new ItemBuilder(Category.NEUTRAL == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.neutral")).build()), e -> this.category = Category.NEUTRAL));
        contents.set(5, 7, ClickableItem.of((new ItemBuilder(Category.ADDONS == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.addons")).build()), e -> this.category = Category.ADDONS));

        for (RoleRegister roleRegister : game.getRolesRegister()) {

            if (roleRegister.getCategories().contains(category)) {

                String key = roleRegister.getKey();

                if (key.equals(game.getTrollKey())) {
                    contents.set(i / 9, i % 9, ClickableItem.of((new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack()).setDisplayName(roleRegister.getName()).build()), e -> {
                    }));
                } else
                    contents.set(i / 9, i % 9, ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack()).setDisplayName(roleRegister.getName()).build()), e -> {
                        game.setTrollKey(key);
                        AdvancedConfig.INVENTORY.open(player);
                    }));
                i++;
            }
        }
        for (int j = i; j < 45; j++) {
            contents.set(j / 9, j % 9, null);
        }
    }

}


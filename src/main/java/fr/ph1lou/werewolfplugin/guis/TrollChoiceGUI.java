package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.Register;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrollChoiceGUI implements InventoryProvider {

    private Category category;

    public TrollChoiceGUI(Player player, Category category) {
        this.category = category;
    }

    public static SmartInventory getInventory(Player player, Category category) {
        return SmartInventory.builder()
                .id("troll")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new TrollChoiceGUI(player, category))
                .size(6, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menus.troll.name"))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        WereWolfAPI game = JavaPlugin.getPlugin(Main.class).getWereWolfAPI();
        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType())
                .setDisplayName(game.translate("werewolf.menus.return")).build()),
                e -> AdvancedSettingsGUI.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        IConfiguration config = game.getConfig();
        Pagination pagination = contents.pagination();

        contents.set(5, 1, ClickableItem.of(
                new ItemBuilder(
                        Category.WEREWOLF == this.category ?
                                Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                        .setDisplayName(game.translate(Camp.WEREWOLF.getKey()))
                        .build(), e -> this.category = Category.WEREWOLF));
        contents.set(5, 3, ClickableItem.of((
                new ItemBuilder(
                        Category.VILLAGER == this.category ?
                                Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                        .setDisplayName(game.translate(Camp.VILLAGER.getKey()))
                        .build()), e -> this.category = Category.VILLAGER));
        contents.set(5, 5, ClickableItem.of((
                new ItemBuilder(
                        Category.NEUTRAL == this.category ?
                                Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                        .setDisplayName(game.translate(Camp.NEUTRAL.getKey()))
                        .build()), e -> this.category = Category.NEUTRAL));
        contents.set(5, 7, ClickableItem.of((
                new ItemBuilder(
                        Category.ADDONS == this.category ?
                                Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                        .setDisplayName(game.translate("werewolf.categories.addons"))
                        .build()), e -> this.category = Category.ADDONS));

        List<ClickableItem> items = new ArrayList<>();


        main.getRegisterManager().getRolesRegister()
                .stream()
                .sorted((o1, o2) -> game.translate(o1.getMetaDatas().key())
                        .compareToIgnoreCase(game.translate(o2.getMetaDatas().key())))
                .forEach(roleRegister -> {

                    String key = roleRegister.getMetaDatas().key();
                    Optional<String> addonKey = Register.get().getModuleKey(key);

                    if (roleRegister.getMetaDatas().category() == this.category
                            ||
                            (addonKey.isPresent() &&
                                    !addonKey.get().equals(Main.KEY) &&
                                    this.category == Category.ADDONS)) {

                        List<String> lore = Arrays.stream(roleRegister.getMetaDatas().loreKey())
                                .map(game::translate)
                                .collect(Collectors.toList());

                        if (config.getTrollKey().equals(key)) {
                            items.add(ClickableItem.empty(new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack()).setLore(lore)
                                    .setDisplayName(game.translate(key)).build()));
                        } else {
                            items.add(ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack())
                                    .setLore(lore)
                                    .setDisplayName(game.translate(key)).build()), event ->
                                    config.setTrollKey(roleRegister.getMetaDatas().key())));
                        }

                    }
                });

        InventoryUtils.fillInventory(game, items, pagination, contents, () -> getInventory(player, this.category), 36);

    }
}


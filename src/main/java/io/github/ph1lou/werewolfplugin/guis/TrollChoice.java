package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Category;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class TrollChoice implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("troll")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new TrollChoice())
            .size(6, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menu.troll"))
            .closeable(true)
            .build();


    private final Map<UUID, Category> categories = new HashMap<>();


    @Override
    public void init(Player player, InventoryContents contents) {
        WereWolfAPI game = JavaPlugin.getPlugin(Main.class).getWereWolfAPI();
        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> AdvancedConfig.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        ConfigWereWolfAPI config = game.getConfig();
        Pagination pagination = contents.pagination();
        UUID uuid = player.getUniqueId();

        contents.set(5, 1, ClickableItem.of((
                new ItemBuilder(
                        Category.WEREWOLF == this.categories.getOrDefault(
                                uuid, Category.WEREWOLF) ?
                                Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                        .setDisplayName(game.translate("werewolf.categories.werewolf"))
                        .build()), e -> this.categories.put(uuid, Category.WEREWOLF)));
        contents.set(5, 3, ClickableItem.of((
                new ItemBuilder(
                        Category.VILLAGER == this.categories.getOrDefault(
                                uuid, Category.WEREWOLF) ?
                                Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                        .setDisplayName(game.translate("werewolf.categories.villager"))
                        .build()), e -> this.categories.put(uuid, Category.VILLAGER)));
        contents.set(5, 5, ClickableItem.of((
                new ItemBuilder(
                        Category.NEUTRAL == this.categories.getOrDefault(
                                uuid, Category.WEREWOLF) ?
                                Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                        .setDisplayName(game.translate("werewolf.categories.neutral"))
                        .build()), e -> this.categories.put(uuid, Category.NEUTRAL)));
        contents.set(5, 7, ClickableItem.of((
                new ItemBuilder(
                        Category.ADDONS == this.categories.getOrDefault(
                                uuid, Category.WEREWOLF) ?
                                Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                        .setDisplayName(game.translate("werewolf.categories.addons"))
                        .build()), e -> this.categories.put(uuid, Category.ADDONS)));

        List<ClickableItem> items = new ArrayList<>();

        for (RoleRegister roleRegister : main.getRegisterManager().getRolesRegister()) {

            if (roleRegister.getCategories().contains(categories.getOrDefault(uuid, Category.WEREWOLF))) {

                String key = roleRegister.getKey();
                List<String> lore = new ArrayList<>(roleRegister.getLore());

                if (config.getTrollKey().equals(key)) {
                    items.add(ClickableItem.empty(new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack()).setLore(lore).setDisplayName(game.translate(roleRegister.getKey())).build()));
                } else {
                    items.add(ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack()).setLore(lore).setDisplayName(game.translate(roleRegister.getKey())).build()), event -> {
                        config.setTrollKey(roleRegister.getKey());
                        AdvancedConfig.INVENTORY.open(player);
                    }));
                }

            }
        }
        if (items.size() > 36) {
            pagination.setItems(items.toArray(new ClickableItem[0]));
            pagination.setItemsPerPage(27);
            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));
            int page = pagination.getPage() + 1;
            contents.set(4, 2, ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName(game.translate("werewolf.menu.roles.previous", page, pagination.isFirst() ? page : page - 1)).build(),
                    e -> INVENTORY.open(player, pagination.previous().getPage())));
            contents.set(4, 6, ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName(game.translate("werewolf.menu.roles.next", page, pagination.isLast() ? page : page + 1)).build(),
                    e -> INVENTORY.open(player, pagination.next().getPage())));
            contents.set(4, 4, ClickableItem.empty(new ItemBuilder(UniversalMaterial.SIGN.getType()).setDisplayName(game.translate("werewolf.menu.roles.current", page, items.size() / 27 + 1)).build()));
        } else {
            int i = 0;
            for (ClickableItem clickableItem : items) {
                contents.set(i / 9 + 1, i % 9, clickableItem);
                i++;
            }
            for (int k = i; k < 36; k++) {
                contents.set(k / 9 + 1, k % 9, null);
            }
        }

    }
}


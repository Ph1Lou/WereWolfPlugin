package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrollChoice implements InventoryProvider {

    private Category category;

    public TrollChoice(Player player, Category category) {
        this.category = category;
    }

    public static SmartInventory getInventory(Player player, Category category) {
        return SmartInventory.builder()
                .id("troll")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new TrollChoice(player, category))
                .size(6, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menu.troll"))
                .closeable(true)
                .build();
    }



    @Override
    public void init(Player player, InventoryContents contents) {
        WereWolfAPI game = JavaPlugin.getPlugin(Main.class).getWereWolfAPI();
        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> AdvancedConfig.INVENTORY.open(player)));
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

                    Optional<String> addonKey = Register.get().getModuleKey(roleRegister.getMetaDatas().key());

                    if (roleRegister.getMetaDatas().category() == this.category
                            ||
                            (addonKey.isPresent() &&
                                    !addonKey.get().equals(Main.KEY) &&
                                    this.category == Category.ADDONS)) {

                        String key = roleRegister.getMetaDatas().key();
                        List<String> lore = Arrays.stream(roleRegister.getMetaDatas().loreKey())
                                .map(game::translate)
                                .collect(Collectors.toList());

                        if (config.getTrollKey().equals(key)) {
                            items.add(ClickableItem.empty(new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack()).setLore(lore)
                                    .setDisplayName(game.translate(roleRegister.getMetaDatas().key())).build()));
                        } else {
                            items.add(ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack())
                                    .setLore(lore)
                                    .setDisplayName(game.translate(roleRegister.getMetaDatas().key())).build()), event ->
                                    config.setTrollKey(roleRegister.getMetaDatas().key())));
                        }

                    }
                });

        if (items.size() > 36) {
            pagination.setItems(items.toArray(new ClickableItem[0]));
            pagination.setItemsPerPage(27);
            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));
            int page = pagination.getPage() + 1;
            contents.set(4, 2, ClickableItem.of(new ItemBuilder(Material.ARROW)
                            .setDisplayName(game.translate("werewolf.menu.roles.previous",
                                    Formatter.format("&current&",page),
                                                    Formatter.format("&previous&",pagination.isFirst() ? page : page - 1)))
                            .build(),
                    e -> getInventory(player, this.category).open(player, pagination.previous().getPage())));
            contents.set(4, 6, ClickableItem.of(new ItemBuilder(Material.ARROW)
                            .setDisplayName(game.translate("werewolf.menu.roles.next",
                                                    Formatter.format("&current&",page),
                                                                    Formatter.format("&next&",pagination.isLast() ? page : page + 1)))
                            .build(),
                    e -> getInventory(player, this.category).open(player, pagination.next().getPage())));
            contents.set(4, 4, ClickableItem.empty(new ItemBuilder(UniversalMaterial.SIGN.getType())
                    .setDisplayName(game.translate("werewolf.menu.roles.current",
                                    Formatter.format("&current&",page),
                                                    Formatter.format("&sum&",items.size() / 27 + 1)))
                    .build()));
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


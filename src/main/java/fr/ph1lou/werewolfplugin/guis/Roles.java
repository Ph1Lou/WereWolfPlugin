package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.LoverType;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.registers.impl.RoleRegister;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Roles implements InventoryProvider {

    private Category category;

    public Roles(Player player, Category category) {
        this.category = category;
    }

    public static SmartInventory getInventory(Player player, Category category) {
        return SmartInventory.builder()
                .id("roles")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new Roles(player, category))
                .size(6, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menu.roles.name"))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = (GameManager) main.getWereWolfAPI();
        IConfiguration config = game.getConfig();

        contents.set(0, 0, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menu.return"))
                        .build()), e -> Config.INVENTORY.open(player)));

        contents.set(0, 8, ClickableItem.of((new ItemBuilder(UniversalMaterial.BARRIER.getType())
                .setDisplayName(game.translate("werewolf.menu.roles.zero")).build()), e -> {
            for (RoleRegister roleRegister : main.getRegisterManager().getRolesRegister()) {
                config.setRole(roleRegister.getKey(), 0);
            }
            config.setLoverCount(LoverType.LOVER.getKey(), 0);
            config.setLoverCount(LoverType.AMNESIAC_LOVER.getKey(), 0);
            config.setLoverCount(LoverType.CURSED_LOVER.getKey(), 0);
            game.setRoleInitialSize(0);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main=JavaPlugin.getPlugin(Main.class);
        GameManager game = (GameManager) main.getWereWolfAPI();
        IConfiguration config = game.getConfig();
        Pagination pagination = contents.pagination();

        List<String> lore = new ArrayList<>(Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right")));

        if (config.getLoverCount(LoverType.LOVER.getKey()) > 0) {
            contents.set(0, 2,
                    ClickableItem.of((
                            new ItemBuilder(
                                    UniversalMaterial.GREEN_TERRACOTTA
                                            .getStack(config.getLoverCount(LoverType.LOVER.getKey())))
                                    .setDisplayName(game.translate(LoverType.LOVER.getKey()) +
                                            game.translate("werewolf.role.lover.random"))
                                    .setLore(lore).build()), e -> {

                        if (e.isLeftClick()) {
                            config.addOneLover(LoverType.LOVER.getKey());
                        } else if (e.isRightClick()) {
                            int LoverNumber = config.getLoverCount(LoverType.LOVER.getKey());
                            if (LoverNumber > 0) {
                                config.removeOneLover(LoverType.LOVER.getKey());
                            }
                        }
                    }));
        } else
            contents.set(0, 2,
                    ClickableItem.of((
                            new ItemBuilder(
                                    UniversalMaterial.RED_TERRACOTTA
                                            .getStack())
                                    .setDisplayName(game.translate(LoverType.LOVER.getKey()) +
                                            game.translate("werewolf.role.lover.random"))
                                    .setLore(lore).build()), e -> {
                        if (e.isLeftClick()) {
                            config.addOneLover(LoverType.LOVER.getKey());
                        }

                    }));

        if (config.getLoverCount(LoverType.AMNESIAC_LOVER.getKey()) > 0) {
            contents.set(0, 4,
                    ClickableItem.of((
                            new ItemBuilder(
                                    UniversalMaterial.GREEN_TERRACOTTA
                                            .getStack(config.getLoverCount(LoverType.AMNESIAC_LOVER.getKey())))
                                    .setDisplayName(game.translate(LoverType.AMNESIAC_LOVER.getKey()))
                                    .setLore(lore).build()), e -> {
                        if (e.isLeftClick()) {
                            config.addOneLover(LoverType.AMNESIAC_LOVER.getKey());
                        } else if (e.isRightClick()) {
                            int AmnesiacLoverNumber = config.getLoverCount(LoverType.AMNESIAC_LOVER.getKey());
                            if (AmnesiacLoverNumber > 0) {
                                config.removeOneLover(LoverType.AMNESIAC_LOVER.getKey());
                            }
                        }
                    }));
        } else
            contents.set(0, 4,
                    ClickableItem.of((
                            new ItemBuilder(UniversalMaterial.RED_TERRACOTTA
                                    .getStack())
                                    .setDisplayName(game.translate(LoverType.AMNESIAC_LOVER.getKey()))
                                    .setLore(lore).build()), e -> {
                        if (e.isLeftClick()) {
                            config.addOneLover(LoverType.AMNESIAC_LOVER.getKey());
                        }

                    }));

        if (config.getLoverCount(LoverType.CURSED_LOVER.getKey()) > 0) {
            contents.set(0, 6,
                    ClickableItem.of((
                            new ItemBuilder(
                                    UniversalMaterial.GREEN_TERRACOTTA
                                            .getStack(config.getLoverCount(LoverType.CURSED_LOVER.getKey())))
                                    .setDisplayName(game.translate(LoverType.CURSED_LOVER.getKey()))
                                    .setLore(lore).build()), e -> {
                        if (e.isLeftClick()) {
                            config.addOneLover(LoverType.CURSED_LOVER.getKey());
                        } else if (e.isRightClick()) {
                            int cursedLoverNumber = config.getLoverCount(LoverType.CURSED_LOVER.getKey());
                            if (cursedLoverNumber > 0) {
                                config.removeOneLover(LoverType.CURSED_LOVER.getKey());
                            }
                        }
                    }));
        } else
            contents.set(0, 6,
                    ClickableItem.of((
                            new ItemBuilder(
                                    UniversalMaterial.RED_TERRACOTTA
                                            .getStack())
                                    .setDisplayName(game.translate(LoverType.CURSED_LOVER.getKey()))
                                    .setLore(lore).build()), e -> {

                        if (e.isLeftClick()) {
                            config.addOneLover(LoverType.CURSED_LOVER.getKey());
                        }
                    }));


        contents.set(5, 1, ClickableItem.of((new ItemBuilder(Category.WEREWOLF == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate(Camp.WEREWOLF.getKey())).setAmount(Math.max(1, count(main, Category.WEREWOLF))).build()), e -> this.category = Category.WEREWOLF));
        contents.set(5, 3, ClickableItem.of((new ItemBuilder(Category.VILLAGER == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate(Camp.VILLAGER.getKey())).setAmount(Math.max(1, count(main, Category.VILLAGER))).build()), e -> this.category = Category.VILLAGER));
        contents.set(5, 5, ClickableItem.of((new ItemBuilder(Category.NEUTRAL == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate(Camp.NEUTRAL.getKey())).setAmount(Math.max(1, count(main, Category.NEUTRAL))).build()), e -> this.category = Category.NEUTRAL));
        contents.set(5, 7, ClickableItem.of((new ItemBuilder(Category.ADDONS == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate("werewolf.categories.addons")).setAmount(Math.max(1, count(main, Category.ADDONS))).build()), e -> this.category = Category.ADDONS));


        lore.add(game.translate("werewolf.menu.shift"));

        List<ClickableItem> items = new ArrayList<>();

        main.getRegisterManager().getRolesRegister()
                .stream()
                .sorted((o1, o2) -> game.translate(o1.getKey()).compareToIgnoreCase(game.translate(o2.getKey())))
                .forEach(roleRegister -> {

                    if (roleRegister.getCategories().contains(this.category)) {

                        String key = roleRegister.getKey();
                        AtomicBoolean unRemovable = new AtomicBoolean(false);
                        List<String> lore2 = new ArrayList<>(lore);
                        roleRegister.getLoreKey()
                                .stream()
                                .map(game::translate)
                                .map(s -> Arrays.stream(s.split("\\n"))
                                        .collect(Collectors.toList())).forEach(lore2::addAll);
                        roleRegister.getRequireRoles().forEach(roleKey -> lore2.add(game.translate("werewolf.menu.roles.need",
                                Formatter.role(game.translate(roleKey)))));
                        main.getRegisterManager().getRolesRegister().stream()
                                .filter(roleRegister1 -> roleRegister1.getRequireRoles().stream()
                                        .anyMatch(requiredRole -> requiredRole.equals(roleRegister1.getKey())))
                                .map(RoleRegister::getKey)
                                .filter(roleRegister1Key -> game.getConfig().getRoleCount(roleRegister1Key) > 0)
                                .findFirst().ifPresent(role -> {
                                    lore2.add(game.translate("werewolf.menu.roles.dependant_load",
                                            Formatter.role(game.translate(role))));
                                    unRemovable.set(true);
                                });

                        Optional<String> incompatible = roleRegister
                                .getIncompatibleRoles()
                                .stream()
                                .filter(s -> game.getConfig().getRoleCount(s) > 0)
                                .map(game::translate)
                                .findFirst();

                        incompatible
                                .ifPresent(role -> lore2.add(game.translate("werewolf.menu.roles.incompatible",
                                        Formatter.role(role))));

                        if (config.getRoleCount(key) > 0) {
                            items.add(ClickableItem.of((
                                    new ItemBuilder(roleRegister.getItem().isPresent() ?
                                            roleRegister.getItem().get() :
                                            UniversalMaterial.GREEN_TERRACOTTA.getStack())
                                            .setAmount(config.getRoleCount(key))
                                            .setLore(lore2)
                                            .setDisplayName(game.translate(roleRegister.getKey()))
                                            .build()), e -> {

                                if (e.isShiftClick()) {
                                    AdvancedRoleMenu.getInventory(roleRegister).open(player);
                                } else if (e.isLeftClick()) {
                                    selectPlus(game, key);
                                } else if (e.isRightClick()) {
                                    int count = game.getConfig().getRoleCount(key);
                                    if (!unRemovable.get() || count > 1) {
                                        if (roleRegister.isRequireDouble() && count == 2) {
                                            selectMinus(game, key);
                                        }
                                        selectMinus(game, key);
                                    }
                                }
                            }));
                        } else {

                            if (roleRegister.getItem().isPresent()) {
                                lore2.add(0, game.translate("werewolf.utils.none"));
                            }

                            items.add(ClickableItem.of((new ItemBuilder(roleRegister.getItem().isPresent() ?
                                    roleRegister.getItem().get() :
                                    UniversalMaterial.RED_TERRACOTTA.getStack())
                                    .setAmount(1)
                                    .setLore(lore2)
                                    .setDisplayName(game.translate(roleRegister.getKey())).build()), e -> {

                                if (e.isShiftClick()) {
                                    AdvancedRoleMenu.getInventory(roleRegister).open(player);
                                } else if (e.isLeftClick()) {
                                    if(incompatible.isPresent()){
                                        return;
                                    }
                                    if (roleRegister.getRequireRoles().stream()
                                            .anyMatch(requireRole -> game.getConfig().getRoleCount(requireRole) == 0)) {
                                        return;
                                    }
                                    if (roleRegister.isRequireDouble()) {
                                        selectPlus(game, key);
                                    }
                                    selectPlus(game, key);
                                }
                            }));
                        }

                    }
                });

        if (items.size() > 36) {
            pagination.setItems(items.toArray(new ClickableItem[0]));
            pagination.setItemsPerPage(27);
            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));
            int page = pagination.getPage() + 1;
            contents.set(4, 0, null);
            contents.set(4, 1, null);
            contents.set(4, 3, null);
            contents.set(4, 5, null);
            contents.set(4, 7, null);
            contents.set(4, 8, null);
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




    public void selectMinus(GameManager game, String key) {
        IConfiguration config = game.getConfig();
        if (config.getRoleCount(key) > 0) {
            game.setRoleInitialSize(game.getRoleInitialSize() - 1);
            config.removeOneRole(key);
        }
    }

    public void selectPlus(GameManager game, String key) {
        IConfiguration config = game.getConfig();
        config.addOneRole(key);
        game.setRoleInitialSize(game.getRoleInitialSize() + 1);
    }

    private int count(GetWereWolfAPI main, Category category) {
        int i = 0;
        for (RoleRegister roleRegister : main.getRegisterManager().getRolesRegister()) {
            if (roleRegister.getCategories().contains(category)) {
                i += main.getWereWolfAPI().getConfig().getRoleCount(roleRegister.getKey());
            }

        }
        return i;
    }
}


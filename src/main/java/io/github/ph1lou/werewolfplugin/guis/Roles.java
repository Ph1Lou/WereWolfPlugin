package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IStuffManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Category;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfplugin.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Roles implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("roles")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Roles())
            .size(6, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menu.roles.name"))
            .closeable(true)
            .build();

    private final Map<UUID, Category> categories = new HashMap<>();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        IConfiguration config = game.getConfig();

        contents.set(0, 0, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menu.return"))
                        .build()), e -> Config.INVENTORY.open(player)));

        /*contents.set(0, 5, ClickableItem.of((new ItemBuilder(UniversalMaterial.ARROW.getType())
                .setDisplayName(game.translate("werewolf.menu.return")).build()),
                e -> {
                    ((Configuration)game.getConfig()).setComposition(((GameManager)game).getRandomConfig().createRandomConfig(game.getConfig().getLoverCount(LoverType.CURSED_LOVER.getKey()),new HashSet<>(),false));
                    game.getScore().setRole(game.getConfig().getLoverCount(LoverType.CURSED_LOVER.getKey()));
                }));*/

        contents.set(0, 8, ClickableItem.of((new ItemBuilder(UniversalMaterial.BARRIER.getType()).setDisplayName(game.translate("werewolf.menu.roles.zero")).build()), e -> {
            for (RoleRegister roleRegister : main.getRegisterManager().getRolesRegister()) {
                config.setRole(roleRegister.getKey(), 0);
            }
            config.setLoverCount(LoverType.LOVER.getKey(), 0);
            config.setLoverCount(LoverType.AMNESIAC_LOVER.getKey(), 0);
            config.setLoverCount(LoverType.CURSED_LOVER.getKey(), 0);
            game.getScore().setRole(0);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main=JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        IConfiguration config = game.getConfig();
        Pagination pagination = contents.pagination();
        UUID uuid = player.getUniqueId();

        List<String> lore = new ArrayList<>(Arrays.asList(game.translate("werewolf.menu.left"), game.translate("werewolf.menu.right")));

        if (config.getLoverCount(LoverType.LOVER.getKey()) > 0) {
            contents.set(0, 2,
                    ClickableItem.of((
                            new ItemBuilder(
                                    UniversalMaterial.GREEN_TERRACOTTA
                                            .getStack(config.getLoverCount(LoverType.LOVER.getKey())))
                                    .setDisplayName(game.translate(LoverType.LOVER.getKey()) + game.translate("werewolf.role.lover.random"))
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
                                    .setDisplayName(game.translate(LoverType.LOVER.getKey()))
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


        contents.set(5, 1, ClickableItem.of((new ItemBuilder(Category.WEREWOLF == this.categories.getOrDefault(uuid, Category.WEREWOLF) ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.werewolf")).setAmount(Math.max(1, count(main, Category.WEREWOLF))).build()), e -> this.categories.put(uuid, Category.WEREWOLF)));
        contents.set(5, 3, ClickableItem.of((new ItemBuilder(Category.VILLAGER == this.categories.getOrDefault(uuid, Category.WEREWOLF) ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.villager")).setAmount(Math.max(1, count(main, Category.VILLAGER))).build()), e -> this.categories.put(uuid, Category.VILLAGER)));
        contents.set(5, 5, ClickableItem.of((new ItemBuilder(Category.NEUTRAL == this.categories.getOrDefault(uuid, Category.WEREWOLF) ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.neutral")).setAmount(Math.max(1, count(main, Category.NEUTRAL))).build()), e -> this.categories.put(uuid, Category.NEUTRAL)));
        contents.set(5, 7, ClickableItem.of((new ItemBuilder(Category.ADDONS == this.categories.getOrDefault(uuid, Category.WEREWOLF) ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.addons")).setAmount(Math.max(1, count(main, Category.ADDONS))).build()), e -> this.categories.put(uuid, Category.ADDONS)));


        lore.add(game.translate("werewolf.menu.shift"));

        List<ClickableItem> items = new ArrayList<>();


        for (RoleRegister roleRegister : main.getRegisterManager().getRolesRegister()) {

            if (roleRegister.getCategories().contains(categories.getOrDefault(uuid, Category.WEREWOLF))) {

                String key = roleRegister.getKey();
                AtomicBoolean unRemovable = new AtomicBoolean(false);
                List<String> lore2 = new ArrayList<>(lore);
                roleRegister.getLoreKey().stream().map(game::translate).map(s -> Arrays.stream(s.split("\\n")).collect(Collectors.toList())).forEach(lore2::addAll);
                roleRegister.getRequireRole().ifPresent(roleKey -> lore2.add(game.translate("werewolf.menu.roles.need", game.translate(roleKey))));
                main.getRegisterManager().getRolesRegister().stream()
                        .filter(roleRegister1 -> roleRegister1.getRequireRole().isPresent())
                        .filter(roleRegister1 -> game.getConfig().getRoleCount(roleRegister1.getKey()) > 0)
                        .filter(roleRegister1 -> roleRegister1.getRequireRole().get().equals(key))
                        .map(RoleRegister::getKey)
                        .findFirst().ifPresent(s -> {
                    lore2.add(game.translate("werewolf.menu.roles.dependant_load", game.translate(s)));
                    unRemovable.set(true);
                });

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
                            manageStuff(main, player, key);
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
                            manageStuff(main, player, key);
                        } else if (e.isLeftClick()) {
                            if (roleRegister.getRequireRole().isPresent()) {
                                if (game.getConfig().getRoleCount(roleRegister.getRequireRole().get()) == 0) {
                                    return;
                                }
                            }
                            if (roleRegister.isRequireDouble()) {
                                selectPlus(game, key);
                            }
                            selectPlus(game, key);
                        }
                    }));
                }

            }
        }
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

    private void manageStuff(Main main, Player player, String key) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();

        if (!game.getModerationManager()
                .checkAccessAdminCommand("werewolf.commands.admin.loot_role.command",
                        player)) {
            return;
        }

        IStuffManager stuffManager = game.getStuffs();
        PlayerInventory inventory = player.getInventory();
        player.setGameMode(GameMode.CREATIVE);

        if (!stuffManager.getTempStuff().containsKey(uuid)) {

            Inventory inventoryTemp = Bukkit.createInventory(player, 45);
            for (int j = 0; j < 40; j++) {
                inventoryTemp.setItem(j, inventory.getItem(j));
            }
            stuffManager.getTempStuff().put(uuid, inventoryTemp);
        }

        for (int j = 0; j < 40; j++) {
            inventory.setItem(j, null);
        }

        for (ItemStack item : game.getStuffs().getStuffRoles().get(key)) {
            if (item != null) {
                player.getInventory().addItem(item);
            }
        }
        TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.loot_role.valid"));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/a %s %s", game.translate("werewolf.commands.admin.loot_role.command"), key)));
        player.spigot().sendMessage(msg);
        player.closeInventory();
    }


    public void selectMinus(WereWolfAPI game, String key) {
        IConfiguration config = game.getConfig();
        if (config.getRoleCount(key) > 0) {
            game.getScore().setRole(game.getScore().getRole() - 1);
            config.removeOneRole(key);
        }
    }

    public void selectPlus(WereWolfAPI game, String key) {
        IConfiguration config = game.getConfig();
        config.addOneRole(key);
        game.getScore().setRole(game.getScore().getRole() + 1);
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


package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfapi.enumlg.Category;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.ItemBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Roles implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("roles")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Roles())
            .size(6, 9)
            .title(JavaPlugin.getPlugin(Main.class).getCurrentGame().translate("werewolf.menu.roles.name"))
            .closeable(true)
            .build();

    private final Map<UUID, Category> categories = new HashMap<>();


    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();
        ConfigWereWolfAPI config = game.getConfig();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));
        contents.set(0, 8, ClickableItem.of((new ItemBuilder(UniversalMaterial.BARRIER.getType()).setDisplayName(game.translate("werewolf.menu.roles.zero")).build()), e -> {
            for (RoleRegister roleRegister : game.getRolesRegister()) {
                config.getRoleCount().put(roleRegister.getKey(), 0);
            }
            config.setAmnesiacLoverSize(0);
            config.setLoverSize(0);
            config.setCursedLoverSize(0);
            game.getScore().setRole(0);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();
        ConfigWereWolfAPI config = game.getConfig();
        Pagination pagination = contents.pagination();
        UUID uuid = player.getUniqueId();

        List<String> lore = new ArrayList<>(Arrays.asList(game.translate("werewolf.menu.left"), game.translate("werewolf.menu.right")));

        if (config.getLoverSize() > 0) {
            contents.set(0, 2, ClickableItem.of((new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack(config.getLoverSize())).setDisplayName(game.translate("werewolf.role.lover.display")).setLore(lore).build()), e -> {

                if (e.isLeftClick()) {
                    config.setLoverSize(config.getLoverSize() + 1);
                } else if (e.isRightClick()) {
                    int LoverNumber = config.getLoverSize();
                    if (LoverNumber > 0) {
                        config.setLoverSize(LoverNumber - 1);
                    }
                }
            }));
        } else
            contents.set(0, 2, ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack()).setDisplayName(game.translate("werewolf.role.lover.display")).setLore(lore).build()), e -> {
                if (e.isLeftClick()) {
                    config.setLoverSize(config.getLoverSize() + 1);
                }

            }));

        if (config.getAmnesiacLoverSize() > 0) {
            contents.set(0, 4, ClickableItem.of((new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack(config.getAmnesiacLoverSize())).setDisplayName(game.translate("werewolf.role.amnesiac_lover.display")).setLore(lore).build()), e -> {
                if (e.isLeftClick()) {
                    config.setAmnesiacLoverSize(config.getAmnesiacLoverSize() + 1);
                } else if (e.isRightClick()) {
                    int AmnesiacLoverNumber = config.getAmnesiacLoverSize();
                    if (AmnesiacLoverNumber > 0) {
                        config.setAmnesiacLoverSize(AmnesiacLoverNumber - 1);
                    }
                }
            }));
        } else
            contents.set(0, 4, ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack()).setDisplayName(game.translate("werewolf.role.amnesiac_lover.display")).setLore(lore).build()), e -> {
                if (e.isLeftClick()) {
                    config.setAmnesiacLoverSize(config.getAmnesiacLoverSize() + 1);
                }

            }));

        if (config.getCursedLoverSize() > 0) {
            contents.set(0, 6, ClickableItem.of((new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack(config.getCursedLoverSize())).setDisplayName(game.translate("werewolf.role.cursed_lover.display")).setLore(lore).build()), e -> {
                if (e.isLeftClick()) {
                    config.setCursedLoverSize(config.getCursedLoverSize() + 1);
                } else if (e.isRightClick()) {
                    int cursedLoverNumber = config.getCursedLoverSize();
                    if (cursedLoverNumber > 0) {
                        config.setCursedLoverSize(cursedLoverNumber - 1);
                    }
                }
            }));
        } else
            contents.set(0, 6, ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack()).setDisplayName(game.translate("werewolf.role.cursed_lover.display")).setLore(lore).build()), e -> {

                if (e.isLeftClick()) {
                    config.setCursedLoverSize(config.getCursedLoverSize() + 1);
                }
            }));


        contents.set(5, 1, ClickableItem.of((new ItemBuilder(Category.WEREWOLF == this.categories.getOrDefault(uuid, Category.WEREWOLF) ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.werewolf")).setAmount(Math.max(1, count(game, Category.WEREWOLF))).build()), e -> this.categories.put(uuid, Category.WEREWOLF)));
        contents.set(5, 3, ClickableItem.of((new ItemBuilder(Category.VILLAGER == this.categories.getOrDefault(uuid, Category.WEREWOLF) ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.villager")).setAmount(Math.max(1, count(game, Category.VILLAGER))).build()), e -> this.categories.put(uuid, Category.VILLAGER)));
        contents.set(5, 5, ClickableItem.of((new ItemBuilder(Category.NEUTRAL == this.categories.getOrDefault(uuid, Category.WEREWOLF) ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.neutral")).setAmount(Math.max(1, count(game, Category.NEUTRAL))).build()), e -> this.categories.put(uuid, Category.NEUTRAL)));
        contents.set(5, 7, ClickableItem.of((new ItemBuilder(Category.ADDONS == this.categories.getOrDefault(uuid, Category.WEREWOLF) ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplayName(game.translate("werewolf.categories.addons")).setAmount(Math.max(1, count(game, Category.ADDONS))).build()), e -> this.categories.put(uuid, Category.ADDONS)));


        lore.add(game.translate("werewolf.menu.shift"));

        List<ClickableItem> items = new ArrayList<>();


        for (RoleRegister roleRegister : game.getRolesRegister()) {

            if (roleRegister.getCategories().contains(categories.getOrDefault(uuid, Category.WEREWOLF))) {

                String key = roleRegister.getKey();
                List<String> lore2 = new ArrayList<>(lore);
                lore2.addAll(roleRegister.getLore());

                if (config.getRoleCount().get(key) > 0) {
                    items.add(ClickableItem.of((new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack(config.getRoleCount().get(key))).setLore(lore2).setDisplayName(roleRegister.getName()).build()), e -> {

                        if (e.isShiftClick()) {

                            player.setGameMode(GameMode.CREATIVE);
                            player.getInventory().clear();

                            for (ItemStack item : game.getStuffs().getStuffRoles().get(key)) {
                                if (item != null) {
                                    player.getInventory().addItem(item);
                                }
                            }
                            TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.loot_role.valid"));
                            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a stuffRole " + key));
                            player.spigot().sendMessage(msg);
                            player.closeInventory();
                        } else if (e.isLeftClick()) {
                            selectPlus(game, roleRegister.getKey());
                        } else if (e.isRightClick()) {
                            selectMinus(game, roleRegister.getKey());
                        }


                    }));
                } else
                    items.add(ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack()).setLore(lore2).setDisplayName(roleRegister.getName()).build()), e -> {

                        if (e.isShiftClick()) {
                            player.setGameMode(GameMode.CREATIVE);
                            player.getInventory().clear();

                            for (ItemStack item : game.getStuffs().getStuffRoles().get(key)) {
                                if (item != null) {
                                    player.getInventory().addItem(item);
                                }
                            }
                            TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.loot_role.valid"));
                            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a stuffRole " + key));
                            player.spigot().sendMessage(msg);
                            player.closeInventory();
                        } else if (e.isLeftClick()) {
                            selectPlus(game, roleRegister.getKey());
                        }
                    }));
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


    public void selectMinus(GameManager game, String key) {
        ConfigWereWolfAPI config = game.getConfig();
        int j = config.getRoleCount().get(key);
        if (j > 0) {
            game.getScore().setRole(game.getScore().getRole() - 1);
            config.getRoleCount().put(key, j - 1);
        }
    }

    public void selectPlus(GameManager game, String key) {
        ConfigWereWolfAPI config = game.getConfig();
        int j = config.getRoleCount().get(key);

        config.getRoleCount().put(key, j + 1);
        game.getScore().setRole(game.getScore().getRole() + 1);
    }

    private int count(GameManager game, Category category) {
        int i = 0;
        for (RoleRegister roleRegister : game.getRolesRegister()) {
            if (roleRegister.getCategories().contains(category)) {
                i += game.getConfig().getRoleCount().get(roleRegister.getKey());
            }

        }
        return i;
    }
}


package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.Register;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class RolesGUI implements InventoryProvider {

    private Category category;

    public RolesGUI(Player player, Category category) {
        this.category = category;
    }

    public static SmartInventory getInventory(Player player, Category category) {

        Main main = JavaPlugin.getPlugin(Main.class);
        return SmartInventory.builder()
                .id("roles")
                .manager(main.getInvManager())
                .provider(new RolesGUI(player, category))
                .size(6, 9)
                .title(main.getWereWolfAPI().translate("werewolf.menus.roles.name"))
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
                        .setDisplayName(game.translate("werewolf.menus.return"))
                        .build()), e -> MainGUI.INVENTORY.open(player)));

        contents.set(0, 8, ClickableItem.of((new ItemBuilder(UniversalMaterial.BARRIER.getType())
                .setDisplayName(game.translate("werewolf.menus.roles.zero")).build()), e -> {
            for (Wrapper<IRole, Role> roleRegister : main.getRegisterManager().getRolesRegister()) {
                config.setRole(roleRegister.getMetaDatas().key(), 0);
            }
            config.setLoverCount(LoverBase.LOVER, 0);
            config.setLoverCount(LoverBase.AMNESIAC_LOVER, 0);
            config.setLoverCount(LoverBase.CURSED_LOVER, 0);
            game.setRoleInitialSize(0);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = (GameManager) main.getWereWolfAPI();
        IConfiguration config = game.getConfig();
        Pagination pagination = contents.pagination();

        List<String> lore = new ArrayList<>(Arrays.asList(game.translate("werewolf.menus.lore.left"),
                game.translate("werewolf.menus.lore.right")));

        if (config.getLoverCount(LoverBase.LOVER) > 0) {
            contents.set(0, 2,
                    ClickableItem.of((
                            new ItemBuilder(
                                    UniversalMaterial.GREEN_TERRACOTTA
                                            .getStack(config.getLoverCount(LoverBase.LOVER)))
                                    .setDisplayName(game.translate(LoverBase.LOVER) +
                                            game.translate("werewolf.lovers.lover.random"))
                                    .setLore(lore).build()), e -> {

                        if (e.isLeftClick()) {
                            config.addOneLover(LoverBase.LOVER);
                        } else if (e.isRightClick()) {
                            int LoverNumber = config.getLoverCount(LoverBase.LOVER);
                            if (LoverNumber > 0) {
                                config.removeOneLover(LoverBase.LOVER);
                            }
                        }
                    }));
        } else
            contents.set(0, 2,
                    ClickableItem.of((
                            new ItemBuilder(
                                    UniversalMaterial.RED_TERRACOTTA
                                            .getStack())
                                    .setDisplayName(game.translate(LoverBase.LOVER) +
                                            game.translate("werewolf.lovers.lover.random"))
                                    .setLore(lore).build()), e -> {
                        if (e.isLeftClick()) {
                            config.addOneLover(LoverBase.LOVER);
                        }

                    }));

        if (config.getLoverCount(LoverBase.AMNESIAC_LOVER) > 0) {
            contents.set(0, 4,
                    ClickableItem.of((
                            new ItemBuilder(
                                    UniversalMaterial.GREEN_TERRACOTTA
                                            .getStack(config.getLoverCount(LoverBase.AMNESIAC_LOVER)))
                                    .setDisplayName(game.translate(LoverBase.AMNESIAC_LOVER))
                                    .setLore(lore).build()), e -> {
                        if (e.isLeftClick()) {
                            config.addOneLover(LoverBase.AMNESIAC_LOVER);
                        } else if (e.isRightClick()) {
                            int AmnesiacLoverNumber = config.getLoverCount(LoverBase.AMNESIAC_LOVER);
                            if (AmnesiacLoverNumber > 0) {
                                config.removeOneLover(LoverBase.AMNESIAC_LOVER);
                            }
                        }
                    }));
        } else
            contents.set(0, 4,
                    ClickableItem.of((
                            new ItemBuilder(UniversalMaterial.RED_TERRACOTTA
                                    .getStack())
                                    .setDisplayName(game.translate(LoverBase.AMNESIAC_LOVER))
                                    .setLore(lore).build()), e -> {
                        if (e.isLeftClick()) {
                            config.addOneLover(LoverBase.AMNESIAC_LOVER);
                        }

                    }));

        if (config.getLoverCount(LoverBase.CURSED_LOVER) > 0) {
            contents.set(0, 6,
                    ClickableItem.of((
                            new ItemBuilder(
                                    UniversalMaterial.GREEN_TERRACOTTA
                                            .getStack(config.getLoverCount(LoverBase.CURSED_LOVER)))
                                    .setDisplayName(game.translate(LoverBase.CURSED_LOVER))
                                    .setLore(lore).build()), e -> {
                        if (e.isLeftClick()) {
                            config.addOneLover(LoverBase.CURSED_LOVER);
                        } else if (e.isRightClick()) {
                            int cursedLoverNumber = config.getLoverCount(LoverBase.CURSED_LOVER);
                            if (cursedLoverNumber > 0) {
                                config.removeOneLover(LoverBase.CURSED_LOVER);
                            }
                        }
                    }));
        } else
            contents.set(0, 6,
                    ClickableItem.of((
                            new ItemBuilder(
                                    UniversalMaterial.RED_TERRACOTTA
                                            .getStack())
                                    .setDisplayName(game.translate(LoverBase.CURSED_LOVER))
                                    .setLore(lore).build()), e -> {

                        if (e.isLeftClick()) {
                            config.addOneLover(LoverBase.CURSED_LOVER);
                        }
                    }));


        contents.set(5, 1, ClickableItem.of((new ItemBuilder(Category.WEREWOLF == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate(Camp.WEREWOLF.getKey())).setAmount(Math.max(1, count(game, Category.WEREWOLF))).build()), e -> this.category = Category.WEREWOLF));
        contents.set(5, 3, ClickableItem.of((new ItemBuilder(Category.VILLAGER == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate(Camp.VILLAGER.getKey())).setAmount(Math.max(1, count(game, Category.VILLAGER))).build()), e -> this.category = Category.VILLAGER));
        contents.set(5, 5, ClickableItem.of((new ItemBuilder(Category.NEUTRAL == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate(Camp.NEUTRAL.getKey())).setAmount(Math.max(1, count(game, Category.NEUTRAL))).build()), e -> this.category = Category.NEUTRAL));
        contents.set(5, 7, ClickableItem.of((new ItemBuilder(Category.ADDONS == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate("werewolf.categories.addons")).setAmount(Math.max(1, count(game, Category.ADDONS))).build()), e -> this.category = Category.ADDONS));


        lore.add(game.translate("werewolf.menus.lore.shift"));

        List<ClickableItem> items = new ArrayList<>();

        main.getRegisterManager().getRolesRegister()
                .stream()
                .sorted((o1, o2) -> game.translate(o1.getMetaDatas().key())
                        .compareToIgnoreCase(game.translate(o2.getMetaDatas().key())))
                .forEach(roleRegister -> {

                    Optional<String> addonKey = Register.get().getModuleKey(roleRegister.getMetaDatas().key());
                    if (roleRegister.getMetaDatas().category() == this.category ||
                            (addonKey.isPresent() &&
                                    !addonKey.get().equals(Main.KEY) &&
                                    this.category == Category.ADDONS)) {

                        String key = roleRegister.getMetaDatas().key();
                        AtomicBoolean unRemovable = new AtomicBoolean(false);
                        List<String> lore2 = new ArrayList<>(lore);
                        Aura aura = roleRegister.getMetaDatas().defaultAura();
                        lore2.add(game.translate("werewolf.commands.player.aura.menu_role",
                                Formatter.format("&aura&",aura.getChatColor() + game.translate(aura.getKey()))));

                        if (game.getConfig().getRoleCount(key) > 0) {
                            lore2.addAll(AdvancedConfigurationUtils.getLore(game,
                                    roleRegister.getMetaDatas().loreKey(),
                                    roleRegister.getMetaDatas().configurations(),
                                    roleRegister.getMetaDatas().timers(),
                                    roleRegister.getMetaDatas().configValues(),
                                    new ConfigurationBasic[]{}));
                        }
                        Arrays.stream(roleRegister.getMetaDatas().requireRoles())
                                .forEach(roleKey -> lore2.add(game.translate("werewolf.menus.roles.need",
                                        Formatter.role(game.translate(roleKey)))));
                        main.getRegisterManager().getRolesRegister().stream()
                                .filter(roleRegister1 -> Arrays.stream(roleRegister1.getMetaDatas().requireRoles())
                                        .anyMatch(requiredRole -> requiredRole.equals(roleRegister1.getMetaDatas().key())))
                                .map(iRoleRoleWrapper -> iRoleRoleWrapper.getMetaDatas().key())
                                .filter(roleRegister1Key -> game.getConfig().getRoleCount(roleRegister1Key) > 0)
                                .findFirst().ifPresent(role -> {
                                    lore2.add(game.translate("werewolf.menus.roles.dependant_load",
                                            Formatter.role(game.translate(role))));
                                    unRemovable.set(true);
                                });

                        Optional<String> incompatible = Arrays.stream(roleRegister.getMetaDatas().incompatibleRoles())
                                .filter(s -> game.getConfig().getRoleCount(s) > 0)
                                .map(game::translate)
                                .findFirst();

                        incompatible
                                .ifPresent(role -> lore2.add(game.translate("werewolf.menus.roles.incompatible",
                                        Formatter.role(role))));

                        if (config.getRoleCount(key) > 0) {
                            items.add(ClickableItem.of((
                                    new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack())
                                            .setAmount(config.getRoleCount(key))
                                            .setLore(lore2)
                                            .setDisplayName(game.translate(key))
                                            .build()), e -> {

                                if (e.isShiftClick()) {
                                    AdvancedRolesGUI.getInventory(roleRegister.getMetaDatas(), pagination.getPage()).open(player);
                                } else if (e.isLeftClick()) {
                                    selectPlus(game, key);
                                } else if (e.isRightClick()) {
                                    int count = game.getConfig().getRoleCount(key);
                                    if (!unRemovable.get() || count > 1) {
                                        if (roleRegister.getMetaDatas().requireDouble() && count == 2) {
                                            selectMinus(game, key);
                                        }
                                        selectMinus(game, key);
                                    }
                                }
                            }));
                        } else {

                            items.add(ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack())
                                    .setAmount(1)
                                    .setLore(lore2)
                                    .setDisplayName(game.translate(key)).build()), e -> {

                                if (e.isShiftClick()) {
                                    AdvancedRolesGUI.getInventory(roleRegister.getMetaDatas(), pagination.getPage()).open(player);
                                } else if (e.isLeftClick()) {
                                    if (incompatible.isPresent()) {
                                        return;
                                    }
                                    if (Arrays.stream(roleRegister.getMetaDatas().requireRoles())
                                            .anyMatch(requireRole -> game.getConfig().getRoleCount(requireRole) == 0)) {
                                        return;
                                    }
                                    if (roleRegister.getMetaDatas().requireDouble()) {
                                        selectPlus(game, key);
                                    }
                                    selectPlus(game, key);
                                }
                            }));
                        }

                    }
                });

        InventoryUtils.fillInventory(game, items, pagination, contents, () -> getInventory(player, this.category), 36);
    }

    public void selectMinus(GameManager game, String key) {

        if (game.isState(StateGame.GAME)) return;

        IConfiguration config = game.getConfig();
        if (config.getRoleCount(key) > 0) {
            game.setRoleInitialSize(game.getRoleInitialSize() - 1);
            config.removeOneRole(key);
        }
    }

    public void selectPlus(GameManager game, String key) {

        if (game.isState(StateGame.GAME)) return;

        IConfiguration config = game.getConfig();
        config.addOneRole(key);
        game.setRoleInitialSize(game.getRoleInitialSize() + 1);
    }

    private int count(WereWolfAPI game, Category category) {
        int i = 0;
        for (Wrapper<IRole, Role> roleRegister : Register.get().getRolesRegister()) {
            if (roleRegister.getMetaDatas().category() == category) {
                i += game.getConfig().getRoleCount(roleRegister.getMetaDatas().key());
            }

        }
        return i;
    }
}


package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.registers.impl.RoleRegister;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AdvancedConfigRole implements InventoryProvider {


    public static SmartInventory getInventory() {

        Main main = JavaPlugin.getPlugin(Main.class);

        return SmartInventory.builder()
                .id("advanced_config_role")
                .manager(main.getInvManager())
                .provider(new AdvancedConfigRole())
                .size(6, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menu.advanced_config_roles.name"))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        Pagination pagination = contents.pagination();
        List<ClickableItem> items = new ArrayList<>();

        for (RoleRegister roleRegister : main.getRegisterManager().getRolesRegister()) {

            for (Function<WereWolfAPI, ClickableItem> item : roleRegister.getConfig()) {

                items.add(item.apply(game));
            }

            items.addAll(getTimersRole(main, roleRegister));
            items.addAll(getConfigsRole(main, roleRegister));
        }

        if (items.size() > 45) {
            pagination.setItems(items.toArray(new ClickableItem[0]));
            pagination.setItemsPerPage(36);
            pagination.addToIterator(contents.newIterator(
                    SlotIterator.Type.HORIZONTAL, 1, 0));
            int page = pagination.getPage() + 1;
            contents.set(5, 0, null);
            contents.set(5, 1, null);
            contents.set(5, 3, null);
            contents.set(5, 5, null);
            contents.set(5, 7, null);
            contents.set(5, 8, null);
            contents.set(5, 2, ClickableItem.of(
                    new ItemBuilder(Material.ARROW)
                            .setDisplayName(
                                    game.translate(
                                            "werewolf.menu.roles.previous",
                                            Formatter.format("&current&",page),
                                            Formatter.format("&previous&",pagination.isFirst() ?
                                                    page : page - 1))).build(),

                    e -> getInventory().open(player, pagination
                            .previous().getPage())));
            contents.set(5, 6, ClickableItem.of(
                    new ItemBuilder(Material.ARROW)
                            .setDisplayName(
                                    game.translate("werewolf.menu.roles.next",
                                            Formatter.format("&current&",page),
                                            Formatter.format("&next&",pagination.isLast() ?
                                                    page : page + 1))).build(),
                    e -> getInventory().open(player, pagination
                            .next().getPage())));

            contents.set(5, 4, ClickableItem.empty(
                    new ItemBuilder(UniversalMaterial.SIGN.getType())
                            .setDisplayName(
                                    game.translate("werewolf.menu.roles.current",
                                                    Formatter.format("&current&",page),
                                                    Formatter.format("&sum&",items.size() / 36 + 1)))
                            .build()));
        } else {
            int i = 0;
            for (ClickableItem clickableItem : items) {
                contents.set(i / 9 + 1, i % 9, clickableItem);
                i++;
            }
            for (int k = i; k < (i / 9 + 1) * 9; k++) {
                contents.set(k / 9 + 1, k % 9, null);
            }
        }
    }

    public static List<ClickableItem> getConfigsRole(GetWereWolfAPI main, RoleRegister roleRegister){
        WereWolfAPI game = main.getWereWolfAPI();
        return main.getRegisterManager().getConfigsRegister().stream()
                .filter(configRegister -> configRegister.getRoleKey().isPresent())
                .filter(configRegister -> configRegister.getRoleKey().get().equals(roleRegister.getKey()))
                .map(configRegister -> GlobalConfigs.getClickableItem(game, configRegister))
                .collect(Collectors.toList());
    }

    public static List<ClickableItem> getTimersRole(GetWereWolfAPI main, RoleRegister roleRegister){

        WereWolfAPI game = main.getWereWolfAPI();
        return main.getRegisterManager().getTimersRegister().stream()
                .filter(timerRegister -> timerRegister.getRoleKey().isPresent())
                .filter(timerRegister -> timerRegister.getRoleKey().get().equals(roleRegister.getKey()))
                .map(timerRegister -> {

                    IConfiguration config = game.getConfig();
                    List<String> lore = new ArrayList<>(Arrays.asList(game.translate("werewolf.menu.left"),
                            game.translate("werewolf.menu.right")));
                    timerRegister.getLoreKey().stream()
                            .map(game::translate)
                            .map(s -> Arrays.stream(s.split("\\n"))
                                    .collect(Collectors.toList()))
                            .forEach(lore::addAll);
                    return ClickableItem.of(new ItemBuilder(UniversalMaterial.ANVIL.getStack())
                            .setLore(lore)
                            .setDisplayName(game.translate(timerRegister.getKey(),
                                    Formatter.timer(Utils.conversion(config.getTimerValue(timerRegister.getKey())))))
                            .build(),e -> {


                        if (e.isLeftClick()) {
                            config.moveTimer(timerRegister.getKey(), timerRegister.getPitch());
                        } else if (config.getTimerValue(timerRegister.getKey()) - timerRegister.getPitch() > 0) {
                            config.moveTimer(timerRegister.getKey(), - timerRegister.getPitch());
                        }

                        e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                .setLore(lore)
                                .setDisplayName(game.translate(timerRegister.getKey(),
                                        Formatter.timer(Utils.conversion(config.getTimerValue(timerRegister.getKey())))))
                                .build());
                    });
                }).collect(Collectors.toList());
    }
}


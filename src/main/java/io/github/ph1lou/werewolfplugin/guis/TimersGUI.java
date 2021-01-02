package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.registers.TimerRegister;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TimersGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("timers")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new TimersGUI())
            .size(Math.min(54, (JavaPlugin.getPlugin(Main.class).getRegisterManager().getTimersRegister().size() / 9 + 2) * 9) / 9, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menu.timers.name"))
            .closeable(true)
            .build();


    private String key = "werewolf.menu.timers.invulnerability";

    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        contents.set(0, 0, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(
                                game.translate("werewolf.menu.return"))
                        .build()), e -> Config.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = (GameManager) main.getWereWolfAPI();
        ConfigWereWolfAPI config = game.getConfig();
        Pagination pagination = contents.pagination();
        List<ClickableItem> items = new ArrayList<>();

        String c = getConversion(game, key);

        contents.set(0, 1, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)

                        .setDisplayName(game.translate("werewolf.utils.display",
                                "-10m", c))
                        .build()), e -> {

            config.moveTimer(key, -600);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.utils.display",
                            "-10m", getConversion(game, key)))
                    .build());

        }));
        contents.set(0, 2, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)
                        .setDisplayName(game.translate("werewolf.utils.display",
                                "-1m", c))
                        .build()), e -> {

            config.moveTimer(key, -60);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.utils.display",
                            "-1m", getConversion(game, key)))
                    .build());

        }));
        contents.set(0, 3, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)
                        .setDisplayName(game.translate("werewolf.utils.display",
                                "-10s", c))
                        .build()), e -> {

            config.moveTimer(key, -10);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.utils.display",
                            "-10s", getConversion(game, key)))
                    .build());

        }));
        contents.set(0, 5, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)
                        .setDisplayName(
                                game.translate("werewolf.utils.display",
                                        "+10s", c))
                        .build()), e -> {

            config.moveTimer(key, 10);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(
                            game.translate("werewolf.utils.display",
                                    "+10s", getConversion(game, key)))
                    .build());

        }));
        contents.set(0, 6, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)
                        .setDisplayName(game.translate("werewolf.utils.display",
                                "+1m", c))
                        .build()), e -> {

            config.moveTimer(key, 60);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.utils.display",
                            "+1m", getConversion(game, key)))
                    .build());

        }));
        contents.set(0, 7, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)
                        .setDisplayName(game.translate("werewolf.utils.display",
                                "+10m", c))

                        .build()), e -> {
            config.moveTimer(key, 600);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.utils.display",
                            "+10m", getConversion(game, key)))
                    .build());
        }));


        for (TimerRegister timer : main.getRegisterManager().getTimersRegister()) {
            List<String> lore = new ArrayList<>();
            timer.getLoreKey().stream().map(game::translate).map(s -> Arrays.stream(s.split("\\n")).collect(Collectors.toList())).forEach(lore::addAll);

            if (game.getConfig().getTimerValue(timer.getKey()) >= 0 || game.isDebug()) {

                items.add(ClickableItem.of((new ItemBuilder(timer.getKey().equals(key) ?
                                Material.FEATHER :
                                Material.ANVIL)
                                .setLore(lore)
                                .setDisplayName(game.translate(timer.getKey(),
                                        game.getScore().conversion(config.getTimerValue(timer.getKey())))).build()),
                        e -> this.key = timer.getKey()));
            }

        }

        if (items.size() > 45) {
            pagination.setItems(items.toArray(new ClickableItem[0]));
            pagination.setItemsPerPage(36);
            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));
            int page = pagination.getPage() + 1;
            contents.set(5, 0, null);
            contents.set(5, 1, null);
            contents.set(5, 3, null);
            contents.set(5, 5, null);
            contents.set(5, 7, null);
            contents.set(5, 8, null);
            contents.set(5, 2, ClickableItem.of(new ItemBuilder(Material.ARROW)
                            .setDisplayName(game.translate("werewolf.menu.roles.previous",
                                    page, pagination.isFirst() ? page : page - 1))
                            .build(),

                    e -> INVENTORY.open(player, pagination.previous().getPage())));
            contents.set(5, 6, ClickableItem.of(new ItemBuilder(Material.ARROW)
                            .setDisplayName(game.translate("werewolf.menu.roles.next",
                                    page, pagination.isLast() ? page : page + 1))
                            .build(),

                    e -> INVENTORY.open(player, pagination.next().getPage())));
            contents.set(5, 4, ClickableItem.empty(
                    new ItemBuilder(UniversalMaterial.SIGN.getType())
                            .setDisplayName(game.translate("werewolf.menu.roles.current",
                                    page, items.size() / 36 + 1)).build()));
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

    public String getConversion(WereWolfAPI game, String key) {
        return game.getScore()
                .conversion(game
                        .getConfig()
                        .getTimerValue(key));
    }
}


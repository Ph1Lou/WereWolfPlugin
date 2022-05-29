package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TimersGUI implements InventoryProvider {

    public TimersGUI(Player player) {
    }

    public static SmartInventory getInventory(Player player) {
        Main main = JavaPlugin.getPlugin(Main.class);

        return SmartInventory.builder()
                .id("timers")
                .manager(main.getInvManager())
                .provider(new TimersGUI(player))
                .size(InventoryUtils.getRowNumbers(main.getRegisterManager().getTimersRegister().size(), false), 9)
                .title(main.getWereWolfAPI().translate("werewolf.menus.timers.name"))
                .closeable(true)
                .build();
    }


    private String key = TimerBase.INVULNERABILITY;

    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        contents.set(0, 0, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(
                                game.translate("werewolf.menus.return"))
                        .build()), e -> MainGUI.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = (GameManager) main.getWereWolfAPI();
        IConfiguration config = game.getConfig();
        Pagination pagination = contents.pagination();
        List<ClickableItem> items = new ArrayList<>();

        String c = getConversion(game, key);

        contents.set(0, 1, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)

                        .setDisplayName(game.translate("werewolf.utils.display",
                                Formatter.format("&field&","-10m"),
                                        Formatter.format("&value&",c)))
                        .build()), e -> {

            config.moveTimer(key, -600);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.utils.display",
                                    Formatter.format("&field&","-10m"),
                                    Formatter.format("&value&",getConversion(game, key))))
                    .build());

        }));
        contents.set(0, 2, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)
                        .setDisplayName(game.translate("werewolf.utils.display",
                                        Formatter.format("&field&","-1m"),
                                        Formatter.format("&value&",c)))
                        .build()), e -> {

            config.moveTimer(key, -60);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.utils.display",
                                    Formatter.format("&field&","-1m"),
                                    Formatter.format("&value&",getConversion(game, key))))
                    .build());

        }));
        contents.set(0, 3, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)
                        .setDisplayName(game.translate("werewolf.utils.display",
                                        Formatter.format("&field&","-10s"),
                                        Formatter.format("&value&",c)))
                        .build()), e -> {

            config.moveTimer(key, -10);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.utils.display",
                                    Formatter.format("&field&","-10s"),
                                    Formatter.format("&value&",getConversion(game, key))))
                    .build());

        }));
        contents.set(0, 5, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)
                        .setDisplayName(
                                game.translate("werewolf.utils.display",
                                                Formatter.format("&field&","+10s"),
                                                Formatter.format("&value&",c)))
                        .build()), e -> {

            config.moveTimer(key, 10);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(
                            game.translate("werewolf.utils.display",
                                            Formatter.format("&field&","+10s"),
                                            Formatter.format("&value&",getConversion(game, key))))
                    .build());

        }));
        contents.set(0, 6, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)
                        .setDisplayName(game.translate("werewolf.utils.display",
                                        Formatter.format("&field&","+1m"),
                                        Formatter.format("&value&",c)))
                        .build()), e -> {

            config.moveTimer(key, 60);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.utils.display",
                                    Formatter.format("&field&","+1m"),
                                    Formatter.format("&value&",getConversion(game, key))))
                    .build());

        }));
        contents.set(0, 7, ClickableItem.of((
                new ItemBuilder(Material.STONE_BUTTON)
                        .setDisplayName(game.translate("werewolf.utils.display",
                                        Formatter.format("&field&","+10m"),
                                        Formatter.format("&value&",c)))

                        .build()), e -> {
            config.moveTimer(key, 600);

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setDisplayName(game.translate("werewolf.utils.display",
                                    Formatter.format("&field&","+10m"),
                                    Formatter.format("&value&",getConversion(game, key))))
                    .build());
        }));


        main.getRegisterManager().getTimersRegister()
                .stream()
                .sorted(Comparator.comparingInt(o -> game.getConfig().getTimerValue(o.getMetaDatas().key())))
                .sorted((o1, o2) -> {
                    if(o1.getMetaDatas().decrement() && o2.getMetaDatas().decrement()){
                        return 0;
                    }
                    if(o1.getMetaDatas().decrement()){
                        return -1;
                    }
                    if(o2.getMetaDatas().decrement()){
                        return 1;
                    }
                    if(o1.getMetaDatas().decrementAfterRole() && o2.getMetaDatas().decrementAfterRole()){
                        return 0;
                    }
                    if(o1.getMetaDatas().decrementAfterRole()){
                        return -1;
                    }
                    if(o2.getMetaDatas().decrementAfterRole()){
                        return 1;
                    }
                    return 0;
                })
                .forEach(timerRegister -> {

            List<String> lore = new ArrayList<>();
                    Arrays.stream(timerRegister.getMetaDatas().loreKey())
                    .map(game::translate)
                    .map(s -> Arrays.stream(s.split("\\n"))
                            .collect(Collectors.toList()))
                    .forEach(lore::addAll);

            if (game.getConfig().getTimerValue(timerRegister.getMetaDatas().key()) >= 0 || game.isDebug()) {

                items.add(ClickableItem.of((new ItemBuilder(timerRegister.getMetaDatas().key().equals(key) ?
                                Material.FEATHER :
                                UniversalMaterial.ANVIL.getType())
                                .setLore(lore)
                                .setDisplayName(game.translate(timerRegister.getMetaDatas().key(),
                                        Formatter.timer(game, timerRegister.getMetaDatas().key())))
                                .build()),
                        e -> this.key = timerRegister.getMetaDatas().key()));
            }
        });

        InventoryUtils.fillInventory(game, items, pagination, contents, () -> getInventory(player), 45);

    }

    public String getConversion(WereWolfAPI game, String key) {
        return Utils.conversion(game
                .getConfig()
                .getTimerValue(key));
    }
}


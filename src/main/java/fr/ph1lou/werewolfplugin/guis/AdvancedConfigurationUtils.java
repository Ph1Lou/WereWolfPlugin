package fr.ph1lou.werewolfplugin.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdvancedConfigurationUtils {

    public static List<? extends ClickableItem> getIntConfigs(WereWolfAPI game, IntValue[] values) {

        return Arrays.stream(values)
                .map(intValue -> {
                    IConfiguration config = game.getConfig();
                    List<String> lore = new ArrayList<>(Arrays.asList(game.translate("werewolf.menus.lore.left"),
                            game.translate("werewolf.menus.lore.right")));

                    return ClickableItem.of(new ItemBuilder(intValue.item().getStack())
                            .setLore(lore)
                            .setDisplayName(game.translate(intValue.key(),
                                    Formatter.number(config.getValue(intValue.key()))))
                            .build(), e -> {

                        if (e.isLeftClick()) {
                            config.setValue(intValue.key(),
                                    config.getValue(intValue.key()) + intValue.step());
                        } else if (config.getValue(intValue.key()) - intValue.step() >= 0) {
                            config.setValue(intValue.key(),
                                    config.getValue(intValue.key()) - intValue.step());
                        }

                        e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                .setLore(lore)
                                .setDisplayName(game.translate(intValue.key(),
                                        Formatter.number(config.getValue(intValue.key()))))
                                .build());
                    });
                }).collect(Collectors.toList());
    }

    public static List<? extends ClickableItem> getTimers(WereWolfAPI game, Timer[] timers) {

        return Arrays.stream(timers)
                .map(timerRegister -> {
                    IConfiguration config = game.getConfig();
                    List<String> lore = new ArrayList<>(Arrays.asList(game.translate("werewolf.menus.lore.left"),
                            game.translate("werewolf.menus.lore.right")));
                    Arrays.stream(timerRegister.loreKey())
                            .map(game::translate)
                            .map(s -> Arrays.stream(s.split("\\n"))
                                    .collect(Collectors.toList()))
                            .forEach(lore::addAll);
                    return ClickableItem.of(new ItemBuilder(UniversalMaterial.CLOCK.getStack())
                            .setLore(lore)
                            .setDisplayName(game.translate(timerRegister.key(),
                                    Formatter.timer(game, timerRegister.key())))
                            .build(), e -> {


                        if (e.isLeftClick()) {
                            config.moveTimer(timerRegister.key(), timerRegister.step());
                        } else if (config.getTimerValue(timerRegister.key()) - timerRegister.step() >= 0) {
                            config.moveTimer(timerRegister.key(), -timerRegister.step());
                        }

                        e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                .setLore(lore)
                                .setDisplayName(game.translate(timerRegister.key(),
                                        Formatter.timer(game, timerRegister.key())))
                                .build());
                    });
                }).collect(Collectors.toList());
    }

    public static List<? extends ClickableItem> getConfigs(WereWolfAPI game, Configuration[] configurations, Supplier<SmartInventory> inventory) {
        return Arrays.stream(configurations)
                .map(configRegister -> ConfigurationsGUI.getClickableItem(game, configRegister,  inventory))
                .collect(Collectors.toList());
    }

    public static List<? extends String> getLoreFormat(WereWolfAPI game,
                                                       String[] loreKeys,
                                                       Configuration[] configurations,
                                                       Timer[] timers,
                                                       IntValue[] values) {
        return Arrays.stream(loreKeys)
                .map(s -> game.translate(s,
                        Stream.concat(Arrays.stream(configurations)
                                                .map(intValue -> {
                                                    String[] intValueKey = intValue.config().key().split("\\.");
                                                    return Formatter.format(String.format("&%s&", intValueKey[intValueKey.length - 1]),
                                                            game.translate(game.getConfig().isConfigActive(intValue.config().key()) ?
                                                                    "werewolf.utils.on" :
                                                                    "werewolf.utils.off"));
                                                }),
                                        Stream.concat(Arrays.stream(timers)
                                                        .map(timer -> {
                                                            String[] timerKey = timer.key().split("\\.");
                                                            return Formatter.format(String.format("&%s&", timerKey[timerKey.length - 1]),
                                                                    Utils.conversion(game.getConfig().getTimerValue(timer.key())));
                                                        }),
                                                Arrays.stream(values)
                                                        .map(value -> {
                                                            String[] timerKey = value.key().split("\\.");
                                                            return Formatter.format(String.format("&%s&", timerKey[timerKey.length - 1]),
                                                                    game.getConfig().getValue(value.key()));
                                                        }))

                                )
                                .toArray(Formatter[]::new)))
                .flatMap(s -> Arrays.stream(s.split("\\n")))
                .collect(Collectors.toList());
    }

    public static List<? extends String> getLore(WereWolfAPI game,
                                                 String[] loreKeys,
                                                 Configuration[] configurations,
                                                 Timer[] timers,
                                                 IntValue[] values,
                                                 ConfigurationBasic[] configurationBasics) {
        return Stream.concat(Arrays.stream(loreKeys).map(game::translate),
                        Stream.concat(Arrays.stream(configurations)
                                        .map(configuration -> String.format("%s : %s", game.translate(configuration.config().key()),
                                                game.translate(game.getConfig().isConfigActive(configuration.config().key()) ?
                                                        "werewolf.utils.on" :
                                                        "werewolf.utils.off"))),
                                Stream.concat(Arrays.stream(timers)
                                                .map(timer -> game.translate(timer.key(),
                                                        Formatter
                                                                .timer(Utils
                                                                        .conversion(game.getConfig()
                                                                                .getTimerValue(timer.key()))))),
                                        Stream.concat(Arrays.stream(values).map(intValue -> game.translate(intValue.key(),
                                                        Formatter.number(game.getConfig().getValue(intValue.key())))),
                                                Arrays.stream(configurationBasics).map(configuration -> String.format("%s : %s", game.translate(configuration.key()),
                                                        game.translate(game.getConfig().isConfigActive(configuration.key()) ?
                                                                "werewolf.utils.on" :
                                                                "werewolf.utils.off")))))))
                .flatMap(s -> Arrays.stream(s.split("\\n")))
                .collect(Collectors.toList());
    }

    public static List<? extends ClickableItem> getConfigBasic(WereWolfAPI game, ConfigurationBasic[] configurations) {

        return Arrays.stream(configurations)
                .map(configuration -> {
                    IConfiguration config = game.getConfig();
                    List<String> lore = new ArrayList<>();
                    String key = configuration.key();
                    ItemStack itemStack;

                    if (game.getConfig().isConfigActive(key)) {
                        lore.add(0, game.translate("werewolf.utils.enable"));
                        itemStack = UniversalMaterial.GREEN_TERRACOTTA.getStack();
                    } else {
                        lore.add(0, game.translate("werewolf.utils.disable"));
                        itemStack = UniversalMaterial.RED_TERRACOTTA.getStack();
                    }

                    Optional<String> incompatible = Arrays.stream(configuration
                                    .incompatibleConfigs())
                            .filter(s -> game.getConfig().isConfigActive(s))
                            .map(game::translate).findFirst();

                    incompatible
                            .ifPresent(configuration1 -> lore.add(game.translate("werewolf.menus.configurations.incompatible",
                                    Formatter.format("&configuration&", configuration1))));

                    return ClickableItem.of(new ItemBuilder(itemStack)
                            .setDisplayName(game.translate(key))
                            .setLore(lore)
                            .build(), e -> {

                        if (!incompatible.isPresent() || config.isConfigActive(key)) {
                            config.switchConfigValue(key);
                        }
                    });
                })
                .collect(Collectors.toList());
    }
}

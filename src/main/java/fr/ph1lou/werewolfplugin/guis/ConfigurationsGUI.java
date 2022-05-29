package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ConfigurationsGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("global")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new ConfigurationsGUI())
            .size(InventoryUtils
                            .getRowNumbers(JavaPlugin.getPlugin(Main.class)
                                    .getRegisterManager()
                                    .getConfigsRegister()
                                    .stream()
                                    .mapToInt(value -> 1)
                                    .sum(), false),
                    9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menus.configurations.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menus.return")).build()), e -> MainGUI.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = (GameManager) main.getWereWolfAPI();
        Pagination pagination = contents.pagination();
        List<ClickableItem> items = new ArrayList<>();

        main.getRegisterManager().getConfigsRegister()
                .stream()
                .sorted((o1, o2) -> game.translate(o1.getMetaDatas().key())
                        .compareToIgnoreCase(game.translate(o2.getMetaDatas().key())))
                .filter(config1 -> (config1.getMetaDatas().appearInMenu())
                        || game.isDebug())
                .forEach(configRegister -> items.add(getClickableItem(game, configRegister.getMetaDatas(),
                        (ListenerManager) configRegister
                                .getObject()
                                .filter(o -> o instanceof ListenerManager)
                                .orElse(null),
                        () -> AdvancedConfigurationsGUI.getInventory(configRegister.getMetaDatas(),
                                pagination.getPage()))));

        InventoryUtils.fillInventory(game, items, pagination, contents, () -> INVENTORY, 45);
    }

    public static ClickableItem getClickableItem(WereWolfAPI game, Configuration configuration,
                                                 @Nullable ListenerManager listener,
                                                 Supplier<SmartInventory> inventory) {
        IConfiguration config = game.getConfig();
        List<String> lore = new ArrayList<>();
        String key = configuration.key();
        ItemStack itemStack;

        if (game.getConfig().isConfigActive(key)) {
            lore.addAll(AdvancedConfigurationUtils.getLore(game,
                    configuration.loreKey(),
                    new Configuration[0],
                    configuration.timers(),
                    configuration.configValues()));
            if(!lore.isEmpty()){
                lore.add(game.translate("werewolf.menus.lore.shift"));
            }
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
                        Formatter.format("&configuration&",configuration1))));

        return ClickableItem.of(new ItemBuilder(itemStack)
                .setDisplayName(game.translate(key))
                .setLore(lore).build(), e -> {

            if(e.isShiftClick()){
                inventory.get().open((Player) e.getWhoClicked());
            }
            else if (!incompatible.isPresent() || config.isConfigActive(key)) {
                config.switchConfigValue(key);
                if(listener != null){
                    listener.register(config.isConfigActive(key));
                }
            }


        });
    }
}


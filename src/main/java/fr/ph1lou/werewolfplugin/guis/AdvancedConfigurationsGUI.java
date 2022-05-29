package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedConfigurationsGUI implements InventoryProvider {

    private final Configuration configuration;
    private final int page;

    public AdvancedConfigurationsGUI(Configuration configuration, int page) {
        this.configuration = configuration;
        this.page = page;
    }

    public static SmartInventory getInventory(Configuration configuration, int page) {

        GetWereWolfAPI api = JavaPlugin.getPlugin(Main.class);

        WereWolfAPI game = api.getWereWolfAPI();
        return SmartInventory.builder()
                .id("advanced" + configuration.key())
                .manager(api.getInvManager())
                .provider(new AdvancedConfigurationsGUI(configuration, page))
                .size(InventoryUtils.getRowNumbers((configuration.timers().length +
                                configuration.configValues().length)*2, true),
                        9)
                .title(game.translate("werewolf.menus.advanced_tool_role.menu",
                                Formatter.role(game.translate(configuration.key()))))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of(new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menus.return")).build(),
                e -> ConfigurationsGUI.INVENTORY.open(player, this.page)));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        AtomicInteger i = new AtomicInteger(2);

        AdvancedConfigurationUtils.getIntConfigs(game, this.configuration.configValues()).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });

        AdvancedConfigurationUtils.getTimers(game, this.configuration.timers()).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });
    }






}


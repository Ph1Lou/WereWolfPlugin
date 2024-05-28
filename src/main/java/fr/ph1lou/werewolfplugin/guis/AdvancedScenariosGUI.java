package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedScenariosGUI implements InventoryProvider {

    private final Scenario register;
    private final int page;

    public AdvancedScenariosGUI(Scenario register, int page) {
        this.register = register;
        this.page = page;
    }

    public static SmartInventory getInventory(Scenario register, int page) {

        Main api = JavaPlugin.getPlugin(Main.class);

        WereWolfAPI game = api.getWereWolfAPI();
        return SmartInventory.builder()
                .id("advanced" + register.key())
                .manager(api.getInvManager())
                .provider(new AdvancedScenariosGUI(register, page))
                .size(InventoryUtils.getRowNumbers((register.configurations().length
                        + register.timers().length +
                        register.configValues().length) * 2, true), 9)
                .title(game.translate("werewolf.menus.advanced_tool_role.menu",
                        Formatter.role(game.translate(register.key()))))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of(new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menus.return")).build(),
                e -> ScenariosGUI.INVENTORY.open(player, page)));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        AtomicInteger i = new AtomicInteger(2);

        AdvancedConfigurationUtils.getIntConfigs(game, this.register.configValues()).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });

        AdvancedConfigurationUtils.getTimers(game, this.register.timers()).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });
    }

}


package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedEventMenu implements InventoryProvider {

    private final Event event;

    public AdvancedEventMenu(Event event) {
        this.event = event;
    }

    public static SmartInventory getInventory(Event register) {

        GetWereWolfAPI api = JavaPlugin.getPlugin(Main.class);

        WereWolfAPI game = api.getWereWolfAPI();
        return SmartInventory.builder()
                .id("advanced" + register.key())
                .manager(api.getInvManager())
                .provider(new AdvancedEventMenu(register))
                .size(Math.min(54, (Math.max(0, ((register.configurations().length
                +register.timers().length +
                        register.configValues().length)
                        * 2 - 6)) / 9 + 1) * 9) / 9, 9)
                .title(game.translate("werewolf.menu.advanced_tool_role.menu",
                                Formatter.role(game.translate(register.key()))))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of(new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menu.return")).build(),
                e -> RandomEvents.INVENTORY.open(player)));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        AtomicInteger i = new AtomicInteger(2);

        AdvancedConfigurationUtils.getIntConfigs(game, this.event.configValues()).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });

        AdvancedConfigurationUtils.getTimers(game, this.event.timers()).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });

        AdvancedConfigurationUtils.getConfigs(game, this.event.configurations()).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });
    }

}


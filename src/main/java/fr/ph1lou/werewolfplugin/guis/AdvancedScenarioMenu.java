package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AdvancedScenarioMenu implements InventoryProvider {

    private final Wrapper<ListenerManager, Scenario> register;

    public AdvancedScenarioMenu(Wrapper<ListenerManager, Scenario> register) {
        this.register = register;
    }

    public static SmartInventory getInventory(Wrapper<ListenerManager, Scenario> register) {

        GetWereWolfAPI api = JavaPlugin.getPlugin(Main.class);

        WereWolfAPI game = api.getWereWolfAPI();
        return SmartInventory.builder()
                .id("advanced" + register.getMetaDatas().key())
                .manager(api.getInvManager())
                .provider(new AdvancedScenarioMenu(register))
                .size(Math.min(54, (Math.max(0, register.getMetaDatas().configValues().length
                        * 2 - 6) / 9 + 1) * 9) / 9, 9)
                .title(game.translate("werewolf.menu.advanced_tool_role.menu",
                                Formatter.role(game.translate(register.getMetaDatas().key()))))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of(new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menu.return")).build(),
                e -> ScenariosGUI.INVENTORY.open(player)));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);

        AtomicInteger i = new AtomicInteger(2);

        this.getIntConfigEvent(main, this.register).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });
    }

    public List<ClickableItem> getIntConfigEvent(GetWereWolfAPI main, Wrapper<ListenerManager, Scenario> roleRegister){

        WereWolfAPI game = main.getWereWolfAPI();
        return Arrays.stream(roleRegister.getMetaDatas().configValues())
                .map(intValue -> {
                    IConfiguration config = game.getConfig();
                    List<String> lore = new ArrayList<>(Arrays.asList(game.translate("werewolf.menu.left"),
                            game.translate("werewolf.menu.right")));

                    return ClickableItem.of(new ItemBuilder(intValue.item().getStack())
                            .setLore(lore)
                            .setDisplayName(game.translate(intValue.key(),
                                    Formatter.number(config.getValue(intValue.key()))))
                            .build(),e -> {

                        if (e.isLeftClick()) {
                            config.setValue(intValue.key(),
                                    config.getValue(intValue.key()) + intValue.step());
                        } else if (config.getValue(intValue.key()) - intValue.step() > 0) {
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




}


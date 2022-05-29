package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ScenariosGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("scenarios")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new ScenariosGUI())
            .size(InventoryUtils.getRowNumbers(JavaPlugin.getPlugin(Main.class)
                    .getRegisterManager()
                    .getScenariosRegister()
                            .size(), false), 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menus.scenarios.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType())
                .setDisplayName(game.translate("werewolf.menus.return")).build()), e -> MainGUI.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = (GameManager) main.getWereWolfAPI();
        IConfiguration config = game.getConfig();
        Pagination pagination = contents.pagination();
        List<ClickableItem> items = new ArrayList<>();

        main.getRegisterManager()
                .getScenariosRegister()
                .stream()
                .sorted((o1, o2) -> game.translate(o1.getMetaDatas().key())
                        .compareToIgnoreCase(game.translate(o2.getMetaDatas().key())))
                .forEach(scenarioRegister -> {
                    List<String> lore = new ArrayList<>();
                    ItemStack itemStack;

                    if (config.isScenarioActive(scenarioRegister.getMetaDatas().key())) {
                        lore.addAll(AdvancedConfigurationUtils.getLore(game,
                                scenarioRegister.getMetaDatas().loreKey(),
                                scenarioRegister.getMetaDatas().configurations(),
                                scenarioRegister.getMetaDatas().timers(),
                                scenarioRegister.getMetaDatas().configValues()));
                        if(!lore.isEmpty()){
                            lore.add(game.translate("werewolf.menus.lore.shift"));
                        }
                        lore.add(0, game.translate("werewolf.utils.enable"));
                        itemStack = UniversalMaterial.GREEN_TERRACOTTA.getStack();
                    } else {
                        lore.add(0, game.translate("werewolf.utils.disable"));
                        itemStack = UniversalMaterial.RED_TERRACOTTA.getStack();
                    }

                    Optional<String> incompatible = Arrays.stream(scenarioRegister
                                    .getMetaDatas().incompatibleScenarios())
                            .filter(s -> game.getConfig().isScenarioActive(s))
                            .map(game::translate)
                            .findFirst();

                    incompatible
                            .ifPresent(scenario -> lore.add(game.translate("werewolf.menus.scenarios.incompatible",
                                    Formatter.format("&scenario&",scenario))));


                    items.add(ClickableItem.of((new ItemBuilder(itemStack)
                            .setDisplayName(game.translate(scenarioRegister.getMetaDatas().key()))
                            .setLore(lore).build()), e -> {


                        if(e.isShiftClick()){
                            AdvancedScenariosGUI.getInventory(scenarioRegister.getMetaDatas(),
                                    pagination.getPage()).open(player);
                        }
                        else if (!incompatible.isPresent() || config.isScenarioActive(scenarioRegister.getMetaDatas().key())) {
                            config.switchScenarioValue(scenarioRegister.getMetaDatas().key());
                            scenarioRegister.getObject().ifPresent(listenerManager -> listenerManager
                                    .register(config.isScenarioActive(scenarioRegister.getMetaDatas().key())));
                        }
                    }));
                });

        InventoryUtils.fillInventory(game, items, pagination, contents, () -> INVENTORY, 45);
    }
}


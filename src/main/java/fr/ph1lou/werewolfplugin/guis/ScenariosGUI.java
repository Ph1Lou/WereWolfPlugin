package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Material;
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
            .size(Math.min(54, (JavaPlugin.getPlugin(Main.class)
                    .getRegisterManager()
                    .getScenariosRegister().size() / 9 + 2) * 9) / 9, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menu.scenarios.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType())
                .setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));
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
                            lore.add(game.translate("werewolf.menu.shift"));
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
                            .ifPresent(scenario -> lore.add(game.translate("werewolf.menu.scenarios.incompatible",
                                    Formatter.format("&scenario&",scenario))));


                    items.add(ClickableItem.of((new ItemBuilder(itemStack)
                            .setDisplayName(game.translate(scenarioRegister.getMetaDatas().key()))
                            .setLore(lore).build()), e -> {


                        if(e.isShiftClick()){
                            AdvancedScenarioMenu.getInventory(scenarioRegister.getMetaDatas()).open(player);
                        }
                        else if (!incompatible.isPresent() || config.isScenarioActive(scenarioRegister.getMetaDatas().key())) {
                            config.switchScenarioValue(scenarioRegister.getMetaDatas().key());
                            scenarioRegister.getObject().ifPresent(listenerManager -> listenerManager
                                    .register(config.isScenarioActive(scenarioRegister.getMetaDatas().key())));
                        }
                    }));
                });

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

                    e -> INVENTORY.open(player, pagination
                            .previous().getPage())));
            contents.set(5, 6, ClickableItem.of(
                    new ItemBuilder(Material.ARROW)
                            .setDisplayName(
                                    game.translate("werewolf.menu.roles.next",
                                            Formatter.format("&current&",page),
                                            Formatter.format("&next&",pagination.isLast() ?
                                                    page : page + 1))).build(),
                    e -> INVENTORY.open(player, pagination
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
}


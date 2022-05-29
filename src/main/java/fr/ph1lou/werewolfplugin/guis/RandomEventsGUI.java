package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomEventsGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("random")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new RandomEventsGUI())
            .size(InventoryUtils
                    .getRowNumbers(JavaPlugin.getPlugin(Main.class)
                            .getRegisterManager()
                            .getRandomEventsRegister()
                            .size(), false), 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menus.random_events.name"))
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
        IConfiguration config = game.getConfig();
        Pagination pagination = contents.pagination();
        List<ClickableItem> items = new ArrayList<>();

        main.getRegisterManager().getRandomEventsRegister()
                .stream()
                .sorted((o1, o2) -> game.translate(o1.getMetaDatas().key())
                        .compareToIgnoreCase(game.translate(o2.getMetaDatas().key())))
                .forEach(randomEventRegister -> {
                    String key = randomEventRegister.getMetaDatas().key();
                    ItemStack itemStack = getItemStack(game, randomEventRegister);

                    items.add(ClickableItem.of((itemStack), e -> {

                        if (e.isShiftClick()) {
                            AdvancedEventsGUI.getInventory(randomEventRegister.getMetaDatas(), pagination.getPage()).open(player);
                        }
                        else if (e.isLeftClick()) {
                            int probability = config.getProbability(key);
                            config.setProbability(key, (probability + 1) % 101);
                            if (probability == 0) {
                                if (!game.isState(StateGame.LOBBY)) {
                                    randomEventRegister.getObject().ifPresent(listenerManager -> listenerManager.register(game.getRandom().nextDouble() * 100 < game.getConfig().getProbability(key)));
                                }
                            }
                            e.setCurrentItem(getItemStack(game, randomEventRegister));
                        }
                        else if (e.isRightClick()) {
                            int probability = config.getProbability(key);
                            config.setProbability(key, ((probability - 1) + 101) % 101);
                            if (probability == 1) {
                                randomEventRegister.getObject().ifPresent(listenerManager -> listenerManager.register(false));

                            }
                            e.setCurrentItem(getItemStack(game, randomEventRegister));
                        }
                    }));


                });

        InventoryUtils.fillInventory(game, items, pagination, contents, () -> INVENTORY, 45);
    }

    private ItemStack getItemStack(GameManager game, Wrapper<ListenerManager, Event> randomEventRegister) {

        String key = randomEventRegister.getMetaDatas().key();
        IConfiguration config = game.getConfig();
        List<String> lore2 = new ArrayList<>(Arrays.asList(game.translate("werewolf.menus.lore.left"),
                game.translate("werewolf.menus.lore.right"),
                game.translate("werewolf.menus.lore.shift")));
        List<String> lore = new ArrayList<>();
        ItemStack itemStack;

        if (config.getProbability(key) > 0) {
            lore.addAll(AdvancedConfigurationUtils.getLoreFormat(game, randomEventRegister.getMetaDatas().loreKey(),
                    randomEventRegister.getMetaDatas().configurations(),
                    randomEventRegister.getMetaDatas().timers(),
                    randomEventRegister.getMetaDatas().configValues()));
            itemStack = UniversalMaterial.GREEN_TERRACOTTA.getStack();
        } else {
            lore.add(0, game.translate("werewolf.utils.disable"));
            itemStack = UniversalMaterial.RED_TERRACOTTA.getStack();
        }
        lore.add(game.translate("werewolf.menus.random_events.probability",
                Formatter.number(config.getProbability(key))));
        lore.addAll(lore2);

        return new ItemBuilder(itemStack).setDisplayName(game.translate(key)).setLore(lore).build();
    }
}


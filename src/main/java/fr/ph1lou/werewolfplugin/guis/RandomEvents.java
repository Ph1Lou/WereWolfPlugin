package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RandomEvents implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("random")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new RandomEvents())
            .size(Math.min(54, (JavaPlugin.getPlugin(Main.class).getRegisterManager().getRandomEventsRegister().size() / 9 + 2) * 9) / 9, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menu.random_events.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = (GameManager) main.getWereWolfAPI();
        IConfiguration config = game.getConfig();
        Pagination pagination = contents.pagination();
        List<ClickableItem> items = new ArrayList<>();

        main.getRegisterManager().getRandomEventsRegister()
                .forEach(randomEventRegister -> {
                    String key = randomEventRegister.getMetaDatas().key();
                    ItemStack itemStack = getItemStack(game, randomEventRegister);

                    items.add(ClickableItem.of((itemStack), e -> {

                        if (e.isLeftClick()) {
                            int probability = config.getProbability(key);
                            config.setProbability(key, (probability + 1) % 101);
                            if (probability == 0) {
                                if (!game.isState(StateGame.LOBBY)) {
                                    randomEventRegister.getObject().ifPresent(listenerManager -> listenerManager.register(game.getRandom().nextDouble() * 100 < game.getConfig().getProbability(key)));
                                }
                            }
                            e.setCurrentItem(getItemStack(game, randomEventRegister));
                        }
                        if (e.isRightClick()) {
                            int probability = config.getProbability(key);
                            config.setProbability(key, ((probability - 1) + 101) % 101);
                            if (probability == 1) {
                                randomEventRegister.getObject().ifPresent(listenerManager -> listenerManager.register(false));

                            }
                            e.setCurrentItem(getItemStack(game, randomEventRegister));
                        }
                    }));

                    if (items.size() > 45) {
                        pagination.setItems(items.toArray(new ClickableItem[0]));
                        pagination.setItemsPerPage(36);
                        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));
                        int page = pagination.getPage() + 1;
                        contents.set(5, 0, null);
                        contents.set(5, 1, null);
                        contents.set(5, 3, null);
                        contents.set(5, 5, null);
                        contents.set(5, 7, null);
                        contents.set(5, 8, null);
                        contents.set(5, 2, ClickableItem.of(new ItemBuilder(Material.ARROW)
                                        .setDisplayName(game.translate("werewolf.menu.roles.previous",
                                                        Formatter.format("&current&",page),
                                                        Formatter.format("&previous&",pagination.isFirst() ? page : page - 1))).build(),
                                e -> INVENTORY.open(player, pagination.previous().getPage())));
                        contents.set(5, 6, ClickableItem.of(new ItemBuilder(Material.ARROW)
                                        .setDisplayName(game.translate("werewolf.menu.roles.next",
                                                                Formatter.format("&current&",page),
                                                                Formatter.format("&next&",pagination.isLast() ? page : page + 1)))
                                        .build(),
                                e -> INVENTORY.open(player, pagination.next().getPage())));
                        contents.set(5, 4, ClickableItem.empty(new ItemBuilder(UniversalMaterial.SIGN.getType())
                                .setDisplayName(game.translate("werewolf.menu.roles.current",
                                                Formatter.format("&current&",page),
                                                Formatter.format("&sum&", items.size() / 36 + 1)))
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
                });
    }

    private ItemStack getItemStack(GameManager game, Wrapper<ListenerManager, Event> randomEventRegister) {

        String key = randomEventRegister.getMetaDatas().key();
        IConfiguration config = game.getConfig();
        List<String> lore2 = new ArrayList<>(Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right")));
        List<String> lore = new ArrayList<>();
        Arrays.stream(randomEventRegister.getMetaDatas().loreKey()).map(game::translate)
                .map(s -> Arrays.stream(s.split("\\n"))
                .collect(Collectors.toList())).forEach(lore::addAll);
        ItemStack itemStack;

        if (config.getProbability(key) > 0) {
            lore.add(0, game.translate("werewolf.utils.enable"));
            itemStack = UniversalMaterial.GREEN_TERRACOTTA.getStack();
        } else {
            lore.add(0, game.translate("werewolf.utils.disable"));
            itemStack = UniversalMaterial.RED_TERRACOTTA.getStack();
        }
        lore.add(game.translate("werewolf.menu.random_events.probability",
                Formatter.number(config.getProbability(key))));
        lore.addAll(lore2);

        return new ItemBuilder(itemStack).setDisplayName(game.translate(key)).setLore(lore).build();
    }
}


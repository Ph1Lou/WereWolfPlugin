package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
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
        ConfigWereWolfAPI config = game.getConfig();
        Pagination pagination = contents.pagination();
        List<ClickableItem> items = new ArrayList<>();

        main.getRegisterManager().getRandomEventsRegister()
                .forEach(randomEventRegister -> {
                    String key = randomEventRegister.getKey();
                    ItemStack itemStack = getItemStack(game, key, randomEventRegister.getLoreKey());

                    items.add(ClickableItem.of((itemStack), e -> {

                        if (e.isLeftClick()) {
                            int probability = config.getProbability(key);
                            config.setProbability(key, probability + 1);
                            if (probability == 0) {
                                randomEventRegister.getRandomEvent().register(true);
                            }
                            e.setCurrentItem(getItemStack(game, key, randomEventRegister.getLoreKey()));
                        }
                        if (e.isRightClick()) {
                            int probability = config.getProbability(key);
                            if (probability > 0) {
                                config.setProbability(key, probability - 1);
                                if (probability == 1) {
                                    randomEventRegister.getRandomEvent().register(false);
                                }
                                e.setCurrentItem(getItemStack(game, key, randomEventRegister.getLoreKey()));
                            }
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
                        contents.set(5, 2, ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName(game.translate("werewolf.menu.roles.previous", page, pagination.isFirst() ? page : page - 1)).build(),
                                e -> INVENTORY.open(player, pagination.previous().getPage())));
                        contents.set(5, 6, ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName(game.translate("werewolf.menu.roles.next", page, pagination.isLast() ? page : page + 1)).build(),
                                e -> INVENTORY.open(player, pagination.next().getPage())));
                        contents.set(5, 4, ClickableItem.empty(new ItemBuilder(UniversalMaterial.SIGN.getType()).setDisplayName(game.translate("werewolf.menu.roles.current", page, items.size() / 36 + 1)).build()));
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

    private ItemStack getItemStack(GameManager game, String key, List<String> loreKey) {

        ConfigWereWolfAPI config = game.getConfig();
        List<String> lore2 = new ArrayList<>(Arrays.asList(game.translate("werewolf.menu.left"), game.translate("werewolf.menu.right")));
        List<String> lore = loreKey.stream().map(game::translate).collect(Collectors.toList());
        ItemStack itemStack;

        if (config.getProbability(key) > 0) {
            lore.add(0, game.translate("werewolf.utils.enable", ""));
            itemStack = UniversalMaterial.GREEN_TERRACOTTA.getStack();
        } else {
            lore.add(0, game.translate("werewolf.utils.disable", ""));
            itemStack = UniversalMaterial.RED_TERRACOTTA.getStack();
        }
        lore.add(game.translate("werewolf.menu.random_events.probability", config.getProbability(key)));
        lore.addAll(lore2);

        return new ItemBuilder(itemStack).setDisplayName(game.translate(key)).setLore(lore).build();
    }
}


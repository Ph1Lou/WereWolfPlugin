package fr.ph1lou.werewolfplugin.utils;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

public class InventoryUtils {


    public static int getRowNumbers(int elements, boolean onFirstLine) {
        if (onFirstLine) {
            elements -= 8;
        }
        if (elements <= 0) {
            return 1;
        }
        return 1 + Math.min(5, (elements - 1) / 9 + 1);
    }

    public static void fillInventory(WereWolfAPI game, List<ClickableItem> items, Pagination pagination, InventoryContents contents, Supplier<SmartInventory> currentInventory, int maxSize) {
        if (items.size() > maxSize) {
            pagination.setItems(items.toArray(new ClickableItem[0]));
            pagination.setItemsPerPage(maxSize - 9);
            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));
            int page = pagination.getPage() + 1;
            contents.set(4, 0, null);
            contents.set(4, 1, null);
            contents.set(4, 3, null);
            contents.set(4, 5, null);
            contents.set(4, 7, null);
            contents.set(4, 8, null);
            contents.set(4, 2, ClickableItem.of(new ItemBuilder(Material.ARROW)
                            .setDisplayName(game.translate("werewolf.menus.pagination.previous",
                                    Formatter.format("&current&", page),
                                    Formatter.format("&previous&", pagination.isFirst() ? page : page - 1)))
                            .build(),
                    e -> currentInventory.get().open((Player) e.getWhoClicked(), pagination.previous().getPage())));
            contents.set(4, 6, ClickableItem.of(new ItemBuilder(Material.ARROW)
                            .setDisplayName(game.translate("werewolf.menus.pagination.next",
                                    Formatter.format("&current&", page),
                                    Formatter.format("&next&", pagination.isLast() ? page : page + 1)))
                            .build(),
                    e -> currentInventory.get().open((Player) e.getWhoClicked(), pagination.next().getPage())));
            contents.set(4, 4, ClickableItem.empty(new ItemBuilder(UniversalMaterial.SIGN.getType())
                    .setDisplayName(game.translate("werewolf.menus.pagination.current",
                            Formatter.format("&current&", page),
                            Formatter.format("&sum&", items.size() / (maxSize - 9) + 1)))
                    .build()));
        } else {
            int i = 0;
            for (ClickableItem clickableItem : items) {
                contents.set(i / 9 + 1, i % 9, clickableItem);
                i++;
            }
            for (int k = i; k < maxSize; k++) {
                contents.set(k / 9 + 1, k % 9, null);
            }
        }
    }
}

package fr.ph1lou.werewolfplugin.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ChoiceGui implements InventoryProvider {

    public ChoiceGui(Player player) {

    }

    public static SmartInventory getInventory(Player player) {
        return SmartInventory.builder()
                .id("color-player-choice")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new ChoiceGui(player))
                .size(5, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.commands.player.color.player_choice"))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = (GameManager) main.getWereWolfAPI();
        Pagination pagination = contents.pagination();


        List<ClickableItem> items = new ArrayList<>();


        game.getPlayerWW(player.getUniqueId())
                .ifPresent(playerWW -> game.getPlayersWW()
                        .stream()
                        .sorted((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()))
                        .filter(playerWW1 -> !playerWW1.isState(StatePlayer.DEATH))
                        .forEach(targetWW -> items.add(ClickableItem.of((
                                new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                                        .setHead(targetWW.getName(),
                                                Bukkit.getOfflinePlayer(targetWW.getReviewUUID()))
                                        .setDisplayName(playerWW.getColor(targetWW)+targetWW.getName())
                                        .build()), e -> ColorsGUI.getInventory(targetWW).open(player)))));

        InventoryUtils.fillInventory(game, items, pagination, contents, () -> getInventory(player), 36);
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

}


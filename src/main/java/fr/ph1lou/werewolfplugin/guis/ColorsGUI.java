package fr.ph1lou.werewolfplugin.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ColorsGUI implements InventoryProvider {

    private final IPlayerWW playerWW;

    private final static Map<UniversalMaterial, ChatColor> COLORS = new HashMap<UniversalMaterial, ChatColor>()
    {{
            put(UniversalMaterial.WHITE_WOOL, ChatColor.RESET);
            put(UniversalMaterial.GRAY_WOOL, ChatColor.DARK_GRAY);
            put(UniversalMaterial.ORANGE_WOOL, ChatColor.GOLD);
            put(UniversalMaterial.YELLOW_WOOL, ChatColor.YELLOW);
            put(UniversalMaterial.GREEN_WOOL, ChatColor.DARK_GREEN);
            put(UniversalMaterial.LIME_WOOL, ChatColor.GREEN);
            put(UniversalMaterial.CYAN_WOOL, ChatColor.DARK_AQUA);
            put(UniversalMaterial.BLUE_WOOL, ChatColor.DARK_BLUE);
            put(UniversalMaterial.LIGHT_BLUE_WOOL, ChatColor.BLUE);
            put(UniversalMaterial.PINK_WOOL, ChatColor.LIGHT_PURPLE);
            put(UniversalMaterial.PURPLE_WOOL, ChatColor.DARK_PURPLE);
            put(UniversalMaterial.LIGHT_GRAY_WOOL, ChatColor.GRAY);
            put(UniversalMaterial.RED_WOOL, ChatColor.DARK_RED);
            put(UniversalMaterial.BLACK_WOOL, ChatColor.BLACK);
    }};

    public ColorsGUI(IPlayerWW playerWW) {
        this.playerWW = playerWW;
    }

    public static SmartInventory getInventory(IPlayerWW playerWW) {
        return SmartInventory.builder()
                .id("color_choice")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new ColorsGUI(playerWW))
                .size(3, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI()
                        .translate("werewolf.commands.player.color.color_choice"))
                .closeable(true)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = (GameManager) JavaPlugin.getPlugin(Main.class).getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menus.return")).build()), e -> ChoiceGui.getInventory(player).open(player)));

        AtomicInteger i= new AtomicInteger();

        COLORS.forEach((universalMaterial, chatColor) -> {
            contents.set(i.get() / 9 + 1, i.get() % 9, ClickableItem.of((new ItemBuilder(universalMaterial.getStack()).build()),
                    e -> {
                        game.getPlayerWW(player.getUniqueId()).ifPresent(playerWW1 -> {
                            playerWW1.setColor(this.playerWW, chatColor);
                            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));
                        });
                        ChoiceGui.getInventory(player).open(player);
                    }));
            i.getAndIncrement();
        });
    }
}

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ColorsGUI implements InventoryProvider {

    private final List<IPlayerWW> iPlayerWWS;

    private static final Set<UUID> pastilleModePlayers = new HashSet<>();

    private final static Map<UniversalMaterial, ChatColor> COLORS = new HashMap<UniversalMaterial, ChatColor>() {{
        put(UniversalMaterial.WHITE_WOOL, ChatColor.RESET);
        put(UniversalMaterial.GRAY_WOOL, ChatColor.DARK_GRAY);
        put(UniversalMaterial.ORANGE_WOOL, ChatColor.GOLD);
        put(UniversalMaterial.YELLOW_WOOL, ChatColor.YELLOW);
        put(UniversalMaterial.GREEN_WOOL, ChatColor.DARK_GREEN);
        put(UniversalMaterial.LIME_WOOL, ChatColor.GREEN);
        put(UniversalMaterial.CYAN_WOOL, ChatColor.DARK_AQUA);
        put(UniversalMaterial.BLUE_WOOL, ChatColor.DARK_BLUE);
        put(UniversalMaterial.LIGHT_BLUE_WOOL, ChatColor.AQUA);
        put(UniversalMaterial.PINK_WOOL, ChatColor.LIGHT_PURPLE);
        put(UniversalMaterial.PURPLE_WOOL, ChatColor.DARK_PURPLE);
        put(UniversalMaterial.LIGHT_GRAY_WOOL, ChatColor.GRAY);
        put(UniversalMaterial.RED_WOOL, ChatColor.DARK_RED);
        put(UniversalMaterial.BLACK_WOOL, ChatColor.BLACK);
    }};

    public ColorsGUI(List<IPlayerWW> iPlayerWWS) {
        this.iPlayerWWS = iPlayerWWS;
    }

    public static SmartInventory getInventory(List<IPlayerWW> playerWW, Player player) {
        boolean mode = pastilleModePlayers.contains(player.getUniqueId());
        String titleKey = mode ? "werewolf.commands.player.color.pastille_choice" : "werewolf.commands.player.color.color_choice";
        return SmartInventory.builder()
                .id("color_choice")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new ColorsGUI(playerWW))
                .size(3, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI()
                        .translate(titleKey))
                .closeable(true)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = (GameManager) JavaPlugin.getPlugin(Main.class).getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menus.return")).build()), e -> ChoiceGui.getInventory(player).open(player)));

        boolean mode = pastilleModePlayers.contains(player.getUniqueId());

        contents.set(0, 8, ClickableItem.of((new ItemBuilder(UniversalMaterial.NAME_TAG.getStack())
                .setDisplayName(game.translate(mode ? "werewolf.commands.player.color.pastille_mode_on" : "werewolf.commands.player.color.pastille_mode_off"))
                .setLore(Arrays.asList(game.translate("werewolf.commands.player.color.pastille_mode_lore")))
                .build()), e -> {
            if (mode) {
                pastilleModePlayers.remove(player.getUniqueId());
            } else {
                pastilleModePlayers.add(player.getUniqueId());
            }
            ColorsGUI.getInventory(iPlayerWWS, player).open(player);
        }));

        if (mode) {
            contents.set(0, 7, ClickableItem.of((new ItemBuilder(UniversalMaterial.BARRIER.getStack())
                    .setDisplayName(game.translate("werewolf.commands.player.color.pastille_remove"))
                    .build()), e -> {
                game.getPlayerWW(player.getUniqueId()).ifPresent(playerWW1 -> {
                    iPlayerWWS.forEach(iPlayerWW -> {
                        playerWW1.removePastille(iPlayerWW);
                        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));
                    });
                });
                ChoiceGui.getInventory(player).open(player);
            }));
        }

        AtomicInteger i = new AtomicInteger();

        COLORS.forEach((universalMaterial, chatColor) -> {
            contents.set(i.get() / 9 + 1, i.get() % 9, ClickableItem.of((new ItemBuilder(universalMaterial.getStack()).build()),
                    e -> {
                        game.getPlayerWW(player.getUniqueId()).ifPresent(playerWW1 -> {
                            iPlayerWWS.forEach(iPlayerWW -> {
                                if (mode) {
                                    playerWW1.setPastilleColor(iPlayerWW, chatColor);
                                } else {
                                    playerWW1.setColor(iPlayerWW, chatColor);
                                }
                                Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));
                            });
                        });
                        ChoiceGui.getInventory(player).open(player);
                    }));
            i.getAndIncrement();
        });
    }
}

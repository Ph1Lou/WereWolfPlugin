package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.save.ConfigurationLoader;
import fr.ph1lou.werewolfplugin.save.StuffLoader;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;

public class SaveGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("save")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new SaveGUI())
            .size(3, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menus.save.name"))
            .closeable(true)
            .build();
    private static final int SAVE_MAX = 17;

    private int j = 0;


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menus.return")).build()),
                e -> MainGUI.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        File repertoire = new File(main.getDataFolder() + File.separator + "configs");
        File[] files = repertoire.listFiles();

        contents.set(2, 0, ClickableItem.of(
                (new ItemBuilder(Material.EMERALD_BLOCK).setDisplayName(game.translate("werewolf.menus.save.new")).build())
                , e -> new AnvilGUI.Builder()
                        .text(game.translate("werewolf.menus.save.save_name"))
                        .onComplete((completion) -> {
                            save(main, completion.getText(), player);
                            return Collections.singletonList(AnvilGUI.ResponseAction.close());
                        })
                        .itemLeft(UniversalMaterial.FEATHER.getStack())
                        .onClose((player2) -> BukkitUtils.scheduleSyncDelayedTask(game, () -> SaveGUI.INVENTORY.open(player2)))
                        .title(game.translate("werewolf.menus.save.save_menu"))
                        .plugin(main)
                        .open(player)));

        if (files == null) return;

        if (j >= files.length) {
            j = Math.max(0, Math.min(SAVE_MAX, files.length) - 1);
        }

        for (int i = 0; i < SAVE_MAX; i++) {

            int column = (i + 1) / 9;
            int row = (i + 1) % 9;

            if (i >= Math.min(files.length, SAVE_MAX)) {
                contents.set(column, row, null);
            } else if (i == j) {
                contents.set(column, row,
                        ClickableItem.empty((new ItemBuilder(UniversalMaterial.FEATHER.getType())
                                .setDisplayName(game.translate("werewolf.menus.save.configuration",
                                        Formatter.format("&save&", files[i].getName()))).build())));
            } else {
                int finalI = i;
                contents.set(column, row, ClickableItem.of((new ItemBuilder(UniversalMaterial.PAPER.getType())
                        .setDisplayName(game.translate("werewolf.menus.save.configuration",
                                Formatter.format("&save&", files[i].getName())))
                        .build()), e -> j = finalI));
            }
        }

        if (files.length != 0) {
            contents.set(2, 3,
                    ClickableItem.of((new ItemBuilder(
                            UniversalMaterial.BED.getType())
                            .setDisplayName(game.translate("werewolf.menus.save.load",
                                    Formatter.format("&save&", files[j].getName())))
                            .build()), e -> {
                        load(main);
                        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.menus.save.load_message",
                                Formatter.format("&save&", files[j].getName())));
                    }));
            contents.set(2, 5, ClickableItem.of((
                    new ItemBuilder(UniversalMaterial.BARRIER.getType())
                            .setDisplayName(game.translate("werewolf.menus.save.delete",
                                    Formatter.format("&save&", files[j].getName())))
                            .build()), e -> {
                player.sendMessage(game.translate(Prefix.RED, "werewolf.menus.save.delete_message",
                        Formatter.format("&save&", files[j].getName())));
                erase(main);
                update(player, contents);
            }));
        } else {
            contents.set(2, 3, null);
            contents.set(2, 5, null);
        }
    }


    public void load(Main main) {
        WereWolfAPI game = main.getWereWolfAPI();
        File repertoire = new File(main.getDataFolder() + File.separator + "configs");
        File[] files = repertoire.listFiles();

        if (files == null || files.length <= j) return;
        String saveName = files[j].getName().replace(".json", "");

        ConfigurationLoader.loadConfig((GameManager) game, saveName);
        StuffLoader.loadStuff(game, saveName);
    }

    public void save(Main main, String saveName, Player player) {
        WereWolfAPI game = main.getWereWolfAPI();
        File repertoire = new File(main.getDataFolder() + File.separator + "configs");
        File[] files = repertoire.listFiles();
        if (files == null || files.length < SAVE_MAX) {
            ConfigurationLoader.saveConfig(game, saveName);
            StuffLoader.saveStuff(game, saveName);
            player.sendMessage(game.translate(Prefix.GREEN, "werewolf.menus.save.success"));
        } else {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.menus.save.failure"));
        }
    }

    public void erase(Main main) {

        File repertoire = new File(main.getDataFolder() + File.separator + "configs");
        File[] files = repertoire.listFiles();
        if (files == null || files.length <= j) return;
        String saveName = files[j].getName().replace(".json", "");

        ConfigurationLoader.deleteConfig(saveName);
        StuffLoader.deleteStuff(saveName);
    }
}


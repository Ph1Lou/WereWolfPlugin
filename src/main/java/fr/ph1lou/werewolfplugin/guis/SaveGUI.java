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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SaveGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("save")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new SaveGUI())
            .size(2, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menu.save.name"))
            .closeable(true)
            .build();

    private int j = 0;


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        File repertoire = new File(main.getDataFolder() + File.separator + "configs");
        File[] files = repertoire.listFiles();
        if (files == null) return;

        if (j >= files.length) {
            j = Math.max(0, Math.min(8, files.length) - 1);
        }


        for (int i = 0; i < 8; i++) {

            if (i >= Math.min(files.length, 8)) {

                contents.set(0, i + 1, null);
            } else if (i == j) {
                contents.set(0, i + 1,
                        ClickableItem.empty((new ItemBuilder(UniversalMaterial.FEATHER.getType())
                                .setDisplayName(game.translate("werewolf.menu.save.configuration",
                                        Formatter.format("&save&",files[i].getName()))).build())));
            } else {
                int finalI = i;
                contents.set(0, i + 1, ClickableItem.of((new ItemBuilder(UniversalMaterial.PAPER.getType())
                        .setDisplayName(game.translate("werewolf.menu.save.configuration",
                                Formatter.format("&save&",files[i].getName())))
                        .build()), e -> j = finalI));
            }
        }

        contents.set(1, 0, ClickableItem.of((new ItemBuilder(Material.EMERALD_BLOCK).setDisplayName(game.translate("werewolf.menu.save.new")).build()), e -> new AnvilGUI.Builder()
                .onComplete((player2, text) -> {
                    save(main, text, player);
                    return AnvilGUI.Response.close();
                })
                .preventClose()
                .text(" ")
                .title(game.translate("werewolf.menu.save.save_menu"))
                .itemLeft(new ItemStack(Material.EMERALD_BLOCK))
                .plugin(main)
                .onClose((player2) -> BukkitUtils.scheduleSyncDelayedTask(() -> SaveGUI.INVENTORY.open(player)))
                .open(player)));

        if (files.length != 0) {
            contents.set(1, 3,
                    ClickableItem.of((new ItemBuilder(
                            UniversalMaterial.BED.getType())
                            .setDisplayName(game.translate("werewolf.menu.save.load",
                                    Formatter.format("&save&",files[j].getName())))
                            .build()), e -> {
                        load(main);
                        player.sendMessage(game.translate(Prefix.GREEN , "werewolf.menu.save.load_message",
                                Formatter.format("&save&",files[j].getName())));
                    }));
            contents.set(1, 5, ClickableItem.of((
                    new ItemBuilder(UniversalMaterial.BARRIER.getType())
                            .setDisplayName(game.translate("werewolf.menu.save.delete",
                                    Formatter.format("&save&",files[j].getName())))
                            .build()), e -> {
                player.sendMessage(game.translate(Prefix.RED , "werewolf.menu.save.delete_message",
                        Formatter.format("&save&",files[j].getName())));
                erase(main);
            }));
        } else {
            contents.set(1, 3, null);
            contents.set(1, 5, null);
        }

    }


    public void load(Main main) {
        WereWolfAPI game = main.getWereWolfAPI();
        File repertoire = new File(main.getDataFolder() + File.separator + "configs");
        File[] files = repertoire.listFiles();

        if (files == null || files.length <= j) return;

        ConfigurationLoader.loadConfig(main, (GameManager) game, files[j].getName().replace(".json", ""));
        StuffLoader.loadStuff(main, (GameManager) game, files[j].getName().replace(".json", ""));
    }

    public void save(Main main, String saveName, Player player) {
        WereWolfAPI game = main.getWereWolfAPI();
        File repertoire = new File(main.getDataFolder() + File.separator + "configs");
        File[] files = repertoire.listFiles();
        if (files == null || files.length < 8) {
            ConfigurationLoader.saveConfig(main, saveName);
            StuffLoader.saveStuff(main, saveName);
            player.sendMessage(game.translate(Prefix.GREEN , "werewolf.menu.save.success"));
        } else player.sendMessage(game.translate(Prefix.RED , "werewolf.menu.save.failure"));
    }

    public void erase(Main main) {

        File repertoire = new File(main.getDataFolder() + File.separator + "configs");
        File[] files = repertoire.listFiles();
        if (files == null || files.length <= j) return;

        ConfigurationLoader.deleteConfig(main, files[j].getName().replace(".json", ""));
        StuffLoader.deleteStuff(main, files[j].getName().replace(".json", ""));
    }
}


package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.save.FileUtils_;
import io.github.ph1lou.werewolfplugin.save.Serializer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Save implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("save")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Save())
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
                contents.set(0, i + 1, ClickableItem.empty((new ItemBuilder(UniversalMaterial.FEATHER.getType()).setDisplayName(game.translate("werewolf.menu.save.configuration", files[i].getName())).build())));
            } else {
                int finalI = i;
                contents.set(0, i + 1, ClickableItem.of((new ItemBuilder(UniversalMaterial.PAPER.getType()).setDisplayName(game.translate("werewolf.menu.save.configuration", files[i].getName())).build()), e -> j = finalI));
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
                .item(new ItemStack(Material.EMERALD_BLOCK))
                .plugin(main)
                .onClose((player2) -> Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> Save.INVENTORY.open(player)))
                .open(player)));

        if (files.length != 0) {
            contents.set(1, 3,
                    ClickableItem.of((new ItemBuilder(
                            UniversalMaterial.BED.getType())
                            .setDisplayName(game.translate("werewolf.menu.save.load",
                                    files[j].getName())).build()), e -> {
                        load(main);
                        player.sendMessage(game.translate("werewolf.menu.save.load_message", files[j].getName()));
                    }));
            contents.set(1, 5, ClickableItem.of((
                    new ItemBuilder(UniversalMaterial.BARRIER.getType())
                            .setDisplayName(game.translate("werewolf.menu.save.delete",
                                    files[j].getName())).build()), e -> {
                player.sendMessage(game.translate("werewolf.menu.save.delete_message",
                        files[j].getName()));
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
        if (files.length <= j) return;
        FileUtils_.loadConfig(main, files[j].getName().replace(".json", ""));
        game.getStuffs().load(files[j].getName().replace(".json", ""));
    }

    public void save(Main main, String saveName, Player player) {
        WereWolfAPI game = main.getWereWolfAPI();
        File file = new File(main.getDataFolder() + File.separator + "configs", saveName + ".json");
        File repertoire = new File(main.getDataFolder() + File.separator + "configs");
        File[] files = repertoire.listFiles();
        if (files == null || files.length < 8) {
            FileUtils_.save(file, Serializer.serialize(game.getConfig()));
            game.getStuffs().save(saveName);
            player.sendMessage(game.translate("werewolf.menu.save.success"));
        } else player.sendMessage(game.translate("werewolf.menu.save.failure"));
    }

    public void erase(Main main) {
        WereWolfAPI game = main.getWereWolfAPI();
        File repertoire = new File(main.getDataFolder() + File.separator + "configs");
        File[] files = repertoire.listFiles();

        if (files.length <= j) return;

        File file = new File(main.getDataFolder() + File.separator + "configs", files[j].getName());
        if (!file.delete()) {
            Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.menu.save.delete_failed", files[j].getName()));
        }
        file = new File(main.getDataFolder() + File.separator + "stuffs", files[j].getName().replaceFirst(".json", ".yml"));
        if (!file.delete()) {
            Bukkit.getConsoleSender().sendMessage(game.translate("werewolf.menu.save.delete_failed", files[j].getName().replaceFirst(".json", ".yml")));
        }

    }
}


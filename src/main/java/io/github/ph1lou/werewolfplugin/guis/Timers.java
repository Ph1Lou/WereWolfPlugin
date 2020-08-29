package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.TimerRegister;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Timers implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("timers")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Timers())
            .size(Math.min(54, (JavaPlugin.getPlugin(Main.class).getRegisterTimers().size() / 9 + 2) * 9) / 9, 9)
            .title(JavaPlugin.getPlugin(Main.class).getCurrentGame().translate("werewolf.menu.timers.name"))
            .closeable(true)
            .build();


    private String key = null;

    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();
        key = main.getRegisterTimers().get(0).getKey();
        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();
        ConfigWereWolfAPI config = game.getConfig();

        String c = game.getScore().conversion(config.getTimerValues().get(key));

        contents.set(0, 1, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "-10m", c)).build()), e -> selectMinusTimer(game, this.key, 600)));
        contents.set(0, 2, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "-1m", c)).build()), e -> selectMinusTimer(game, this.key, 60)));
        contents.set(0, 3, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "-10s", c)).build()), e -> selectMinusTimer(game, this.key, 10)));
        contents.set(0, 5, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "+10s", c)).build()), e -> selectPlusTimer(game, this.key, 10)));
        contents.set(0, 6, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "+1m", c)).build()), e -> selectPlusTimer(game, this.key, 60)));
        contents.set(0, 7, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "+10m", c)).build()), e -> selectPlusTimer(game, this.key, 600)));

        int i = 0;

        for (TimerRegister timer : main.getRegisterTimers()) {
            List<String> lore = new ArrayList<>(timer.getLore());
            contents.set(i / 9 + 1, i % 9, ClickableItem.of((new ItemBuilder(timer.getKey().equals(key) ? Material.FEATHER : Material.ANVIL).setLore(lore).setDisplayName(game.translate(timer.getKey(), game.getScore().conversion(config.getTimerValues().get(timer.getKey())))).build()), e -> this.key = timer.getKey()));
            i++;
        }

    }

    public void selectMinusTimer(GameManager game, String key, int value) {
        ConfigWereWolfAPI config = game.getConfig();
        int j = config.getTimerValues().get(key);

        if (j >= value) {
            config.getTimerValues().put(key, j - value);
        }
    }

    public void selectPlusTimer(GameManager game, String key, int value) {
        ConfigWereWolfAPI config = game.getConfig();
        int j = config.getTimerValues().get(key);
        config.getTimerValues().put(key, j + value);
    }
}


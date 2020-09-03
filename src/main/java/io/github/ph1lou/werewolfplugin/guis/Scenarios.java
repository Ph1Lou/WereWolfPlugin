package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.ScenarioRegister;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Scenarios implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("scenarios")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Scenarios())
            .size(Math.min(54, (JavaPlugin.getPlugin(Main.class).getRegisterScenarios().size() / 9 + 2) * 9) / 9, 9)
            .title(JavaPlugin.getPlugin(Main.class).getCurrentGame().translate("werewolf.menu.scenarios.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();
        ConfigWereWolfAPI config = game.getConfig();

        int i = 0;
        for (ScenarioRegister scenarioRegister : main.getRegisterScenarios()) {

            List<String> lore = new ArrayList<>(scenarioRegister.getLore());
            ItemStack itemStack;

            if (config.getScenarioValues().get(scenarioRegister.getKey())) {
                lore.add(0, game.translate("werewolf.utils.enable", ""));
                itemStack = UniversalMaterial.GREEN_TERRACOTTA.getStack();
            } else {
                lore.add(0, game.translate("werewolf.utils.disable", ""));
                itemStack = UniversalMaterial.RED_TERRACOTTA.getStack();
            }
            contents.set(i / 9 + 1, i % 9, ClickableItem.of((new ItemBuilder(itemStack).setDisplayName(game.translate(scenarioRegister.getKey())).setLore(lore).build()), e -> {
                config.getScenarioValues().put(scenarioRegister.getKey(), !config.getScenarioValues().get(scenarioRegister.getKey()));
                game.getScenarios().update();
            }));
            i++;
        }
    }
}


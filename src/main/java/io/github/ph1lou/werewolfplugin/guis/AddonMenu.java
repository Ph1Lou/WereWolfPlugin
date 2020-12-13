package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.registers.AddonRegister;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class AddonMenu implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("addonMenu")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new AddonMenu())
            .size(4, 9)
            .title(JavaPlugin.getPlugin(Main.class)
                    .getWereWolfAPI().translate("werewolf.menu.addon.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        int i = 1;



        for (AddonRegister addon : main.getRegisterManager().getAddonsRegister()) {

            List<String> lore =addon.getLoreKey().stream().map(game::translate).collect(Collectors.toList());
            lore.add(game.translate("werewolf.utils.author"));
            lore.addAll(addon.getAuthors());

            contents.set(i / 9, i % 9, ClickableItem.of((new ItemBuilder(addon.getItem()).setDisplayName(game.translate(addon.getAddonKey())).setLore(lore).build()), e ->
                    addon.getAction().make(player, INVENTORY)));
            i++;
        }
        contents.set(0, 0, ClickableItem.of((new ItemBuilder(
                UniversalMaterial.COMPASS.getType())
                .setDisplayName(game.translate("werewolf.menu.return"))
                .build()), e -> Config.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {


    }


}


package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IStuffManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.save.StuffLoader;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class StuffsGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("stuffs")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new StuffsGUI())
            .size(2, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menus.stuff.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menus.return")).build()), e -> MainGUI.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();

        contents.set(0, 4, ClickableItem.of((new ItemBuilder(Material.JUKEBOX).setDisplayName(game.translate("werewolf.menus.stuff.chill"))).build(),
                e -> StuffLoader.loadStuffChill(game)));

        contents.set(0, 2, ClickableItem.of((new ItemBuilder(Material.EGG).setDisplayName(game.translate("werewolf.menus.stuff.reset"))).build(),
                e -> StuffLoader.loadAllStuffDefault(game)));

        contents.set(1, 1, ClickableItem.of((new ItemBuilder(Material.BARRIER).setDisplayName(game.translate("werewolf.menus.stuff.delete"))).build(), e -> {
            game.getStuffs().clearStartLoot();
            game.getStuffs().clearDeathLoot();
        }));
        contents.set(1, 4, ClickableItem.of((new ItemBuilder(Material.CHEST).setDisplayName(game.translate("werewolf.menus.stuff.start"))).build(), e -> {


            if (!game.getModerationManager()
                    .checkAccessAdminCommand(
                            "werewolf.commands.admin.stuff_start.command",
                            player)) {
                return;
            }

            IStuffManager stuffManager = game.getStuffs();
            PlayerInventory inventory = player.getInventory();
            player.setGameMode(GameMode.CREATIVE);

            if (!stuffManager.isInTempStuff(uuid)) {
                ItemStack[] items = new ItemStack[40];
                for (int i = 0; i < 40; i++) {
                    items[i] = inventory.getItem(i);
                }
                stuffManager.putTempStuff(uuid, items);
            }

            inventory.clear();

            ItemStack itemStack = new ItemStack(Material.BARRIER);
            inventory.setArmorContents(new ItemStack[]{itemStack, itemStack, itemStack, itemStack});

            game.getStuffs().getStartLoot().forEach(inventory::addItem);

            TextComponent msg = VersionUtils.getVersionUtils().createClickableText(game.translate(Prefix.YELLOW, "werewolf.commands.admin.stuff_start.valid"),
                    String.format("/a %s", game.translate("werewolf.commands.admin.stuff_start.command")),
                    ClickEvent.Action.RUN_COMMAND);

            player.spigot().sendMessage(msg);
            player.closeInventory();
        }));
        contents.set(1, 7, ClickableItem.of((new ItemBuilder(Material.ENDER_CHEST).setDisplayName(game.translate("werewolf.menus.stuff.death"))).build(), e -> {

            if (!game.getModerationManager()
                    .checkAccessAdminCommand(
                            "werewolf.commands.admin.loot_death.command",
                            player)) {
                return;
            }

            IStuffManager stuffManager = game.getStuffs();
            PlayerInventory inventory = player.getInventory();
            player.setGameMode(GameMode.CREATIVE);

            if (!stuffManager.isInTempStuff(uuid)) {
                ItemStack[] items = new ItemStack[40];
                for (int i = 0; i < 40; i++) {
                    items[i] = inventory.getItem(i);
                }
                stuffManager.putTempStuff(uuid, items);
            }

            inventory.clear();

            ItemStack itemStack = new ItemStack(Material.BARRIER);
            inventory.setArmorContents(new ItemStack[]{itemStack, itemStack, itemStack, itemStack});

            stuffManager.getDeathLoot().forEach(inventory::addItem);

            TextComponent msg = VersionUtils.getVersionUtils().createClickableText(game.translate(Prefix.YELLOW, "werewolf.commands.admin.loot_death.valid"),
                    String.format("/a %s", game.translate("werewolf.commands.admin.loot_death.command")),
                    ClickEvent.Action.RUN_COMMAND);
            player.spigot().sendMessage(msg);
            player.closeInventory();
        }));

    }

}


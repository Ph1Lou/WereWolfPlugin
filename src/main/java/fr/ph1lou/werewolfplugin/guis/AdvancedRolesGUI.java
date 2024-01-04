package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.IStuffManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedRolesGUI implements InventoryProvider {

    private final Role register;
    private final int page;

    public AdvancedRolesGUI(Role register, int page) {
        this.register = register;
        this.page = page;
    }

    public static SmartInventory getInventory(Role register, int page) {

        Main api = JavaPlugin.getPlugin(Main.class);

        WereWolfAPI game = api.getWereWolfAPI();
        return SmartInventory.builder()
                .id("advanced" + register.key())
                .manager(api.getInvManager())
                .provider(new AdvancedRolesGUI(register, page))
                .size(InventoryUtils.getRowNumbers(
                        (register.configurations().length
                                + register.timers().length +
                                register.configValues().length + 1) * 2, true), 9)
                .title(game.translate("werewolf.menus.advanced_tool_role.menu",
                        Formatter.role(game.translate(register.key()))))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of(new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menus.return")).build(),
                e -> RolesGUI.getInventory(player, register.category()).open(player, page)));

        contents.set(0, 2, ClickableItem.of(new ItemBuilder(UniversalMaterial.CHEST.getType()).setDisplayName(game.translate("werewolf.menus.advanced_tool_role.config",
                        Formatter.role(game.translate(register.key())))).build(),
                event -> manageStuff(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();


        AtomicInteger i = new AtomicInteger(4);

        AdvancedConfigurationUtils.getIntConfigs(game, this.register.configValues()).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });

        AdvancedConfigurationUtils.getTimers(game, this.register.timers()).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });

        AdvancedConfigurationUtils.getConfigs(game, this.register.configurations(), () -> getInventory(this.register, this.page)).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });
    }


    private void manageStuff(Player player) {

        Main api = JavaPlugin.getPlugin(Main.class);

        WereWolfAPI game = api.getWereWolfAPI();

        UUID uuid = player.getUniqueId();

        if (!game.getModerationManager()
                .checkAccessAdminCommand("werewolf.commands.admin.loot_role.command",
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

        for (ItemStack item : game.getStuffs().getStuffRole(this.register.key())) {
            player.getInventory().addItem(item);
        }
        TextComponent msg = VersionUtils.getVersionUtils().createClickableText(game.translate(Prefix.YELLOW, "werewolf.commands.admin.loot_role.valid",
                        Formatter.role(game.translate(register.key()))),
                String.format("/a %s %s",
                        game.translate("werewolf.commands.admin.loot_role.command"), this.register.key()),
                ClickEvent.Action.RUN_COMMAND
        );
        player.spigot().sendMessage(msg);
        player.closeInventory();
    }
}


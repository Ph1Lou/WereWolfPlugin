package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.game.IStuffManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.registers.impl.RoleRegister;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedRoleMenu implements InventoryProvider {

    private final RoleRegister register;

    public AdvancedRoleMenu(RoleRegister register) {
        this.register = register;
    }

    public static SmartInventory getInventory(RoleRegister register) {

        GetWereWolfAPI api = JavaPlugin.getPlugin(Main.class);

        WereWolfAPI game = api.getWereWolfAPI();
        return SmartInventory.builder()
                .id("advanced" + register.getKey())
                .manager(api.getInvManager())
                .provider(new AdvancedRoleMenu(register))
                .size(Math.min(54, (Math.max(0, (register.getConfig().size() * 2 - 6)) / 9 + 1) * 9) / 9, 9)
                .title(game.translate("werewolf.menu.advanced_tool_role.menu",
                                Formatter.role(game.translate(register.getKey()))))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of(new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menu.return")).build(),
                e -> Roles.getInventory(player, Category.WEREWOLF).open(player)));

        contents.set(0, 2, ClickableItem.of(new ItemBuilder(UniversalMaterial.CHEST.getType()).setDisplayName(game.translate("werewolf.menu.advanced_tool_role.config",
                        Formatter.role(game.translate(register.getKey())))).build(),
                event -> manageStuff(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        AtomicInteger i = new AtomicInteger(4);

        register.getConfig().forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem.apply(game));
            i.set(i.get() + 2);
        });

        AdvancedConfigRole.getTimersRole(main, this.register).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });

        AdvancedConfigRole.getConfigsRole(main, this.register).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });

    }


    private void manageStuff(Player player) {

        GetWereWolfAPI api = Bukkit.getServer().getServicesManager().load(GetWereWolfAPI.class);
        if (api == null) {
            throw new RuntimeException("WereWolfPlugin not loaded");
        }

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

        if (!stuffManager.getTempStuff().containsKey(uuid)) {

            Inventory inventoryTemp = Bukkit.createInventory(player, 45);
            for (int j = 0; j < 40; j++) {
                inventoryTemp.setItem(j, inventory.getItem(j));
            }
            stuffManager.getTempStuff().put(uuid, inventoryTemp);
        }

        for (int j = 0; j < 40; j++) {
            inventory.setItem(j, null);
        }

        for (ItemStack item : game.getStuffs().getStuffRoles().get(this.register.getKey())) {
            if (item != null) {
                player.getInventory().addItem(item);
            }
        }
        TextComponent msg = new TextComponent(game.translate(Prefix.YELLOW.getKey() , "werewolf.commands.admin.loot_role.valid",
                Formatter.role(game.translate(register.getKey()))));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/a %s %s", game.translate("werewolf.commands.admin.loot_role.command"), this.register.getKey())));
        player.spigot().sendMessage(msg);
        player.closeInventory();
    }
}


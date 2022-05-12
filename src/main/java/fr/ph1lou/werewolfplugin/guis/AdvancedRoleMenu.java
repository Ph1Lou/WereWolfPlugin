package fr.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.game.IStuffManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AdvancedRoleMenu implements InventoryProvider {

    private final Wrapper<IRole, Role> register;

    public AdvancedRoleMenu(Wrapper<IRole, Role> register) {
        this.register = register;
    }

    public static SmartInventory getInventory(Wrapper<IRole, Role> register) {

        GetWereWolfAPI api = JavaPlugin.getPlugin(Main.class);

        WereWolfAPI game = api.getWereWolfAPI();
        return SmartInventory.builder()
                .id("advanced" + register.getMetaDatas().key())
                .manager(api.getInvManager())
                .provider(new AdvancedRoleMenu(register))
                .size(Math.min(54, (Math.max(0, ((register.getMetaDatas().configurations().length
                +register.getMetaDatas().timers().length +
                        register.getMetaDatas().intValues().length)
                        * 2 - 6)) / 9 + 1) * 9) / 9, 9)
                .title(game.translate("werewolf.menu.advanced_tool_role.menu",
                                Formatter.role(game.translate(register.getMetaDatas().key()))))
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
                        Formatter.role(game.translate(register.getMetaDatas().key())))).build(),
                event -> manageStuff(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        AtomicInteger i = new AtomicInteger(4);

       // register.getConfig().forEach(clickableItem -> {
       //     contents.set(i.get() / 9, i.get() % 9, clickableItem.apply(game));
       //     i.set(i.get() + 2);
       // });

        this.getTimersRole(main, this.register).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });

        this.getConfigsRole(main, this.register).forEach(clickableItem -> {
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

        for (ItemStack item : game.getStuffs().getStuffRoles().get(this.register.getMetaDatas().key())) {
            if (item != null) {
                player.getInventory().addItem(item);
            }
        }
        TextComponent msg = new TextComponent(game.translate(Prefix.YELLOW , "werewolf.commands.admin.loot_role.valid",
                Formatter.role(game.translate(register.getMetaDatas().key()))));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/a %s %s",
                game.translate("werewolf.commands.admin.loot_role.command"), this.register.getMetaDatas().key())));
        player.spigot().sendMessage(msg);
        player.closeInventory();
    }

    public List<ClickableItem> getConfigsRole(GetWereWolfAPI main, Wrapper<IRole, Role> roleRegister){
        WereWolfAPI game = main.getWereWolfAPI();
        return Arrays.stream(roleRegister.getMetaDatas().configurations())
                .map(configRegister -> GlobalConfigs.getClickableItem(game, new Wrapper<>(roleRegister.getClazz(),
                        configRegister, roleRegister.getAddonKey(), null)))
                .collect(Collectors.toList());
    }

    public List<ClickableItem> getTimersRole(GetWereWolfAPI main, Wrapper<IRole, Role> roleRegister){

        WereWolfAPI game = main.getWereWolfAPI();
        return Arrays.stream(roleRegister.getMetaDatas().timers())
                .map(timerRegister -> {
                    IConfiguration config = game.getConfig();
                    List<String> lore = new ArrayList<>(Arrays.asList(game.translate("werewolf.menu.left"),
                            game.translate("werewolf.menu.right")));
                    Arrays.stream(timerRegister.loreKey())
                            .map(game::translate)
                            .map(s -> Arrays.stream(s.split("\\n"))
                                    .collect(Collectors.toList()))
                            .forEach(lore::addAll);
                    return ClickableItem.of(new ItemBuilder(UniversalMaterial.ANVIL.getStack())
                            .setLore(lore)
                            .setDisplayName(game.translate(timerRegister.key(),
                                    Formatter.timer(Utils.conversion(config.getTimerValue(timerRegister.key())))))
                            .build(),e -> {


                        if (e.isLeftClick()) {
                            config.moveTimer(timerRegister.key(), timerRegister.step());
                        } else if (config.getTimerValue(timerRegister.key()) - timerRegister.step() > 0) {
                            config.moveTimer(timerRegister.key(), - timerRegister.step());
                        }

                        e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                                .setLore(lore)
                                .setDisplayName(game.translate(timerRegister.key(),
                                        Formatter.timer(Utils.conversion(config.getTimerValue(timerRegister.key())))))
                                .build());
                    });
                }).collect(Collectors.toList());
    }
}


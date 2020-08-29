package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.ItemBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class Stuffs implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("stuffs")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Stuffs())
            .size(2, 9)
            .title(JavaPlugin.getPlugin(Main.class).getCurrentGame().translate("werewolf.menu.stuff.name"))
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

        contents.set(0, 2, ClickableItem.of((new ItemBuilder(Material.EGG).setDisplayName(game.translate("werewolf.menu.stuff.normal"))).build(), e -> game.getStuffs().loadAllStuffDefault()));
        contents.set(0, 4, ClickableItem.of((new ItemBuilder(UniversalMaterial.GOLDEN_SWORD.getType()).setDisplayName(game.translate("werewolf.menu.stuff.meet_up"))).build(), e -> game.getStuffs().loadAllStuffMeetUP()));
        contents.set(0, 6, ClickableItem.of((new ItemBuilder(Material.JUKEBOX).setDisplayName(game.translate("werewolf.menu.stuff.chill"))).build(), e -> game.getStuffs().loadStuffChill()));
        contents.set(1, 1, ClickableItem.of((new ItemBuilder(Material.BARRIER).setDisplayName(game.translate("werewolf.menu.stuff.delete"))).build(), e -> {
            game.getStuffs().clearStartLoot();
            game.getStuffs().clearDeathLoot();
        }));
        contents.set(1, 4, ClickableItem.of((new ItemBuilder(Material.CHEST).setDisplayName(game.translate("werewolf.menu.stuff.start"))).build(), e -> {
            player.setGameMode(GameMode.CREATIVE);
            PlayerInventory inventory = player.getInventory();

            for (int j = 0; j < 40; j++) {
                inventory.setItem(j, game.getStuffs().getStartLoot().getItem(j));
            }

            TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.stuff_start.valid"));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a lootStart"));
            player.spigot().sendMessage(msg);
            player.closeInventory();
        }));
        contents.set(1, 7, ClickableItem.of((new ItemBuilder(Material.ENDER_CHEST).setDisplayName(game.translate("werewolf.menu.stuff.death"))).build(), e -> {
            player.setGameMode(GameMode.CREATIVE);
            PlayerInventory inventory = player.getInventory();
            inventory.clear();

            for (ItemStack i : game.getStuffs().getDeathLoot()) {
                if (i != null) {
                    inventory.addItem(i);
                }
            }

            TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.loot_death.valid"));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a lootDeath"));
            player.spigot().sendMessage(msg);
            player.closeInventory();
        }));

    }

}


package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IStuffManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.UpdateStuffEvent;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CommandLootDeath implements ICommands {


    private final Main main;

    public CommandLootDeath(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        IStuffManager stuffManager = game.getStuffs();
        UUID uuid = player.getUniqueId();

        stuffManager.clearDeathLoot();

        for (ItemStack i : player.getInventory().getContents()) {
            stuffManager.addDeathLoot(i);
        }
        player.sendMessage(game.translate("werewolf.commands.admin.loot_death.perform"));
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);

        Inventory inventory;
        if (stuffManager.getTempStuff().containsKey(uuid)) {
            inventory = stuffManager.getTempStuff().get(uuid);
            stuffManager.getTempStuff().remove(uuid);
        } else inventory = Bukkit.createInventory(player, 45);

        for (int j = 0; j < 40; j++) {
            player.getInventory().setItem(j, inventory.getItem(j));
        }

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}

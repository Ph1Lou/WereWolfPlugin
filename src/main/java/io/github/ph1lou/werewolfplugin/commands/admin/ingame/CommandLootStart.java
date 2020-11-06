package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.StuffManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.UpdateStuffEvent;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class CommandLootStart implements Commands {


    private final Main main;

    public CommandLootStart(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        PlayerInventory inventory = player.getInventory();
        StuffManager stuffManager = game.getStuffs();
        UUID uuid = player.getUniqueId();

        stuffManager.clearStartLoot();

        if (!stuffManager.getTempStuff().containsKey(uuid)) {
            stuffManager.getTempStuff().put(uuid, Bukkit.createInventory(player, 45));
        }

        for (int j = 0; j < 40; j++) {
            stuffManager.getStartLoot().setItem(j, inventory.getItem(j));
            inventory.setItem(j, stuffManager.getTempStuff().get(uuid).getItem(j));
        }
        stuffManager.getTempStuff().remove(uuid);
        player.sendMessage(game.translate("werewolf.commands.admin.stuff_start.perform"));
        player.setGameMode(GameMode.ADVENTURE);

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}

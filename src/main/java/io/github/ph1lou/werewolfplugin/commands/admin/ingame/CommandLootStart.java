package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IStuffManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.UpdateStuffEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class CommandLootStart implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        PlayerInventory inventory = player.getInventory();
        IStuffManager stuffManager = game.getStuffs();
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

package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.IStuffManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.events.UpdateStuffEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.loot_death.command", descriptionKey = "",
        moderatorAccess = true,
        autoCompletion = false,
        argNumbers = 0)
public class CommandLootDeath implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IStuffManager stuffManager = game.getStuffs();
        UUID uuid = player.getUniqueId();

        stuffManager.clearDeathLoot();

        for (ItemStack i : player.getInventory().getContents()) {
            stuffManager.addDeathLoot(i);
        }
        player.sendMessage(game.translate(Prefix.GREEN , "werewolf.commands.admin.loot_death.perform"));
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

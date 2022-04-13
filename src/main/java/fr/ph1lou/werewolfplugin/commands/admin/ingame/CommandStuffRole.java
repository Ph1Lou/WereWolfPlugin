package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.IStuffManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.events.UpdateStuffEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CommandStuffRole implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IStuffManager stuffManager = game.getStuffs();
        UUID uuid = player.getUniqueId();

        if (!stuffManager.getStuffRoles().containsKey(args[0])) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.invalid_key"));
            return;
        }

        stuffManager.getStuffRoles().get(args[0]).clear();
        for (ItemStack i : player.getInventory().getContents()) {
            stuffManager.getStuffRoles().get(args[0]).add(i);
        }
        player.sendMessage(game.translate(Prefix.GREEN.getKey() , "werewolf.commands.admin.loot_role.perform",
                Formatter.role(game.translate(args[0]))));

        Inventory inventory;
        if (stuffManager.getTempStuff().containsKey(uuid)) {
            inventory = stuffManager.getTempStuff().get(uuid);
            stuffManager.getTempStuff().remove(uuid);
        } else inventory = Bukkit.createInventory(player, 45);

        for (int j = 0; j < 40; j++) {
            player.getInventory().setItem(j, inventory.getItem(j));
        }

        player.setGameMode(GameMode.ADVENTURE);

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}

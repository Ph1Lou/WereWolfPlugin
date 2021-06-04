package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CommandInventory implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Player pInv = Bukkit.getPlayer(args[0]);

        if (pInv == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 45, args[0]);

        for (int i = 0; i < 40; i++) {
            inv.setItem(i, pInv.getInventory().getItem(i));
        }

        player.openInventory(inv);
    }
}

package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CommandInventory extends Commands {

    final MainLG main;

    public CommandInventory(MainLG main) {
        this.main = main;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.inv.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(main.text.getText(140));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(main.text.getText(54));
            return;
        }
        if (Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(main.text.getText(132));
            return;
        }
        Player pInv = Bukkit.getPlayer(args[0]);
        Inventory inv = Bukkit.createInventory(null, 45, args[0]);

        for (int i = 0; i < 40; i++) {
            inv.setItem(i, pInv.getInventory().getItem(i));
        }

        ((Player) sender).openInventory(inv);
    }
}

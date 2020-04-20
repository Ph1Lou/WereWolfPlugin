package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandLootDeath extends Commands {

    final MainLG main;

    public CommandLootDeath(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.lootDeath.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(main.text.getText(140));
            return;
        }

        if (!main.isState(StateLG.LOBBY)) {
            sender.sendMessage(main.text.getText(119));
            return;
        }
        main.stufflg.clearDeathLoot();
        for (ItemStack i : ((Player) sender).getInventory().getContents()) {
            if (i != null) {
                main.stufflg.addDeathLoot(i);
            }
        }
        sender.sendMessage(main.text.getText(152));
        ((Player) sender).getInventory().clear();
        ((Player) sender).setGameMode(GameMode.ADVENTURE);
    }
}

package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandLootStart extends Commands {

    final MainLG main;

    public CommandLootStart(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.lootStart.use")) {
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

        main.stufflg.clearStartLoot();
        for (ItemStack i : ((Player) sender).getInventory().getContents()) {
            if (i != null) {
                main.stufflg.addStartLoot(i);
            }
        }

        sender.sendMessage(main.text.getText(151));
        ((Player) sender).getInventory().clear();
        ((Player) sender).setGameMode(GameMode.ADVENTURE);
    }
}

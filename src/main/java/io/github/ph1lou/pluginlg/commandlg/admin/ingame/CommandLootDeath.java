package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandLootDeath extends Commands {


    public CommandLootDeath(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

     GameManager game = main.currentGame;

        TextLG text = game.text;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.lootDeath.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        if (!game.isState(StateLG.LOBBY)) {
            sender.sendMessage(text.getText(119));
            return;
        }
        game.stufflg.clearDeathLoot();
        for (ItemStack i : ((Player) sender).getInventory().getContents()) {
            if (i != null) {
                game.stufflg.addDeathLoot(i);
            }
        }
        sender.sendMessage(text.getText(152));
        ((Player) sender).getInventory().clear();
        ((Player) sender).setGameMode(GameMode.ADVENTURE);
    }
}

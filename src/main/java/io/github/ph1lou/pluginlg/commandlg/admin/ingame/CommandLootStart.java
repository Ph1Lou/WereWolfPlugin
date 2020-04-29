package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class CommandLootStart extends Commands {


    public CommandLootStart(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game = main.currentGame;

        TextLG text = game.text;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.lootStart.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        if (!game.isState(StateLG.LOBBY)) {
            sender.sendMessage(text.getText(119));
            return;
        }
        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();

        game.stufflg.clearStartLoot();

        for (int j = 0; j < 40; j++) {
            game.stufflg.getStartLoot().setItem(j, inventory.getItem(j));
            inventory.setItem(j, null);
        }

        sender.sendMessage(text.getText(151));
        ((Player) sender).setGameMode(GameMode.ADVENTURE);
    }
}

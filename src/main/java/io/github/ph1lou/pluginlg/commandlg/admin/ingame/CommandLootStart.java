package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
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

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.lootStart.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (!game.isState(StateLG.LOBBY)) {
            sender.sendMessage(game.translate("werewolf.check.already_begin"));
            return;
        }
        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();

        game.stufflg.clearStartLoot();

        for (int j = 0; j < 40; j++) {
            game.stufflg.getStartLoot().setItem(j, inventory.getItem(j));
            inventory.setItem(j, null);
        }

        sender.sendMessage(game.translate("werewolf.commands.admin.stuff_start.perform"));
        ((Player) sender).setGameMode(GameMode.ADVENTURE);
    }
}

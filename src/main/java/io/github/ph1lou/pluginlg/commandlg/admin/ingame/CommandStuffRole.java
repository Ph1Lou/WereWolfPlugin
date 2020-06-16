package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandStuffRole implements Commands {


    private final MainLG main;

    public CommandStuffRole(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.stuffRole.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(game.translate("werewolf.check.number_required"));
            return;
        }
        if(!game.getStuffs().getStuffRoles().containsKey(args[0])){
            sender.sendMessage(game.translate("werewolf.check.invalid_key"));
            return;
        }
        
        game.getStuffs().getStuffRoles().get(args[0]).clear();
        for (ItemStack i : ((Player) sender).getInventory().getContents()) {
            if (i != null) {
                game.getStuffs().getStuffRoles().get(args[0]).add(i);
            }
        }
        sender.sendMessage(game.translate("werewolf.commands.admin.loot_role.perform"));
        ((Player) sender).getInventory().clear();
        ((Player) sender).setGameMode(GameMode.ADVENTURE);
    }
}

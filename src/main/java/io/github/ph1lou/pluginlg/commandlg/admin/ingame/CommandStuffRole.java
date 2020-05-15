package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandStuffRole extends Commands {


    public CommandStuffRole(MainLG main) {
        super(main);
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
        try {
            int j = Integer.parseInt(args[0]);
            game.stufflg.role_stuff.get(RoleLG.values()[j]).clear();
            for (ItemStack i : ((Player) sender).getInventory().getContents()) {
                if (i != null) {
                    game.stufflg.role_stuff.get(RoleLG.values()[j]).add(i);
                }
            }
            sender.sendMessage(game.translate("werewolf.commands.admin.loot_role.perform"));
            ((Player) sender).getInventory().clear();
            ((Player) sender).setGameMode(GameMode.ADVENTURE);
        } catch (NumberFormatException ignored) {
        }
    }
}

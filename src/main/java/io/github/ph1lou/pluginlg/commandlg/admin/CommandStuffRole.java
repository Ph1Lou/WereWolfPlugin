package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandStuffRole extends Commands {

    final MainLG main;

    public CommandStuffRole(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.stuffRole.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(main.text.getText(140));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(String.format(main.text.getText(190), 1));
            return;
        }
        try {
            int j = Integer.parseInt(args[0]);
            main.stufflg.role_stuff.get(RoleLG.values()[j]).clear();
            for (ItemStack i : ((Player) sender).getInventory().getContents()) {
                if (i != null) {
                    main.stufflg.role_stuff.get(RoleLG.values()[j]).add(i);
                }
            }
            sender.sendMessage(main.text.getText(199));
            ((Player) sender).getInventory().clear();
            ((Player) sender).setGameMode(GameMode.ADVENTURE);
        } catch (NumberFormatException ignored) {
        }
    }
}

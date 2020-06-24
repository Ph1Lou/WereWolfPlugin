package io.github.ph1lou.werewolfplugin.commandlg.admin.ingame;

import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandConfig implements Commands {


    private final Main main;

    public CommandConfig(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.config.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        game.optionlg.toolBar((Player) sender);
    }
}

package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChat implements Commands {


    private final MainLG main;

    public CommandChat(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!sender.hasPermission("a.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        game.getConfig().getConfigValues().put(ToolLG.CHAT, !game.getConfig().getConfigValues().get(ToolLG.CHAT));

        Bukkit.broadcastMessage(game.getConfig().getConfigValues().get(ToolLG.CHAT) ? game.translate("werewolf.commands.admin.chat.on") : game.translate("werewolf.commands.admin.chat.off"));
    }
}

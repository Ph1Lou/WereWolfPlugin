package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChat implements Commands {


    private final Main main;

    public CommandChat(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!sender.hasPermission("a.chat.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        game.getConfig().getConfigValues().put("werewolf.menu.global.chat", !game.getConfig().getConfigValues().get("werewolf.menu.global.chat"));

        Bukkit.broadcastMessage(game.getConfig().getConfigValues().get("werewolf.menu.global.chat") ? game.translate("werewolf.commands.admin.chat.on") : game.translate("werewolf.commands.admin.chat.off"));
    }
}

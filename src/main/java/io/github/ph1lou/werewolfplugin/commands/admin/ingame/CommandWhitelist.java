package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.game.ModerationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWhitelist implements Commands {

    private final Main main;

    public CommandWhitelist(Main main) {
        this.main = main;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.getCurrentGame();
        ModerationManager moderationManager = game.getModerationManager();

        if (!sender.hasPermission("a.whitelist.use") && !moderationManager.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (args.length != 1) {
            return;
        }

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            sender.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID uuid = playerArg.getUniqueId();

        if (moderationManager.getWhiteListedPlayers().contains(uuid)) {
            sender.sendMessage(game.translate("werewolf.commands.admin.whitelist.remove"));
            moderationManager.removePlayerOnWhiteList(uuid);
        } else {
            sender.sendMessage(game.translate("werewolf.commands.admin.whitelist.add"));
            moderationManager.addPlayerOnWhiteList(uuid);
            if (game.isState(StateLG.LOBBY)) {
                game.join(playerArg);
            }
        }
    }
}

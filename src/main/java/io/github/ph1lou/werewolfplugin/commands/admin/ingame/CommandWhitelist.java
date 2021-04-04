package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IModerationManager;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWhitelist implements ICommands {

    private final Main main;

    public CommandWhitelist(Main main) {
        this.main = main;
    }


    @Override
    public void execute(Player player, String[] args) {


        GameManager game = (GameManager) main.getWereWolfAPI();
        IModerationManager moderationManager = game.getModerationManager();
        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID uuid = playerArg.getUniqueId();

        if (moderationManager.getWhiteListedPlayers().contains(uuid)) {
            player.sendMessage(game.translate("werewolf.commands.admin.whitelist.remove"));
            moderationManager.removePlayerOnWhiteList(uuid);
        } else {
            player.sendMessage(game.translate("werewolf.commands.admin.whitelist.add"));
            moderationManager.addPlayerOnWhiteList(uuid);
            game.finalJoin(playerArg);
        }
    }
}

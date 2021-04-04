package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandLate implements ICommands {


    private final Main main;

    public CommandLate(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = (GameManager) main.getWereWolfAPI();

        Player player1 = Bukkit.getPlayer(args[0]);

        if (player1 == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID uuid = player1.getUniqueId();

        if (game.getPlayerWW(uuid) != null) {
            player.sendMessage(game.translate("werewolf.commands.late.in_game"));
            return;
        }

        if (game.getModerationManager().getModerators().contains(uuid)) {
            return;
        }

        Bukkit.broadcastMessage(game.translate("werewolf.commands.late.launch", player1.getName()));

        game.addLatePlayer(player1);
    }
}

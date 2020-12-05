package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandLate implements Commands {


    private final Main main;

    public CommandLate(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = (GameManager) main.getWereWolfAPI();


        UUID argUUID = UUID.fromString(args[0]);
        Player player1 = Bukkit.getPlayer(argUUID);

        if (player1 == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        if (game.getPlayersWW().containsKey(argUUID)) {
            player.sendMessage(game.translate("werewolf.commands.late.in_game"));
            return;
        }

        Bukkit.broadcastMessage(game.translate("werewolf.commands.late.launch"));

        game.addLatePlayer(player1);
    }
}

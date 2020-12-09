package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandVote implements Commands {


    private final Main main;

    public CommandVote(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        Player playerArg = Bukkit.getPlayer(args[0]);
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (playerWW1.isState(StatePlayer.DEATH)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        game.getVote().setUnVote(playerWW, playerWW1);
    }
}

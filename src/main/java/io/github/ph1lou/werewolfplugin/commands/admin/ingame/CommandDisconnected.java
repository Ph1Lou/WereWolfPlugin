package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandDisconnected implements Commands {


    private final Main main;

    public CommandDisconnected(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {


        WereWolfAPI game = main.getWereWolfAPI();

        for (PlayerWW playerWW : game.getPlayerWW()) {
            Player player1 = Bukkit.getPlayer(playerWW.getUUID());
            if (playerWW.isState(StatePlayer.ALIVE) && player1 == null) {
                player.sendMessage(game.translate("werewolf.commands.admin.disconnected.send",
                        playerWW.getName(),
                        game.getScore().conversion(game.getScore().getTimer() - playerWW.getDisconnectedTime())));
            }
        }
    }
}

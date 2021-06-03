package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.utils.Utils;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandDisconnected implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {


        for (IPlayerWW playerWW : game.getPlayersWW()) {
            Player player1 = Bukkit.getPlayer(playerWW.getUUID());
            if (playerWW.isState(StatePlayer.ALIVE) && player1 == null) {
                player.sendMessage(game.translate("werewolf.commands.admin.disconnected.send",
                        playerWW.getName(),
                        Utils.conversion(game.getTimer() - playerWW.getDisconnectedTime())));
            }
        }
    }
}

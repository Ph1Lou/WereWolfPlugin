package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandDisconnected implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {


        for (IPlayerWW playerWW : game.getPlayersWW()) {
            Player player1 = Bukkit.getPlayer(playerWW.getUUID());
            if (playerWW.isState(StatePlayer.ALIVE) && player1 == null) {
                player.sendMessage(game.translate("werewolf.commands.admin.disconnected.send",
                        Formatter.player(playerWW.getName()),
                        Formatter.timer(Utils.conversion(game.getTimer() - playerWW.getDisconnectedTime()))));
            }
        }
    }
}

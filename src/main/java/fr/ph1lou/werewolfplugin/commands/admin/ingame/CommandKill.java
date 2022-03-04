package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandKill implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        boolean find = false;

        UUID argUUID = null;
        IPlayerWW playerWW1 = null;

        for (IPlayerWW playerWW : game.getPlayersWW()) {
            if (playerWW.getName().equalsIgnoreCase(args[0])) {
                find = true;
                argUUID = playerWW.getUUID();
                playerWW1 = playerWW;
            }
        }
        if (!find) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.not_in_game_player"));
            return;
        }

        if (!playerWW1.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.commands.kill.not_living"));
            return;
        }
        if (game.isState(StateGame.START)) {
            ((GameManager) game).remove(argUUID);
            player.sendMessage(game.translate(Prefix.ORANGE.getKey() , "werewolf.commands.kill.remove_role"));
            return;
        }
        if (Bukkit.getPlayer(args[0]) != null) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.commands.kill.on_line"));
            return;
        }

        game.death(playerWW1);
    }
}

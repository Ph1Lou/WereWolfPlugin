package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.kill.command",
        descriptionKey = "werewolf.commands.admin.kill.description",
        argNumbers = 1,
        statesGame = {StateGame.START, StateGame.GAME})
public class CommandKill implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IPlayerWW playerWW1 = game.getPlayersWW()
                .stream()
                .filter(iPlayerWW -> iPlayerWW.getName().equalsIgnoreCase(args[0]))
                .findFirst()
                .orElse(null);

        if (playerWW1 == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.not_in_game_player"));
            return;
        }

        if (!playerWW1.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.kill.not_living"));
            return;
        }
        if (game.isState(StateGame.START)) {
            ((GameManager) game).remove(playerWW1.getUUID());
            player.sendMessage(game.translate(Prefix.ORANGE, "werewolf.commands.admin.kill.remove_role"));
            return;
        }
        if (Bukkit.getPlayer(args[0]) != null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.kill.on_line"));
            return;
        }

        game.death(playerWW1);
    }
}

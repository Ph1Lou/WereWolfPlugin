package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@PlayerCommand(key = "werewolf.vote.command",
        descriptionKey = "",
        statesPlayer = StatePlayer.ALIVE,
        statesGame = StateGame.GAME,
        argNumbers = 1)
public class CommandVote implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        Player playerArg = Bukkit.getPlayer(args[0]);
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (playerArg == null) {
            player.sendMessage(game.translate(Prefix.RED , "werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null) {
            player.sendMessage(game.translate(Prefix.RED , "werewolf.check.player_not_found"));
            return;
        }

        if (playerWW1.isState(StatePlayer.DEATH)) {
            player.sendMessage(game.translate(Prefix.RED , "werewolf.check.player_not_found"));
            return;
        }

        game.getVoteManager().setOneVote(playerWW, playerWW1);
    }
}

package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.guis.VoteGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@PlayerCommand(key = "werewolf.commands.player.vote.command",
        descriptionKey = "werewolf.commands.player.vote.description",
        statesPlayer = StatePlayer.ALIVE,
        statesGame = StateGame.GAME,
        argNumbers = {0, 1})
public class CommandVote implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();

        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (game.getConfig().getTimerValue(TimerBase.VOTE_BEGIN) > 0) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.vote.vote_not_yet_activated");
            return;
        }

        if (!game.getConfig().isConfigActive(ConfigBase.VOTE) || game.getVoteManager().isStatus(VoteStatus.ENDED)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.vote.vote_disable");
            return;
        }
        if (!game.getVoteManager().isStatus(VoteStatus.IN_PROGRESS)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.vote.not_vote_time");
            return;
        }

        if (game.getVoteManager().getPlayerVote(playerWW).isPresent()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.vote.already_voted");

            return;
        }

        if(args.length == 0){
            VoteGui.getInventory(player).open(player);
            return;
        }

        Player playerArg = Bukkit.getPlayer(args[0]);


        if (playerArg == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.player_not_found"));
            return;
        }

        if (playerWW1.isState(StatePlayer.DEATH)) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.player_not_found"));
            return;
        }

        game.getVoteManager().setOneVote(playerWW, playerWW1);
    }
}

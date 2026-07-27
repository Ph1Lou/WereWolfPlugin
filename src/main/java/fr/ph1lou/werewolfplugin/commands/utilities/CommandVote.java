package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.guis.VoteGui;
import org.bukkit.entity.Player;

import java.util.UUID;

@PlayerCommand(key = "werewolf.commands.player.vote.command",
        descriptionKey = "werewolf.commands.player.vote.description",
        statesPlayer = StatePlayer.ALIVE,
        statesGame = StateGame.GAME,
        argNumbers = {0, 1},
        args = @Params(players = @PlayerArg(index = 0,
                states = {StatePlayer.ALIVE, StatePlayer.JUDGEMENT}, excludeSelf = true, required = false)))
public class CommandVote implements IParameterizedCommand {

    @Override
    public void execute(WereWolfAPI game, Player player, CommandContext ctx) {

        UUID uuid = player.getUniqueId();

        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (game.getConfig().getTimerValue(TimerBase.VOTE_START) > 0) {
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

        if (!ctx.has(0)) {
            VoteGui.getInventory(player).open(player);
            return;
        }

        IPlayerWW playerWW1 = ctx.player(0);

        game.getVoteManager().setOneVote(playerWW, playerWW1);
    }
}

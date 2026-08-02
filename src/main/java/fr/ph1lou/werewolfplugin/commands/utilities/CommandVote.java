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
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.buildings.UrnVoteManager;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.guis.VoteGui;
import fr.ph1lou.werewolfplugin.roles.villagers.Raven;
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

        // --- Urn-vote mode: only the Raven may vote remotely via /lg vote ---
        if (game.getConfig().isConfigActive(ConfigBase.URN_VOTE)) {

            if (!(playerWW.getRole() instanceof Raven)) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.vote.vote_disable");
                return;
            }

            if (!ctx.has(0)) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.raven.vote_usage");
                return;
            }

            IPlayerWW target = ctx.player(0);
            UrnVoteManager urn = ((GameManager) game).getBuildingsManager().getUrnVoteManager();

            if (!urn.isActive()) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.buildings.urn_vote_not_active");
                return;
            }
            if (urn.hasVotedAgainst(playerWW)) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.vote.already_voted");
                return;
            }

            UrnVoteManager.CastResult result = urn.castVoteAgainst(playerWW, target, null);

            if (result == UrnVoteManager.CastResult.REJECTED) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.vote.vote_disable");
                return;
            }

            if (result == UrnVoteManager.CastResult.COUNTED) {
                playerWW.modifyHonor(1);
            }

            playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.raven.vote_perform",
                    Formatter.player(target.getName()));
            return;
        }

        // --- Classic vote mode ---
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

package fr.ph1lou.werewolfplugin.commands.roles.villager.citizen;

import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.events.roles.seer.SeeVoteEvent;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Citizen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCitizenSeeVote implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        Citizen citizen = (Citizen) playerWW.getRole();

        if (citizen.getUse() >= 2) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.power");
            return;
        }

        if (!game.getVoteManager().isStatus(VoteStatus.WAITING)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.power");
            return;
        }

        citizen.setUse(citizen.getUse() + 1);
        SeeVoteEvent seeVoteEvent = new SeeVoteEvent(playerWW, game.getVoteManager().getVotes());
        Bukkit.getPluginManager().callEvent(seeVoteEvent);

        if (seeVoteEvent.isCancelled()) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.cancel"));
            return;
        }
        player.sendMessage(game.translate(Prefix.GREEN.getKey() , "werewolf.role.citizen.count_votes"));

        game.getVoteManager().getPlayerVotes().forEach((voterWW, voteWW) -> {
            String voterName = voterWW.getName();
            String voteName = voteWW.getName();
            player.sendMessage(game.translate("werewolf.role.citizen.see_vote",
                    Formatter.format("&voter&",voterName),
                    Formatter.player(voteName)));
        });
    }
}

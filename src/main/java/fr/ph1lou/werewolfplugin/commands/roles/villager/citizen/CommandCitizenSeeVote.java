package fr.ph1lou.werewolfplugin.commands.roles.villager.citizen;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.events.roles.citizen.CitizenSeeVoteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Citizen;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.citizen.command_1",
        roleKeys = RoleBase.CITIZEN,
        argNumbers = 0,
        autoCompletion = false)
public class CommandCitizenSeeVote implements ICommandRole {
    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {


        Citizen citizen = (Citizen) playerWW.getRole();
        if (citizen.getUse() >= game.getConfig().getValue(IntValueBase.CITIZEN_SEE_VOTE_NUMBER)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }
        if (!game.getVoteManager().isStatus(VoteStatus.WAITING)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }
        citizen.setUse(citizen.getUse() + 1);
        CitizenSeeVoteEvent seeVoteEvent = new CitizenSeeVoteEvent(playerWW);
        Bukkit.getPluginManager().callEvent(seeVoteEvent);
        if (seeVoteEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }
        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.citizen.count_votes");

        game.getVoteManager().getVoters()
                .forEach((voterWW) -> game.getVoteManager().getPlayerVote(voterWW)
                        .ifPresent(playerWW1 -> playerWW.sendMessageWithKey("werewolf.roles.citizen.see_vote",
                        Formatter.format("&voter&", voterWW.getName()),
                        Formatter.player(playerWW1.getName()))));
    }
}

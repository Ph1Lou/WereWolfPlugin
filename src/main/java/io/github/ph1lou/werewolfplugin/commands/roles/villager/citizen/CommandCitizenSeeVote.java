package io.github.ph1lou.werewolfplugin.commands.roles.villager.citizen;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.VoteStatus;
import io.github.ph1lou.werewolfplugin.roles.villagers.Citizen;
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

        if (!game.getVote().isStatus(VoteStatus.WAITING_CITIZEN)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.power");
            return;
        }

        citizen.setUse(citizen.getUse() + 1);
        game.getVote().seeVote(playerWW);
    }
}

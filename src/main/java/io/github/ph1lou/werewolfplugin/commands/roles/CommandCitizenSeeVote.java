package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.VoteStatus;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.roles.villagers.Citizen;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCitizenSeeVote implements Commands {

    private final Main main;

    public CommandCitizenSeeVote(Main main) {
        this.main = main;
    }


    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Citizen citizen = (Citizen) playerWW.getRole();

        if (citizen.getUse() >= 2) {
            playerWW.sendMessageWithKey("werewolf.check.power");
            return;
        }

        if (!game.getVote().isStatus(VoteStatus.WAITING_CITIZEN)) {
            playerWW.sendMessageWithKey("werewolf.check.power");
            return;
        }

        citizen.setUse(citizen.getUse() + 1);
        game.getVote().seeVote(playerWW);
    }
}

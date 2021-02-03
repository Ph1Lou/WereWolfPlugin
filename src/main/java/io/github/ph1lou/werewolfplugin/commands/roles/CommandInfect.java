package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.InfectionEvent;
import io.github.ph1lou.werewolfapi.events.NewWereWolfEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class CommandInfect implements Commands {


    private final Main main;

    public CommandInfect(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {


        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles infect = playerWW.getRole();

        if(Bukkit.getPlayer(UUID.fromString(args[0]))==null){
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey("werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (!playerWW1.isState(StatePlayer.JUDGEMENT)) {
            playerWW.sendMessageWithKey("werewolf.check.not_in_judgement");
            return;
        }


        Optional<PlayerWW> killerWW = playerWW1.getLastKiller();

        if (!killerWW.isPresent() ||
                !killerWW.get()
                        .getRole()
                        .isWereWolf() ||
                game.getScore().getTimer() - playerWW1.getDeathTime() > 7) {

            playerWW.sendMessageWithKey("werewolf.role.infect_father_of_the_wolves.player_cannot_be_infected");
            return;
        }


        ((Power) infect).setPower(false);

        InfectionEvent infectionEvent = new InfectionEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(infectionEvent);

        if (infectionEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        ((AffectedPlayers) infect).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey("werewolf.role.infect_father_of_the_wolves.infection_perform",
                playerWW1.getName());
        game.resurrection(playerWW1);

        if (!playerWW1.getRole().isWereWolf()) { //si déjà loup
            playerWW1.getRole().setInfected(); //pour qu'il sois actualisé en tan que loup
            Bukkit.getPluginManager().callEvent(
                    new NewWereWolfEvent(playerWW1));
        }

        playerWW1.getRole().setInfected(); //répétition indispensable
        game.checkVictory();
    }
}

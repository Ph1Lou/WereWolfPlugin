package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.infect_father_of_the_wolves.InfectionEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class CommandInfect implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole infect = playerWW.getRole();

        if(Bukkit.getPlayer(UUID.fromString(args[0]))==null){
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

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


        Optional<IPlayerWW> killerWW = playerWW1.getLastKiller();

        if (!killerWW.isPresent() ||
                !killerWW.get()
                        .getRole()
                        .isWereWolf() ||
                game.getTimer() - playerWW1.getDeathTime() > 7) {

            playerWW.sendMessageWithKey("werewolf.role.infect_father_of_the_wolves.player_cannot_be_infected");
            return;
        }


        ((IPower) infect).setPower(false);

        InfectionEvent infectionEvent = new InfectionEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(infectionEvent);

        if (infectionEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) infect).addAffectedPlayer(playerWW1);

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

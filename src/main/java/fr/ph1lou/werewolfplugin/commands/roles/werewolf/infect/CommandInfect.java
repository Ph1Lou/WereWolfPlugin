package fr.ph1lou.werewolfplugin.commands.roles.werewolf.infect;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.infect_father_of_the_wolves.InfectionEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

@RoleCommand(key = "werewolf.roles.infect_father_of_the_wolves.command",
        roleKeys = RoleBase.INFECT,
        requiredPower = true,
        autoCompletion = false,
        argNumbers = 1)
public class CommandInfect implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        UUID uuid = playerWW.getUUID();

        IRole infect = playerWW.getRole();

        if (Bukkit.getPlayer(UUID.fromString(args[0])) == null) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.player_not_found");
            return;
        }

        if (!playerWW1.isState(StatePlayer.JUDGEMENT)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.not_in_judgement");
            return;
        }


        Optional<IPlayerWW> killerWW = playerWW1.getLastKiller();

        if (!killerWW.isPresent() ||
                !killerWW.get()
                        .getRole()
                        .isWereWolf() ||
                game.getTimer() - playerWW1.getDeathTime() > 7) {

            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.infect_father_of_the_wolves.player_cannot_be_infected");
            return;
        }

        ((IPower) infect).setPower(false);

        InfectionEvent infectionEvent = new InfectionEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(infectionEvent);

        if (infectionEvent.isCancelled()) {
            if (!infectionEvent.isInformInfectionCancelledMessage()) return; //ne prévient dans le cas d'une erreur

            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) infect).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.infect_father_of_the_wolves.infection_perform",
                Formatter.player(playerWW1.getName()));
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

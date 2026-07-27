package fr.ph1lou.werewolfplugin.commands.roles.werewolf.infect;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
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

@RoleCommand(key = "werewolf.roles.infect_father_of_the_wolves.command",
        roleKeys = RoleBase.INFECT,
        requiredPower = true,
        autoCompletion = false,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, byUuid = true, excludeSelf = true,
                states = { StatePlayer.JUDGEMENT },
                stateMessageKey = "werewolf.check.not_in_judgement")))
public class CommandInfect implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole infect = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);


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

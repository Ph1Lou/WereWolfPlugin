package fr.ph1lou.werewolfplugin.commands.roles.werewolf.counterfeiter;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.werewolfs.CounterfeiterWereWolf;

@RoleCommand(key = "werewolf.roles.counterfeiter_werewolf.howl_command",
        roleKeys = RoleBase.COUNTERFEITER_WEREWOLF,
        argNumbers = 0,
        statesPlayer = StatePlayer.ALIVE,
        requiredPower = true)
public class CommandCounterfeiterHowl implements IParameterizedCommandRole {

    private static final double HOWLING_RANGE = 50d;

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole role = playerWW.getRole();
        if (!(role instanceof CounterfeiterWereWolf)) {
            return;
        }

        int nearbyWolves = (int) game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(playerWW))
                .filter(p -> p.getRole().isWereWolf())
                .filter(p -> p.distance(playerWW) < HOWLING_RANGE)
                .count();

        game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(playerWW))
                .filter(p -> p.distance(playerWW) < HOWLING_RANGE)
                .forEach(p -> {
                    Sound.WOLF_HOWL.play(p);
                    p.sendMessageWithKey(Prefix.RED, "werewolf.roles.counterfeiter_werewolf.howl_warn");
                });

        ((CounterfeiterWereWolf) role).setPower(false);

        playerWW.sendMessageWithKey(Prefix.GREEN,
                "werewolf.roles.counterfeiter_werewolf.howl_perform",
                Formatter.number(nearbyWolves));
    }
}

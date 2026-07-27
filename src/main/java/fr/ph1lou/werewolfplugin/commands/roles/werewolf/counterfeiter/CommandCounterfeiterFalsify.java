package fr.ph1lou.werewolfplugin.commands.roles.werewolf.counterfeiter;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.werewolfs.CounterfeiterWereWolf;

@RoleCommand(key = "werewolf.roles.counterfeiter_werewolf.falsify_command",
        roleKeys = RoleBase.COUNTERFEITER_WEREWOLF,
        argNumbers = 1,
        statesPlayer = StatePlayer.ALIVE,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true,
                rejectIfAlreadyAffected = true,
                alreadyAffectedMessageKey = "werewolf.roles.counterfeiter_werewolf.already_use_on_player")))
public class CommandCounterfeiterFalsify implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole role = playerWW.getRole();
        if (!(role instanceof CounterfeiterWereWolf)) {
            return;
        }

        IPlayerWW target = ctx.player(0);
        ((CounterfeiterWereWolf) role).falsify(target);
    }
}

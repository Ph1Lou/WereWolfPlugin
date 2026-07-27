package fr.ph1lou.werewolfplugin.commands.roles.neutral.rival;

import fr.ph1lou.werewolfapi.annotations.IntArg;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.neutrals.Rival;

@RoleCommand(key = "werewolf.roles.rival.bonus_command",
        roleKeys = RoleBase.RIVAL,
        argNumbers = 1,
        statesPlayer = StatePlayer.ALIVE,
        args = @Params(integers = @IntArg(index = 0, min = 1, max = 4)))
public class CommandBonus implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {
        IRole role = playerWW.getRole();
        if (!(role instanceof Rival)) return;
        ((Rival) role).bonus(ctx.intValue(0));
    }
}

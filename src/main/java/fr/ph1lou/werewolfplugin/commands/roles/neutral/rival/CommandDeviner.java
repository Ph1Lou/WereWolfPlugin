package fr.ph1lou.werewolfplugin.commands.roles.neutral.rival;

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
import fr.ph1lou.werewolfplugin.roles.neutrals.Rival;

@RoleCommand(key = "werewolf.roles.rival.deviner_command",
        roleKeys = RoleBase.RIVAL,
        argNumbers = 2,
        statesPlayer = StatePlayer.ALIVE,
        args = @Params(players = {
                @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true),
                @PlayerArg(index = 1, states = { StatePlayer.ALIVE }, excludeSelf = true)
        }))
public class CommandDeviner implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {
        IRole role = playerWW.getRole();
        if (!(role instanceof Rival)) return;
        ((Rival) role).deviner(ctx.player(0), ctx.player(1));
    }
}

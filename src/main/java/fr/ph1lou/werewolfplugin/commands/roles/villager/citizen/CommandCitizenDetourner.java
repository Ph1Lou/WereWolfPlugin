package fr.ph1lou.werewolfplugin.commands.roles.villager.citizen;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Citizen;

@RoleCommand(key = "werewolf.roles.citizen.detourner_command",
        roleKeys = RoleBase.CITIZEN,
        requiredPowerKey = Citizen.POWER_REDIRECT,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE, StatePlayer.JUDGEMENT }, excludeSelf = true)))
public class CommandCitizenDetourner implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Citizen citizen = (Citizen) playerWW.getRole();
        IPlayerWW target = ctx.player(0);

        if (!citizen.canRedirectNow()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.citizen.redirect_not_now");
            return;
        }

        citizen.setRedirectTarget(target);

        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.citizen.redirect_set",
                Formatter.player(target.getName()));
    }
}

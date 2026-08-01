package fr.ph1lou.werewolfplugin.commands.roles.werewolf.alerter;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.werewolfs.AlerterWereWolf;

@RoleCommand(key = "werewolf.roles.alerter_werewolf.command",
        roleKeys = RoleBase.ALERTER_WEREWOLF,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE },
                excludeSelf = true, rejectIfAlreadyAffected = true)))
public class CommandAlerter implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        AlerterWereWolf alerter = (AlerterWereWolf) playerWW.getRole();
        IPlayerWW target = ctx.player(0);

        alerter.setPower(false);
        alerter.clearAffectedPlayer();
        alerter.addAffectedPlayer(target);
        alerter.resetProximity();

        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.alerter_werewolf.perform",
                Formatter.player(target.getName()),
                Formatter.number(game.getConfig().getValue(IntValueBase.ALERTER_PROXIMITY_REQUIRED)));
    }
}
